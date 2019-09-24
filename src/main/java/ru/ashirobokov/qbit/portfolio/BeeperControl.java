package ru.ashirobokov.qbit.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BeeperControl {

    private final Logger LOG = LoggerFactory.getLogger(BeeperControl.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void beepForMinute(CountDownLatch latch) {
        LOG.info(".....beepForMinute...scheduler started");
        final Runnable beeper = new Runnable() {
            public void run() { LOG.info("beep :-)"); }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 0, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
                latch.countDown();
            }
        }, 60 * 1, SECONDS);
    }

    public void stopScheduler() {
        LOG.info("...../scheduler stopped");
        scheduler.shutdown();
    }

    public void stopTasks() {
        LOG.info(".....stopTasks...");
        if (!scheduler.isTerminated()) {
            List<Runnable> tasks = scheduler.shutdownNow();
            if (null != tasks) {
                LOG.info(".......stopTasks : {} tasks were awaiting", tasks.stream().count());
                tasks.forEach(task -> {
                    LOG.info("..........awaiting task {}", task.toString());
                });
            }
        } else {
            LOG.info(".....stopTasks : all tasks stopped");
        }
        LOG.info("...../stopTasks");
    }
}
