package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * High-level thing that will run the entire test.
 */
public class TesterEngine {

    private final String EXECUTE_SECUENTIALLY = "sequential";

    private final BaseConfig config;

    private List<ThreadPerformersManager> mgrs = new ArrayList<>();

    public TesterEngine(BaseConfig config) {
        this.config = config;
    }

    public void run() {
        // here the entire test is run
        boolean wait = EXECUTE_SECUENTIALLY.equals(config.execType);
        ThreadPerformersManager prevMgr = null;
        for (int i = 0; i < config.testsToRun.size(); i += 1) {
            if (wait && prevMgr != null) {
                prevMgr.waitStopped();
                // TODO: wait
            }
            TestSettings test = config.testsToRun.get(i);
            ThreadPerformersManager mgr = new ThreadPerformersManager(test);
            mgrs.add(mgr);
            mgr.setGroupName(test.testName);
            startThreadPerformersManager(mgr);
            prevMgr = mgr;
        }
        waitThreadPerformersManagersStopped();
    }

    private void startThreadPerformersManager(ThreadPerformersManager mgr) {
        mgr.start();
    }

    private void waitThreadPerformersManagersStopped() {
        for (ThreadPerformersManager mgr : mgrs) {
            mgr.waitStopped();
        }
    }
}
