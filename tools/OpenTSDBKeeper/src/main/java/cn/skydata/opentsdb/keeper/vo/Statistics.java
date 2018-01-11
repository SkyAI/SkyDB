package cn.skydata.opentsdb.keeper.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Statistics for OpenTSDB
 */
@Data
@NoArgsConstructor
public class Statistics {
    private List<RegionClientInfo> regionClientInfos;
    private Thread thread;
    private JVM jvm;

    public static Statistics analyze(List<RegionClientInfo> regionClientInfos, List<ThreadInfo> threadInfos, JvmInfo jvmInfo) {
        Statistics statistics = new Statistics();
        statistics.thread = new Thread(threadInfos);
        statistics.jvm = new JVM(jvmInfo);
        statistics.regionClientInfos = regionClientInfos;
        return statistics;
    }

    @Data
    @NoArgsConstructor
    public static class Thread {
        private Integer total;
        private Map<java.lang.Thread.State, Long> state;

        public Thread(List<ThreadInfo> threadInfos) {
            total = threadInfos.size();
            state = threadInfos.parallelStream().collect(Collectors.groupingBy(ThreadInfo::getState, Collectors.counting()));
        }
    }

    @Data
    @NoArgsConstructor
    public static class JVM {
        private double systemLoadAverage;
        /**
         * Unit：kB
         */
        private Long heapUsedSize;
        /**
         * Unit：kB
         */
        private Long nonHeapUsedSize;

        private double heapUsage;

        private double nonHeadUsage;
        /**
         * Eden Space
         */
        private double edenUsage;
        /**
         * Survivor Space
         */
        private double suUsage;
        /**
         * Old Space
         */
        private double oldUsage;


        public JVM(JvmInfo jvmInfo) {
            systemLoadAverage = jvmInfo.getOs().getSystemLoadAverage();
            heapUsedSize = jvmInfo.getMemory().getHeapMemoryUsage().getUsed() / 1024;
            nonHeapUsedSize = jvmInfo.getMemory().getNonHeapMemoryUsage().getUsed() / 1024;

            heapUsage = jvmInfo.getMemory().getHeapMemoryUsage().usage();
            nonHeadUsage = jvmInfo.getMemory().getNonHeapMemoryUsage().usage();

            edenUsage = jvmInfo.getPools().get("pSEdenSpace").getUsage().usage();
            oldUsage = jvmInfo.getPools().get("pSOldGen").getUsage().usage();
            suUsage = jvmInfo.getPools().get("pSSurvivorSpace").getUsage().usage();
        }
    }

    public Integer marks() {
        int initMark = 10000;
        if (jvm != null) {
            initMark -= (jvm.oldUsage - 0.85) * 100;
            initMark -= (jvm.heapUsage - 0.8) * 100;
        }
        if (regionClientInfos != null && regionClientInfos.size() > 0) {
            for (RegionClientInfo regionClientInfo : regionClientInfos) {
                initMark -= regionClientInfo.getPendingBreached();
                initMark -= regionClientInfo.getWritesBlocked();
                initMark -= regionClientInfo.getInflightBreached();
                initMark -= regionClientInfo.getRpcsTimedout();
            }
        }
        return initMark;
    }
}
