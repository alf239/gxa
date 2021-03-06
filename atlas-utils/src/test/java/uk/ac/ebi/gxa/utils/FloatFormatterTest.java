package uk.ac.ebi.gxa.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FloatFormatterTest {
    @Test
    public void testDoubleFormats() {
        assertEquals("null", FloatFormatter.formatDouble(Double.POSITIVE_INFINITY, 1));
        assertEquals("null", FloatFormatter.formatDouble(Double.NaN, 1));
        assertEquals("0.0", FloatFormatter.formatDouble(0, 1));
        assertEquals("5.0", FloatFormatter.formatDouble(5, 1));
        assertEquals("600.0", FloatFormatter.formatDouble(555, 1));
        assertEquals("6.0E10", FloatFormatter.formatDouble(55555555555.0, 1));
        assertEquals("5.56E10", FloatFormatter.formatDouble(55555555555.0, 3));
    }

    @Test
    public void testFloatFormats() {
        assertEquals("null", FloatFormatter.formatFloat(Float.POSITIVE_INFINITY, 1));
        assertEquals("null", FloatFormatter.formatFloat(Float.NaN, 1));
        assertEquals("0.0", FloatFormatter.formatFloat(0, 1));
        assertEquals("5.0", FloatFormatter.formatFloat(5, 1));
        assertEquals("600.0", FloatFormatter.formatFloat(555, 1));
        assertEquals("6.0E10", FloatFormatter.formatFloat(55555555555.0F, 1));
        assertEquals("5.56E10", FloatFormatter.formatFloat(55555555555.0F, 3));
        assertEquals("2.75E-6", FloatFormatter.formatFloat(2.748345E-6F, 3));
        assertEquals("-2.75E-6", FloatFormatter.formatFloat(-2.748345E-6F, 3));
        assertEquals("0.0", FloatFormatter.formatFloat(2.748345E-11F, 3));
        assertEquals("0.0", FloatFormatter.formatFloat(-2.748345E-11F, 3));
        assertEquals("69.1", FloatFormatter.formatFloat(69.12F, 3));
        assertEquals("86.6", FloatFormatter.formatFloat(86.6F, 3));
    }
}
