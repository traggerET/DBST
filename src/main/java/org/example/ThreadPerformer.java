package org.example;

import java.util.concurrent.TimeUnit;

/**
 * CLass responsible for running N queries sequentially in one thread.
 */
public class ThreadPerformer implements Runnable {

    private final QueryInfo queryInfo;
    private final int queryTimeout;
    int initialDelay;
    private String threadName;
    private int threadNumber;

    private int repeats = 1;

    private DBSTConnection dsce;

    public ThreadPerformer(DBSTConnection dsce, QueryInfo queryInfo, int queryTimeout) {
        this.dsce = dsce;
        this.queryInfo = queryInfo;
        this.queryTimeout = queryTimeout;
    }

    @Override
    public void run() {
        initRun();
        for (int i = 0; i < repeats; i++) {
            String freshQuery = ParamsReplacerUtils.generateAndReplace(queryInfo.query, queryInfo.params, threadNumber);
            QueryExecutor executor = new QueryExecutor(dsce, freshQuery, queryInfo.queryType, queryTimeout);
            processExec(executor);
            TestResult res = executor.exec();
            // TODO: send res to subscribers/listeners
        }
    }

    private void initRun() {
        delay(initialDelay);
    }

    private void processExec(QueryExecutor exe) {
        TestResult result = exec(exe);

        if (result != null)
        {
            // TODO: If we got any results, then perform processing on the result
        }
    }

    private TestResult exec(QueryExecutor queryExecutor) {
        return queryExecutor.exec();
    }

    public void setInitDelay(int delay) {
        initialDelay = delay;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setThreadNum(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public String getThreadName() {
        return threadName;
    }

    protected final void delay(long delay) {
        if (delay > 0) {
            long begin = System.currentTimeMillis();
            long finish = begin + delay;
            long curr;
            long pause = 1000;
            while ((curr = System.currentTimeMillis()) < finish) {
                long togo = finish - curr;
                if (togo < pause) {
                    pause = togo;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(pause); // delay between checks
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
