package cn.skydata.opentsdb.keeper.vo;

import lombok.Data;

import java.util.List;

/**
 * This is used for debugging the TSD and providing insight into the state and execution of various threads without
 * having to resort to a JStack trace.
 *
 * @href http://opentsdb.net/docs/build/html/api_http/stats/threads.html
 */
@Data
public class ThreadInfo {
    private int threadID;
    private int priority;
    private String name;
    private boolean interrupted;
    private Thread.State state;
    private List<String> stack;
}
