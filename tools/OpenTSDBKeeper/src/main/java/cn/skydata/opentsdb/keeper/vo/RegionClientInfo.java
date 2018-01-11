package cn.skydata.opentsdb.keeper.vo;

import lombok.Data;

/**
 * Information about the various HBase region server clients in AsyncHBase. This helps to identify issues with a
 * particular region server. (v2.2)
 *
 * @href http://opentsdb.net/docs/build/html/api_http/stats/region_clients.html
 */
@Data
public class RegionClientInfo {
    private int pendingBreached;
    private int writesBlocked;
    private int inflightBreached;
    private boolean dead;
    private int rpcsInFlight;
    private int rpcsSent;
    private int rpcResponsesUnknown;
    private int pendingBatchedRPCs;
    private String endpoint;
    private int rpcResponsesTimedout;
    private int rpcid;
    private int rpcsTimedout;
    private int pendingRPCs;
}
