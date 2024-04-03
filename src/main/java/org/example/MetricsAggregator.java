package org.example;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class MetricsAggregator {

    private final DoubleAdder sum = new DoubleAdder();

    private final DoubleAdder sqSum = new DoubleAdder();

    private final LongAdder count = new LongAdder();

    private final LongAdder bytes = new LongAdder();

    private final LongAdder sentB = new LongAdder();

    private final AtomicLong max = new AtomicLong();

    private final AtomicLong min = new AtomicLong();

    private final LongAdder errors = new LongAdder();

    private final String label;

    private final AtomicLong startTime = new AtomicLong();

    private final LongAccumulator elapsedTime = new LongAccumulator(Math::max, Long.MIN_VALUE);

    public MetricsAggregator() {
        this("");
    }

    public MetricsAggregator(String label) {
        this.label = label;
        clear();
    }

    public void clear() {
        max.set(Long.MIN_VALUE);
        min.set(Long.MAX_VALUE);
        sum.reset();
        sqSum.reset();
        count.reset();
        bytes.reset();
        sentB.reset();
        errors.reset();
        startTime.set(Long.MAX_VALUE);
        elapsedTime.reset();
    }

    private void addValue(long newValue) {
        count.add(1);
        sum.add((double) newValue);
        long value;
        double extraSumOfSquares;

        value = newValue;
        extraSumOfSquares = (double) newValue * (double) newValue;
        sqSum.add(extraSumOfSquares);

        long currentMinimum = min.get();
        if (currentMinimum > value) {
            min.accumulateAndGet(value, Math::min);
        }
        long currentMaximum = max.get();
        if (currentMaximum < value) {
            max.accumulateAndGet(value, Math::max);
        }
    }

    public void addRes(TestResult res) {
        addB(res.getBytes());
        addValue(res.getTime());
        errors.add(res.getErrorCount());
        long testStarted = startTime.get();
        if (res.getStartTime() < testStarted) {
            testStarted = startTime.accumulateAndGet(res.getStartTime(), Math::min);
        }
        elapsedTime.accumulate(res.getEndTime() - testStarted);
    }

    public void addB(long newValue) {
        bytes.add(newValue);
    }

    private void addSentB(long value) {
        sentB.add(value);
    }

    public long getTotalB() {
        return bytes.sum();
    }

    public double getMean() {
        double sum = this.sum.sum();
        double count = this.count.sum();
        if (count == 0) {
            return 0.0;
        }
        return sum / count;
    }


    public double getDev() {
        double sum = this.sum.sum();
        double sqSum = this.sqSum.sum();
        double count = this.count.sum();
        if (count == 0) {
            return 0.0;
        }
        double mean = sum / count;
        return Math.sqrt((sqSum / count) - (mean * mean));
    }

    public long getMin() {
        return min.get();
    }

    public long getMax() {
        return max.get();
    }

    public long getCount() {
        return count.sum();
    }

    public String getLabel() {
        return label;
    }

    public double getErrorPercentage() {
        long count = this.count.sum();
        if (count == 0) {
            return 0.0;
        }
        return errors.sum() / (double) count;
    }

    public double getThroughput() {
        return throughputPerSecond(count.sum());
    }

    public double getBytes() {
        long count = this.count.sum();
        long bytes = this.bytes.sum();
        if (count > 0 && bytes > 0) {
            return ((double) bytes) / count;
        }
        return 0.0;
    }

    public double getBPerSecond() {
        return throughputPerSecond(bytes.sum());
    }

    public double getKBPerSecond() {
        return getBPerSecond() / 1024;
    }

    public double getSentBPerSecond() {
        return throughputPerSecond(sentB.sum());
    }

    public double getSentKBPerSecond() {
        return getSentBPerSecond() / 1024;
    }

    private double throughputPerSecond(long value) {
        long elapsed = this.elapsedTime.get();
        if (elapsed > 0) {
            return value / ((double) elapsed / 1000);
        }
        return 0.0;
    }
}
