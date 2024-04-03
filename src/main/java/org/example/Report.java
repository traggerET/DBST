package org.example;

import javax.swing.*;

/**
 * Encapsulates result calculations. One SR for one TPM currently.
 */
public class Report {
    private static final int REFRESH_PERIOD = 2000;
    private final Timer timer;
    private final transient Object lock = new Object();
    private volatile boolean modified;

    private final MetricsAggregator metricsAggregator = new MetricsAggregator();

    public Report() {

        timer = new Timer(REFRESH_PERIOD, e -> {
            if (!modified) {
                return;
            }
            modified = false;
            synchronized (lock) {
                // TODO: Here write to a file or do smth more general like fire events
            }
        });
        timer.start();
    }

    public void add(final TestResult res) {
        metricsAggregator.addRes(res);
        modified = true;
    }

    /**
     * Should be called to properly close resources.
     */
    public void close() {
        timer.stop();
    }
}
