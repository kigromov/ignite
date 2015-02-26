/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.client.marshaller.optimized;

import org.apache.ignite.*;
import org.apache.ignite.internal.client.marshaller.*;
import org.apache.ignite.internal.processors.rest.client.message.*;
import org.apache.ignite.marshaller.optimized.*;

import java.io.*;
import java.nio.*;

/**
 * Wrapper, that adapts {@link org.apache.ignite.marshaller.optimized.OptimizedMarshaller} to
 * {@link GridClientMarshaller} interface.
 */
public class GridClientOptimizedMarshaller implements GridClientMarshaller {
    /** ID. */
    public static final byte ID = 1;

    /** Optimized marshaller. */
    private final OptimizedMarshaller opMarsh;

    /**
     * Default constructor.
     */
    public GridClientOptimizedMarshaller() {
        opMarsh = new OptimizedMarshaller();
    }

    /**
     * Constructs optimized marshaller with specific parameters.
     *
     * @param requireSer Require serializable flag.
     * @param poolSize Object streams pool size.
     * @throws IOException If an I/O error occurs while writing stream header.
     * @throws IgniteException If this marshaller is not supported on the current JVM.
     * @see OptimizedMarshaller
     */
    public GridClientOptimizedMarshaller(boolean requireSer, int poolSize) throws IOException {
        opMarsh = new OptimizedMarshaller();

        opMarsh.setRequireSerializable(requireSer);
        opMarsh.setPoolSize(poolSize);
    }

    /** {@inheritDoc} */
    @Override public ByteBuffer marshal(Object obj, int off) throws IOException {
        try {
            if (!(obj instanceof GridClientMessage))
                throw new IOException("Message serialization of given type is not supported: " +
                    obj.getClass().getName());

            byte[] bytes = opMarsh.marshal(obj);

            ByteBuffer buf = ByteBuffer.allocate(off + bytes.length);

            buf.position(off);

            buf.put(bytes);

            buf.flip();

            return buf;
        }
        catch (IgniteCheckedException e) {
            throw new IOException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public <T> T unmarshal(byte[] bytes) throws IOException {
        try {
            return opMarsh.unmarshal(bytes, null);
        }
        catch (IgniteCheckedException e) {
            throw new IOException(e);
        }
    }
}
