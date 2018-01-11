package cn.skydata.opentsdb.keeper.connect;

import cn.skydata.opentsdb.keeper.vo.JvmInfo;
import cn.skydata.opentsdb.keeper.vo.RegionClientInfo;
import cn.skydata.opentsdb.keeper.vo.ThreadInfo;

import java.util.List;
import java.util.Optional;

/**
 * Client to OpenTSDB, providing system info
 */
public interface SystemClient {
    /**
     * Check for HBase Client
     *
     * @return
     */
    Optional<List<RegionClientInfo>> checkRegionClientInfo();

    /**
     * Check for Threads
     *
     * @return
     */
    Optional<List<ThreadInfo>> checkThreadInfo();

    /**
     * Check for JVM
     *
     * @return
     */
    Optional<JvmInfo> checkJvmInfo();
}
