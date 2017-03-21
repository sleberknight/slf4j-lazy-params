package com.fortitudetec.slf4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fortitudetec.slf4j.Slf4jParameterSupplier.delayed;
import static com.fortitudetec.slf4j.Slf4jParameterSupplier.lazy;
import static org.awaitility.Awaitility.await;

public class Slf4jParameterSupplierTest {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jParameterSupplierTest.class);

    @Rule
    public TestName testName = new TestName();

    private AtomicBoolean completionFlag;

    @Before
    public void setUp() {
        LOG.info("Starting test {}", testName.getMethodName());
        completionFlag = new AtomicBoolean(false);
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive() {
        Executors.newSingleThreadExecutor().submit(logAtInfoLevel());
        await().atMost(1100, TimeUnit.MILLISECONDS).until(completed());
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive_AndSupplierReturnsNull() {
        Executors.newSingleThreadExecutor().submit(logAtInfoLevelWithNullReturned());
        await().atMost(1100, TimeUnit.MILLISECONDS).until(completed());
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive_AndSupplierReturnsComplexObject() {
        Executors.newSingleThreadExecutor().submit(logAtInfoLevelWithComplexObjectReturned());
        await().atMost(1100, TimeUnit.MILLISECONDS).until(completed());
    }

    @Test
    public void testDoesNotExecuteSupplier_WhenLogLevel_IsNotActive() {
        Executors.newSingleThreadExecutor().submit(logAtTraceLevel());
        await().atMost(150, TimeUnit.MILLISECONDS).until(completed());
    }

    private Runnable logAtInfoLevel() {
        return () -> {
            LOG.info("Before log of expensive computation at info level...");
            LOG.info("The result at {} level is {}", "info", delayed(this::expensiveToCreateString));
            LOG.info("After log of expensive computation at info level...");
            completionFlag.compareAndSet(false, true);
        };
    }

    private Runnable logAtInfoLevelWithNullReturned() {
        return () -> {
            LOG.info("The result at {} level is {}", "info", lazy(this::expensiveReturningNull));
            completionFlag.compareAndSet(false, true);
        };
    }

    private Runnable logAtInfoLevelWithComplexObjectReturned() {
        return () -> {
            LOG.info("The result at {} level is {}", "info", lazy(this::expensiveToCreateThing));
            completionFlag.compareAndSet(false, true);
        };
    }

    private Runnable logAtTraceLevel() {
        return () -> {
            LOG.info("Before log of expensive computation at trace level...");
            LOG.trace("The result at {} level is {}", "trace", lazy(this::expensiveToCreateNumber));
            LOG.info("After log of expensive computation at trace level...");
            completionFlag.compareAndSet(false, true);
        };
    }

    private Callable<Boolean> completed() {
        return () -> {
            LOG.debug("Checking completion...");
            return completionFlag.get();
        };
    }

    private String expensiveToCreateString() {
        SilentSleeper.sleep(1, TimeUnit.SECONDS);
        return "42";
    }

    private Object expensiveReturningNull() {
        SilentSleeper.sleep(1, TimeUnit.SECONDS);
        return null;
    }

    private long expensiveToCreateNumber() {
        SilentSleeper.sleep(1, TimeUnit.SECONDS);
        return 42;
    }

    private Thing expensiveToCreateThing() {
        SilentSleeper.sleep(1, TimeUnit.SECONDS);
        return new Thing(42L, "The Blob", "It's blobby!");
    }

    @SuppressWarnings("unused")
    static class Thing {
        private Long id;
        private String name;
        private String description;

        public Thing(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Thing{" + "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}