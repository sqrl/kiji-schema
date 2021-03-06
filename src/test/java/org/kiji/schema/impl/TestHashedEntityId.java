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

package org.kiji.schema.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.kiji.schema.avro.HashType;
import org.kiji.schema.avro.RowKeyEncoding;
import org.kiji.schema.avro.RowKeyFormat;
import org.kiji.schema.util.ByteArrayFormatter;

/** Tests for HashedEntityId. */
public class TestHashedEntityId {
  @Test
  public void testHashedEntityIdFromKijiRowKey() {
    final RowKeyFormat format = RowKeyFormat.newBuilder()
        .setEncoding(RowKeyEncoding.HASH)
        .setHashType(HashType.MD5)
        .setHashSize(16)
        .build();
    final byte[] kijiRowKey = new byte[] {0x11, 0x22};
    final HashedEntityId eid = HashedEntityId.fromKijiRowKey(kijiRowKey, format);
    assertEquals(format, eid.getFormat());
    assertArrayEquals(kijiRowKey, eid.getKijiRowKey());
    assertEquals(
        "c700ed4fdb1d27055aa3faa2c2432283",
        ByteArrayFormatter.toHex(eid.getHBaseRowKey()));
  }

  @Test
  public void testHashedEntityIdFromHBaseRowKey() throws Exception {
    final RowKeyFormat format = RowKeyFormat.newBuilder()
        .setEncoding(RowKeyEncoding.HASH)
        .setHashType(HashType.MD5)
        .setHashSize(16)
        .build();
    final byte[] hbaseRowKey = ByteArrayFormatter.parseHex("c700ed4fdb1d27055aa3faa2c2432283");
    final HashedEntityId eid = HashedEntityId.fromHBaseRowKey(hbaseRowKey, format);
    assertEquals(format, eid.getFormat());
    assertArrayEquals(hbaseRowKey, eid.getHBaseRowKey());
    assertEquals(null, eid.getKijiRowKey());
  }
}
