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

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kiji.schema.layout.KijiTableLayoutDatabase;

/**
 * The kiji metadata table, which stores layouts and other user defined meta data on a per-table
 * basis.
 *
 * @see KijiSchemaTable
 * @see KijiSystemTable
 */
public abstract class KijiMetaTable implements Closeable, KijiTableLayoutDatabase,
    KijiTableKeyValueDatabase {
  private static final Logger LOG = LoggerFactory.getLogger(KijiMetaTable.class);
  /**
   * Whether the table is open.
   */
  private boolean mIsOpen;

  /**
   * Creates a new <code>KijiMetaTable</code> instance.
   */
  protected KijiMetaTable() {
    mIsOpen = true;
  }

  /**
   * Remove all metadata, including layouts, for a particular table.
   *
   * @param table The name of the kiji table to delete.
   * @throws IOException If there is an error.
   */
  public abstract void deleteTable(String table) throws IOException;

  /** {@inheritDoc} */
  @Override
  public void close() throws IOException {
    if (!mIsOpen) {
      LOG.warn("close() called on a KijiMetaTable that was already closed.");
      return;
    }
    mIsOpen = false;
  }

  /** {@inheritDoc} */
  @Override
  protected void finalize() throws Throwable {
    if (mIsOpen) {
      LOG.warn("Closing KijiMetaTable in finalize(). You should close it explicitly");
      close();
    }
    super.finalize();
  }

}
