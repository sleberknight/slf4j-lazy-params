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
    public void testDoesNotExecuteSupplier_WhenLogLevel_IsNotActive() {
        Executors.newSingleThreadExecutor().submit(logAtTraceLevel());
        await().atMost(150, TimeUnit.MILLISECONDS).until(completed());
    }

    private Runnable logAtInfoLevel() {
        return () -> {
            LOG.info("Before log of expensive computation at info level...");
            LOG.info("The result at {} level is {}", "info", delayed(this::expensiveToCreateValue));
            LOG.info("After log of expensive computation at info level...");
            completionFlag.compareAndSet(false, true);
        };
    }

    private Runnable logAtTraceLevel() {
        return () -> {
            LOG.info("Before log of expensive computation at trace level...");
            LOG.trace("The result at {} level is {}", "trace", lazy(this::expensiveToCreateValue));
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

    private String expensiveToCreateValue() {
        SilentSleeper.sleep(1, TimeUnit.SECONDS);
        return "42";
    }

}