package cn.skydata.opentsdb.keeper.component;

import cn.skydata.opentsdb.keeper.vo.OpenTSDB;
import cn.skydata.opentsdb.keeper.connect.SystemClient;
import cn.skydata.opentsdb.keeper.connect.impl.SystemClientImpl;
import cn.skydata.opentsdb.keeper.service.MailService;
import cn.skydata.opentsdb.keeper.vo.JvmInfo;
import cn.skydata.opentsdb.keeper.vo.RegionClientInfo;
import cn.skydata.opentsdb.keeper.vo.ThreadInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Pool implements InitializingBean {

    @Value("${opentsdb.url}")
    private String[] urls;

    private List<OpenTSDB> tsd;
    private ConcurrentHashMap<String, OpenTSDB> assignedTable;

    @Autowired
    private MailService mailService;

    @Autowired
    private OpenTSDBExceptionKeeper openTSDBExceptionKeeper;

    public String apply(String ip) {
        OpenTSDB old = assignedTable.get(ip);

        if (Objects.nonNull(old)) {
            if (old.isAlive())
                return old.getUrl();

            old.setUsed(false);
            log.info("{} disconnect to {}", ip, old.getUrl());
        }

        OpenTSDB openTSDB = tsd.parallelStream()
                .filter(OpenTSDB::nonUsed)
                .filter(OpenTSDB::isAlive)
                .sorted(Comparator.comparingDouble(OpenTSDB::healthMark).reversed())
                .findFirst()
                .orElseGet(() -> tsd.parallelStream().filter(OpenTSDB::isAlive).findAny().orElseThrow(openTSDBExceptionKeeper::nonAliveOpenTSDBException));
        if (openTSDB.nonUsed()) {
            assignedTable.put(ip, openTSDB);
            openTSDB.setUsed(true);
        }

        log.info("{} now connect to {}", ip, openTSDB.getUrl());
        return openTSDB.getUrl();
    }

    /**
     * Update OpenTSDB state at a fixed rate
     */
    private void refreshState() {
        Runnable updateTask = () -> {
            log.info("Checking for OpenTSDB");
            tsd.parallelStream().forEach(openTSDB -> {
                openTSDB.updateCheckDeadline();

                SystemClient openTSDBClient = new SystemClientImpl(openTSDB.getUrl());
                Optional<List<RegionClientInfo>> regionClientInfoList = openTSDBClient.checkRegionClientInfo();
                Optional<List<ThreadInfo>> threadInfoList = openTSDBClient.checkThreadInfo();
                Optional<JvmInfo> jvmInfoList = openTSDBClient.checkJvmInfo();

                if (regionClientInfoList.isPresent() && threadInfoList.isPresent() && jvmInfoList.isPresent()) {
                    regionClientInfoList.ifPresent(openTSDB::setRegionClientInfoList);
                    threadInfoList.ifPresent(openTSDB::setThreadInfoList);
                    jvmInfoList.ifPresent(openTSDB::setJvmInfo);

                    log.info("OpenTSDB Client [{}] works well", openTSDB.getUrl());
                    openTSDB.resetCheckDeadline();
                }

                if (openTSDB.isDead()) {
                    String content = "OpenTSDB Client [" + openTSDB.getUrl() + "] dead.";
                    mailService.sendMail("OpenTSDB Warning", content);
                    log.error(content);
                }
            });
        };
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(updateTask, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assignedTable = new ConcurrentHashMap<>();
        tsd = Arrays.stream(urls).map(OpenTSDB::new).collect(Collectors.toList());
        refreshState();
    }

    public List<OpenTSDB> getTsd() {
        return tsd;
    }

    public Map<String, String> showAssignedTable() {
        Map<String, String> assigned = new HashMap<>();
        assignedTable.forEach((key, value) -> {
            assigned.put(key, value.getUrl());
        });
        return assigned;
    }
}
