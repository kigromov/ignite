/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.visor.debug;

import org.apache.ignite.lang.*;
import org.gridgain.grid.*;
import org.gridgain.grid.kernal.processors.task.*;
import org.gridgain.grid.kernal.visor.*;
import org.gridgain.grid.util.typedef.internal.*;

import java.lang.management.*;

/**
 * Creates thread dump.
 */
@GridInternal
public class VisorThreadDumpTask extends VisorOneNodeTask<Void, IgniteBiTuple<VisorThreadInfo[], long[]>> {
    /** */
    private static final long serialVersionUID = 0L;

    /** {@inheritDoc} */
    @Override protected VisorDumpThreadJob job(Void arg) {
        return new VisorDumpThreadJob(arg, debug);
    }

    /**
     * Job that take thread dump on node.
     */
    private static class VisorDumpThreadJob extends VisorJob<Void, IgniteBiTuple<VisorThreadInfo[], long[]>> {
        /** */
        private static final long serialVersionUID = 0L;

        /**
         * @param arg Formal job argument.
         * @param debug Debug flag.
         */
        private VisorDumpThreadJob(Void arg, boolean debug) {
            super(arg, debug);
        }

        /** {@inheritDoc} */
        @Override protected IgniteBiTuple<VisorThreadInfo[], long[]> run(Void arg) throws GridException {
            ThreadMXBean mx = U.getThreadMx();

            ThreadInfo[] info = mx.dumpAllThreads(true, true);

            VisorThreadInfo[] visorInfo = new VisorThreadInfo[info.length];

            for (int i = 0; i < info.length; i++)
                visorInfo[i] = VisorThreadInfo.from(info[i]);

            return new IgniteBiTuple<>(visorInfo, mx.findDeadlockedThreads());
        }

        /** {@inheritDoc} */
        @Override public String toString() {
            return S.toString(VisorDumpThreadJob.class, this);
        }
    }
}
