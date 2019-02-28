/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.misc;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryingExecutor implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryingExecutor.class);
    private static final int DEFAULT_RETRY_DELAY = 10000;
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final Timer timer = new Timer();
    private final String name;
    private volatile Duration retryDelay = Duration.ofMillis(DEFAULT_RETRY_DELAY);
    private volatile Thread thread;
    private volatile Runnable command;

    public RetryingExecutor() {
        this.name = "retry";
    }

    public RetryingExecutor(String name) {
        this.name = name;
    }

    public RetryingExecutor(String name, Duration retryDelay) {
        this.name = name;
        this.retryDelay = retryDelay;
    }

    public Duration getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Duration retryDelay) {
        this.retryDelay = retryDelay;
    }

    public synchronized void start() {
        if (!active.getAndSet(true)) {
            startThread();
        }
    }

    public synchronized void stop() {
        if (active.getAndSet(false)) {
            timer.cancel();
            thread.interrupt();
        }
    }

    public synchronized void execute(Runnable command) {
        if (active.get()) {
            stop();
            this.command = command;
            start();
        } else {
            this.command = command;
        }
    }

    private void startThread() {
        thread = new Thread(() -> {
            LOGGER.debug("Retry executor {} is starting", getName());

            while (active.get()) {
                try {
                    command.run();
                } catch (Exception e) {
                    LOGGER.error("An exception occurred during {} command execution", getName(), e);
                    break;
                }
            }

            if (active.get()) {
                LOGGER.warn("Interruption of {} command execution. A retry is scheduled in {} ms",
                        getName(),
                        retryDelay.toMillis());
                try {
                    timer.schedule(new MyTimerTask(), retryDelay.toMillis());
                } catch (Exception e) {
                    LOGGER.error("Unable to schedule a retry of {} command execution", getName(), e);
                }
            } else {
                LOGGER.debug("Retry executor {} is stopping", getName());
            }
        });
        thread.setName(getName());
        thread.start();
    }

    private String getName() {
        return name + "-" + thread.getId();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            synchronized (RetryingExecutor.this) {
                if (!thread.isAlive()) {
                    startThread();
                } else {
                    timer.schedule(new MyTimerTask(), retryDelay.toMillis());
                }
            }
        }
    }
}
