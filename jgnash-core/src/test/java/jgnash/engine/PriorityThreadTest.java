/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2018 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import jgnash.engine.concurrent.Priority;
import jgnash.engine.concurrent.PriorityThreadPoolExecutor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Craig Cavanaugh
 */
class PriorityThreadTest {

    @Test
    void testPriorityThreadPoolExecutor() throws Exception {

        final AtomicLong atomicLongSequence = new AtomicLong(0);

        final PriorityThreadPoolExecutor executorService = new PriorityThreadPoolExecutor();

        final List<Future> futures = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            Future future = executorService.submit(() -> {
                final long value = atomicLongSequence.incrementAndGet();

                Thread.sleep(500);
                System.out.println("Background Callable: " + value);
                return null;
            }, Priority.BACKGROUND);

            futures.add(future);
        }

        Thread.sleep(1999);

        final Future priorityFuture = executorService.submit(() -> {
            final long value = atomicLongSequence.incrementAndGet();

            Thread.sleep(500);
            System.out.println("System Callable: " + value);

            assertEquals(5, value);
            return null;
        });

        futures.add(priorityFuture);

        // wait for futures to complete
        for (Future future : futures) {
            future.get();
        }

        executorService.shutdown();

        executorService.shutdownNow();
    }
}
