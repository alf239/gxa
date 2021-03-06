package uk.ac.ebi.gxa.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static uk.ac.ebi.gxa.utils.NumberFormatUtil.formatPValue;
import static uk.ac.ebi.gxa.utils.NumberFormatUtil.formatTValue;

public class NumberFormatUtilTest {

    @Test(expected = NullPointerException.class)
    public void testTValueNullFormat() {
        formatTValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPValueNullFormat() {
        formatPValue(null);
    }

    @Test
    public void testNAN() {
        assertEquals("NAN", nobrWrap("N/A"), formatTValue(Float.NaN));
        assertEquals("NAN", nobrWrap("N/A"), formatPValue(Float.NaN));
    }

    @Test
    public void testEmptyValue() {
        assertEquals("Empty value", "<nobr>9.97 &#0215; 10<span style=\"vertical-align: super;\">36</span></nobr>",
                formatPValue(9969209968386869000000000000000000000.000f));
    }

    @Test
    public void testEmptyValueExponential() {
        assertEquals("Empty value (exponential)", "<nobr>9.97 &#0215; 10<span style=\"vertical-align: super;\">36</span></nobr>",
                formatPValue(9.97e36f));
    }

    @Test
    public void testIntegerValue() {
        assertEquals("Integer value", nobrWrap("1"),
                formatPValue(1.0f));
    }

    @Test
    public void testSimpleFloatValue() {
        assertEquals("Simple float value", nobrWrap("1.01"),
                formatPValue(1.01f));
    }

    @Test
    public void testFloatAccuracyPositive() {
        assertEquals("Float accuracy - positive test", nobrWrap("1.001"),
                formatPValue(1.001f));
    }

    @Test
    public void testFloatAccuracyNegative() {
        assertEquals("Float accuracy - negative test", nobrWrap("1"),
                formatPValue(1.0001f));
    }

    @Test
    public void testTValueFormat() {
        assertEquals(nobrWrap("0"), formatTValue(0.0f));

        assertEquals(nobrWrap("0"), formatTValue(1.2E-11f));
        assertEquals(nobrWrap("0"), formatTValue(-1.2E-11f));

        assertEquals(nobrWrap("1.2E-5"), formatTValue(1.2E-5f));
        assertEquals(nobrWrap("-1.2E-5"), formatTValue(-1.2E-5f));

        assertEquals(nobrWrap("123"), formatTValue(123.456f));
        assertEquals(nobrWrap("-123"), formatTValue(-123.456f));

        assertEquals(nobrWrap("124"), formatTValue(123.567f));
        assertEquals(nobrWrap("-124"), formatTValue(-123.567f));

        assertEquals(nobrWrap("10"), formatTValue(10.0f));
        assertEquals(nobrWrap("-10"), formatTValue(-10.0f));

        assertEquals(nobrWrap("10"), formatTValue(10.123f));
        assertEquals(nobrWrap("-10"), formatTValue(-10.123f));

        assertEquals(nobrWrap("11"), formatTValue(10.623f));
        assertEquals(nobrWrap("-11"), formatTValue(-10.623f));

        assertEquals(nobrWrap("0.12"), formatTValue(0.123f));
        assertEquals(nobrWrap("-0.12"), formatTValue(-0.123f));

        assertEquals(nobrWrap("0.13"), formatTValue(0.1267f));
        assertEquals(nobrWrap("-0.13"), formatTValue(-0.1267f));

        assertEquals(nobrWrap("1.3"), formatTValue(1.32f));
        assertEquals(nobrWrap("-1.3"), formatTValue(-1.32f));

        assertEquals(nobrWrap("1.4"), formatTValue(1.356f));
        assertEquals(nobrWrap("-1.4"), formatTValue(-1.356f));

        assertEquals(nobrWrap("9"), formatTValue(9.02f));
    }

    private String nobrWrap(String s) {
        return "<nobr>" + s + "</nobr>";
    }
}
