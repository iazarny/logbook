package com.az.lb.misc;

import com.vaadin.flow.data.binder.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurationValidatorTest {

    DurationValidator durationValidator = new DurationValidator("Error");

    @Test
    void normalizeDurationString() {

        assertEquals(null, durationValidator.normalizeDurationString(null));
        assertEquals("", durationValidator.normalizeDurationString(""));
        assertEquals("", durationValidator.normalizeDurationString("  "));
        assertEquals("P4D", durationValidator.normalizeDurationString("4D"));
        assertEquals("PT1S", durationValidator.normalizeDurationString("1s"));
        assertEquals("P7DT9S", durationValidator.normalizeDurationString("7d9s"));
        assertEquals("P7DT5H123M1S", durationValidator.normalizeDurationString("7d5H123m1s"));
        assertEquals("P7DT5H123M1S", durationValidator.normalizeDurationString("7d 5H 123m 1s"));

    }

    @Test
    void applyOk() {
        assertFalse(durationValidator.apply("P7DT5H123M1S", null).isError());
        assertFalse(durationValidator.apply("   ", null).isError());
        assertFalse(durationValidator.apply("", null).isError());
        assertFalse(durationValidator.apply(null, null).isError());
    }

    @Test
    void applyError() {
        assertTrue(durationValidator.apply("Hi", null).isError());
    }
}