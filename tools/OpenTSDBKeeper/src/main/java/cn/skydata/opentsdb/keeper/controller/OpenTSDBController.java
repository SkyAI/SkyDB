package cn.skydata.opentsdb.keeper.controller;

import cn.skydata.opentsdb.keeper.vo.OpenTSDB;
import cn.skydata.opentsdb.keeper.component.Pool;
import cn.skydata.opentsdb.keeper.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/opentsdb")
public class OpenTSDBController {

    @Autowired
    private Pool pool;

    @RequestMapping(value = "/apply")
    public String applyForOpenTSDB(HttpServletRequest request) {
        String ip = getIpAddr(request);
        return pool.apply(ip);
    }

    /**
     * Check for statistics of all your OpenTSDB
     */
    @RequestMapping(value = "/state")
    public List<StatisticsVO> state() {
        return pool.getTsd().parallelStream().map(OpenTSDB::toStatistics).collect(Collectors.toList());
    }

    /**
     * Check for raw state info of all your OpenTSDB
     */
    @RequestMapping(value = "/state/raw")
    public List<OpenTSDB> rawState() {
        return pool.getTsd();
    }

    /**
     * Check for your pair of OpenTSDB and Consumer
     */
    @RequestMapping(value = "/state/assigned")
    public Map<String, String> assignedTable() {
        return pool.showAssignedTable();
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
