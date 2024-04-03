package org.example;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages threads to be run in a single group
 */
public class ThreadPerformersManager {
    private static final long DIE_TIME = 5000;
    boolean running;
    private TestSettings settings;

    private String groupName;

    private final ConcurrentHashMap<ThreadPerformer, Thread> threads = new ConcurrentHashMap<>();

    public ThreadPerformersManager(TestSettings settings) {
        this.settings = settings;
    }

    public void start() {
        running = true;
        // init and start threads
        long lastStart = 0;
        int delayFor = 0;

        final int uniformDelay = Math.round((float) settings.startUpTime * 1000 / settings.connNum);
        for (int tNum = 0; running && tNum < settings.connNum; tNum++) {
            long currTime = System.currentTimeMillis();
            if(tNum > 0) {
                long fromLast = currTime - lastStart;
                delayFor = (int) (delayFor + (uniformDelay - fromLast));
            }

            lastStart = currTime;
            startNewThread(tNum, Math.max(0, delayFor));
        }
    }

    private ThreadPerformer startNewThread(int threadNum, int delay) {
        ThreadPerformer threadPerformer = mkThread(threadNum);
        threadPerformer.setInitDelay(delay);
        Thread newThread = new Thread(threadPerformer, threadPerformer.getThreadName());
        threads.put(threadPerformer, newThread);
        newThread.start();
        return threadPerformer;
    }

    protected ThreadPerformer mkThread(int threadNumber) {
        DBSTConnection conn = new DBSTConnection();
        conn.setDriverName(settings.dbDriverName);
        conn.setPassword(settings.password);
        conn.setUsername(settings.username);
        conn.setDbUrl(settings.dbUrl);

        final ThreadPerformer threadPerformer = new ThreadPerformer(conn, settings.queryCfg, settings.queryTimeout);
        threadPerformer.setThreadNum(threadNumber);
        threadPerformer.setRepeats(settings.repeat);
        final String threadName = groupName + "-" + (threadNumber + 1);
        threadPerformer.setThreadName(threadName);
        return threadPerformer;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void waitStopped() {
        while (!threads.isEmpty()) {
            threads.values().forEach(ThreadPerformersManager::waitStopped);
        }
    }

    private static void waitStopped(Thread thread) {
        if (thread == null) {
            return;
        }
        while (thread.isAlive()) {
            try {
                thread.join(DIE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
