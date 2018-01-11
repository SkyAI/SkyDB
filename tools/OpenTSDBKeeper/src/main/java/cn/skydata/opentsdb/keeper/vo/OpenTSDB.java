package cn.skydata.opentsdb.keeper.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenTSDB {
    private String url;
    private List<RegionClientInfo> regionClientInfoList;
    private List<ThreadInfo> threadInfoList;
    private JvmInfo jvmInfo;

    private int checkDeadline = 2;
    private boolean used;

    public OpenTSDB(String url) {
        this.url = url;
    }


    public boolean isAlive() {
        return checkDeadline > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    public boolean healthy() {
        return true;
    }

    public double healthMark() {
        return Statistics.analyze(regionClientInfoList, threadInfoList, jvmInfo).marks();
    }

    public boolean nonHealthy() {
        return !healthy();
    }

    public boolean nonUsed() {
        return !isUsed();
    }

    public void updateCheckDeadline() {
        this.checkDeadline--;
    }

    public void resetCheckDeadline() {
        this.checkDeadline = 3;
    }

    public StatisticsVO toStatistics() {
        return new StatisticsVO(this.url, regionClientInfoList, threadInfoList, jvmInfo);
    }

}
