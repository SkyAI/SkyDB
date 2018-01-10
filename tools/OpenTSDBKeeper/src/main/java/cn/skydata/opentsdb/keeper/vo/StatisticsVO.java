package cn.skydata.opentsdb.keeper.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StatisticsVO {
    private String url;
    private double marks;
    private Statistics statistics;

    public StatisticsVO(String url, List<RegionClientInfo> regionClientInfos, List<ThreadInfo> threadInfos, JvmInfo jvmInfo) {
        this.url = url;
        this.statistics = Statistics.analyze(regionClientInfos, threadInfos, jvmInfo);
        this.marks = this.statistics.marks();
    }
}
