/**
 * (c) Copyright 2012 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiji.schema;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kiji.schema.KijiTableReader.KijiScannerOptions;
import org.kiji.schema.layout.KijiTableLayout;
import org.kiji.schema.layout.KijiTableLayouts;
import org.kiji.schema.util.InstanceBuilder;
import org.kiji.schema.util.ResourceUtils;

public class TestKijiRowScanner {
  private Kiji mKiji;
  private KijiTable mTable;
  private KijiTableReader mReader;

  @Before
  public void setupEnvironment() throws Exception {
    // Get the test table layouts.
    final KijiTableLayout layout = KijiTableLayout.newLayout(
        KijiTableLayouts.getLayout(KijiTableLayouts.SIMPLE_UNHASHED));
    // Populate the environment.
    mKiji = new InstanceBuilder()
        .withTable("table", layout)
            .withRow("a")
                .withFamily("family")
                    .withQualifier("column").withValue(1L, "foo-val")
            .withRow("c")
                .withFamily("family")
                    .withQualifier("column").withValue(1L, "bar-val")
        .build();

    // Fill local variables.
    mTable = mKiji.openTable("table");
    mReader = mTable.openTableReader();
  }

  @After
  public void cleanupEnvironment() throws IOException {
    ResourceUtils.closeOrLog(mReader);
    ResourceUtils.releaseOrLog(mTable);
    ResourceUtils.releaseOrLog(mKiji);
  }

  @Test
  public void testScanner() throws Exception {
    final KijiDataRequest request = KijiDataRequest.create("family", "column");
    final KijiRowScanner scanner = mReader.getScanner(request);
    final Iterator<KijiRowData> iterator = scanner.iterator();

    final String actual1 = iterator.next().getValue("family", "column", 1L).toString();
    final String actual2 = iterator.next().getValue("family", "column", 1L).toString();

    assertEquals("foo-val", actual1);
    assertEquals("bar-val", actual2);

    ResourceUtils.closeOrLog(scanner);
  }

  @Test
  public void testScannerOptionsStart() throws Exception {
    final KijiDataRequest request = KijiDataRequest.create("family", "column");

    // Test with a scanner that starts directly on a value.
    EntityId startRow = mTable.getEntityId("c");
    KijiRowScanner scanner = mReader.getScanner(
        request, new KijiScannerOptions().setStartRow(startRow));
    Iterator<KijiRowData> iterator = scanner.iterator();

    String first = iterator.next().getValue("family", "column", 1L).toString();
    assertEquals("bar-val", first);
    ResourceUtils.closeOrLog(scanner);

    // Test with a scanner that starts between the rows.
    startRow = mTable.getEntityId("b");
    scanner = mReader.getScanner(request, new KijiScannerOptions().setStartRow(startRow));
    iterator = scanner.iterator();

    first = iterator.next().getValue("family", "column", 1L).toString();
    assertEquals("bar-val", first);
    ResourceUtils.closeOrLog(scanner);
  }
}
