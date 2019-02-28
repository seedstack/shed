/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.misc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class RetryingExecutorTest {
    private RetryingExecutor executor = new RetryingExecutor("test", Duration.ofMillis(100));
    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testExecution() throws InterruptedException {
        executor.execute(latch::countDown);
        try {
            executor.start();
            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        } finally {
            executor.stop();
        }
    }

    @Test
    public void testRetry() throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        executor.execute(() -> {
            count.incrementAndGet();
            throw new RuntimeException("");
        });
        try {
            executor.start();
            assertThat(latch.await(200, TimeUnit.MILLISECONDS)).isFalse();
            assertThat(count.get()).isGreaterThan(1);
        } finally {
            executor.stop();
        }
    }
}
