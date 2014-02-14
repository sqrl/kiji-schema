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

package org.kiji.schema.platform;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.Compression;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kiji.annotations.ApiAudience;

/**
 * Hadoop 1.x and HBase 0.94.x-backed implementation of the SchemaPlatformBridge API.
 */
@ApiAudience.Private
public final class Hadoop1xHBase94SchemaBridge extends SchemaPlatformBridge {
  private static final Logger LOG = LoggerFactory.getLogger(Hadoop1xHBase94SchemaBridge.class);

  /** {@inheritDoc} */
  @Override
  public void initializeHadoopResources() {
    // Do nothing: Configuration resources include hdfs/mapred-site/default.xml by default.
  }

  /** {@inheritDoc} */
  @Override
  public void setAutoFlush(HTableInterface hTable, boolean autoFlush) {
    // setAutoFlush is added to HTableInterface in HBase 0.94.2
    hTable.setAutoFlush(autoFlush);
  }

  /** {@inheritDoc} */
  @Override
  public void setWriteBufferSize(HTableInterface hTable, long bufSize)
      throws IOException {
    // setWriteBufferSize is added to HTableInterface in HBase 0.94.2
    hTable.setWriteBufferSize(bufSize);
  }

  /** {@inheritDoc} */
  @Override
  public HFile.Writer createHFileWriter(Configuration conf,
      FileSystem fs, Path path, int blockSizeBytes, String compressionType)
      throws IOException {

     return HFile.getWriterFactory(conf, new CacheConfig(conf))
         .withPath(fs, path)
         .withBlockSize(blockSizeBytes)
         .withCompression(Compression.getCompressionAlgorithmByName(compressionType))
         .withComparator(KeyValue.KEY_COMPARATOR)
         .create();
  }

  /** {@inheritDoc} */
  @Override
  public HColumnDescriptorBuilderInterface createHColumnDescriptorBuilder() {
    return new HColumnDescriptorBuilder();
  }

  /** {@inheritDoc} */
  @Override
  public HColumnDescriptorBuilderInterface createHColumnDescriptorBuilder(byte[] family) {
    return new HColumnDescriptorBuilder(family);
  }

  /**
   * Platform-specific implementation of HColumnDescriptorBuilderInterface.
   */
  public static class HColumnDescriptorBuilder implements HColumnDescriptorBuilderInterface {
    private HColumnDescriptor mHColumnDescriptor;

    /**
     * Construct a new HColumnDescriptorBuilder.
     */
    HColumnDescriptorBuilder() {
      mHColumnDescriptor = new HColumnDescriptor();
    }

    /**
     * Construct a new HColumnDescriptorBuilder with the family specified for the
     * HColumnDescriptor.
     *
     * @param family Family for the HColumnDescriptor.
     */
    HColumnDescriptorBuilder(byte[] family) {
      mHColumnDescriptor = new HColumnDescriptor(family);
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setMaxVersions(int maxVersions) {
      mHColumnDescriptor.setMaxVersions(maxVersions);
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setCompressionType(
        String compressionAlgorithm) {
      mHColumnDescriptor.setCompressionType(
          Compression.getCompressionAlgorithmByName(compressionAlgorithm));
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setInMemory(boolean inMemory) {
      mHColumnDescriptor.setInMemory(inMemory);
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setBlockCacheEnabled(boolean blockCacheEnabled) {
      mHColumnDescriptor.setBlockCacheEnabled(blockCacheEnabled);
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setTimeToLive(int timeToLive) {
      mHColumnDescriptor.setTimeToLive(timeToLive);
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptorBuilderInterface setBloomType(String bloomType) {
      mHColumnDescriptor.setBloomFilterType(StoreFile.BloomType.valueOf(bloomType));
      return this;
    }

    /** {@inheritDoc} */
    @Override
    public HColumnDescriptor build() {
      return new HColumnDescriptor(mHColumnDescriptor);
    }
  }
}

