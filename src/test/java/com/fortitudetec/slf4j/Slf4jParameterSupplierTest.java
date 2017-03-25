package com.fortitudetec.slf4j;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.fortitudetec.slf4j.Slf4jParameterSupplier.delayed;
import static com.fortitudetec.slf4j.Slf4jParameterSupplier.lazy;
import static org.assertj.core.api.Assertions.assertThat;

public class Slf4jParameterSupplierTest {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jParameterSupplierTest.class);

    private AtomicBoolean wasCalled;

    @Before
    public void setUp() {
        wasCalled = new AtomicBoolean(false);
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive() {
        LOG.info("The result at {} level is {}", "info", delayed(this::expensiveToCreateString));
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive_AndSupplierReturnsNull() {
        LOG.info("The result at {} level is {}", "info", lazy(this::expensiveReturningNull));
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testDoesExecuteSupplier_WhenLogLevel_IsActive_AndSupplierReturnsComplexObject() {
        LOG.info("The result at {} level is {}", "info", lazy(this::expensiveToCreateThing));
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testDoesNotExecuteSupplier_WhenLogLevel_IsNotActive() {
        LOG.trace("The result at {} level is {}", "trace", lazy(this::expensiveToCreateNumber));
        assertThat(wasCalled.get()).isFalse();
    }

    private String expensiveToCreateString() {
        wasCalled.set(true);
        return "42";
    }

    private Object expensiveReturningNull() {
        wasCalled.set(true);
        return null;
    }

    private long expensiveToCreateNumber() {
        wasCalled.set(true);
        return 42;
    }

    private Thing expensiveToCreateThing() {
        wasCalled.set(true);
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
            return "Thing{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}