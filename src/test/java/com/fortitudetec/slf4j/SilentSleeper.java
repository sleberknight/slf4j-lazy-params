package com.fortitudetec.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SilentSleeper {

    private static final Logger LOG = LoggerFactory.getLogger(SilentSleeper.class);

    private SilentSleeper() {
    }

    @SuppressWarnings("squid:S2925")  // intentionally using sleep to test delayed SLF4J replacement parameters
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.warn("InterruptedException while sleeping for {} millis", millis);
        }
    }

    public static void sleep(long time, TimeUnit unit) {
        sleep(unit.toMillis(time));
    }

}
