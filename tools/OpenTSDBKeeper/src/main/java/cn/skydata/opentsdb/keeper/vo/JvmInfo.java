package cn.skydata.opentsdb.keeper.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @href http://opentsdb.net/docs/build/html/api_http/stats/jvm.html
 */
@Data
public class JvmInfo {

    private OS os;
    private JSONObject gc;
    private Runtime runtime;
    private Map<String, MemoryItem> pools;
    private Memory memory;


    @Data
    public static class OS {
        private double systemLoadAverage;
    }

    @Data
    public static class Memory {
        private long objectsPendingFinalization;
        private MemoryUsage nonHeapMemoryUsage;
        private MemoryUsage heapMemoryUsage;
    }

    @Data
    public static class Runtime {
        private String vmName;
        private String vmVersion;
        private long startTime;
        private long updateTime;
        private String vmVendor;
    }


    @Data
    public static class MemoryItem {
        private MemoryUsage collectionUsage;
        private MemoryUsage peakUsage;
        private MemoryUsage usage;
        private String type;
    }

    @Data
    @NoArgsConstructor
    public static class MemoryUsage {
        private long init;
        private long used;
        private long committed;
        private long max;

        public double usage() {
            return (double) used / (double) committed;
        }
    }
}
