package studio.kdb;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KTest {

    private Collection<DynamicTest> check(K.KBase base, String expectedNoType, String expectedWithType) throws IOException {
        String actualNoType = base.toString(false);
        String actualWithType = base.toString(true);
        //uncomment below for easy debugging
        //System.out.println("\"" + actualNoType + "\", \"" + actualWithType + "\"");

        return Arrays.asList(
                DynamicTest.dynamicTest("toString(false) for " + base.getDataType(),
                        () -> assertEquals(expectedNoType, actualNoType)),
                DynamicTest.dynamicTest("toString(true) for " + base.getDataType(),
                        () -> assertEquals(expectedWithType, actualWithType)));
    }


    private void check2(K.KBase base, String expectedNoType, String expectedWithType) {
        String actualNoType = base.toString(false);
        String actualWithType = base.toString(true);
        //uncomment below for easy debugging
        System.out.println("\"" + actualNoType + "\", \"" + actualWithType + "\"");
        assertEquals(expectedNoType, actualNoType);
        assertEquals(expectedWithType, actualWithType);
    }


    private K.KBaseVector vector(Class clazz, Object... values) throws Exception {
        K.KBaseVector baseVector = (K.KBaseVector) clazz.getConstructor(int.class).newInstance(values.length);
        Object anArray = baseVector.getArray();
        for (int index=0; index < values.length; index++) {
            Array.set(anArray, index, values[index]);
        }
        return baseVector;
    }


    @TestFactory
    public Collection<DynamicTest> testIntegerToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KInteger(-123), "-123", "-123i"));
        tests.addAll(check(new K.KInteger(-Integer.MAX_VALUE), "-0W", "-0Wi"));
        tests.addAll(check(new K.KInteger(Integer. MAX_VALUE), "0W", "0Wi"));
        tests.addAll(check(new K.KInteger(Integer.MIN_VALUE), "0N", "0Ni"));

        tests.addAll(check(vector(K.KIntVector.class, -10, 10, 3), "-10 10 3", "-10 10 3i"));
        tests.addAll(check(vector(K.KIntVector.class), "`int$()", "`int$()"));
        tests.addAll(check(vector(K.KIntVector.class, 0), "enlist 0", "enlist 0i"));
        tests.addAll(check(vector(K.KIntVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "5 0N 0W -0W", "5 0N 0W -0Wi"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testLongToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KLong(-123456789), "-123456789", "-123456789j"));
        tests.addAll(check(new K.KLong(-Long.MAX_VALUE), "-0W", "-0Wj"));
        tests.addAll(check(new K.KLong(Long. MAX_VALUE), "0W", "0Wj"));
        tests.addAll(check(new K.KLong(Long.MIN_VALUE), "0N", "0Nj"));

        tests.addAll(check(vector(K.KLongVector.class, -10, 10, 3), "-10 10 3", "-10 10 3j"));
        tests.addAll(check(vector(K.KLongVector.class), "`long$()", "`long$()"));
        tests.addAll(check(vector(K.KLongVector.class, 0), "enlist 0", "enlist 0j"));
        tests.addAll(check(vector(K.KLongVector.class, 5, Long.MIN_VALUE, Long.MAX_VALUE, -Long.MAX_VALUE), "5 0N 0W -0W", "5 0N 0W -0Wj"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testShortToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KShort((short)-123), "-123", "-123h"));
        tests.addAll(check(new K.KShort((short) -32767 ), "-0W", "-0Wh"));
        tests.addAll(check(new K.KShort(Short.MAX_VALUE), "0W", "0Wh"));
        tests.addAll(check(new K.KShort(Short.MIN_VALUE), "0N", "0Nh"));

        tests.addAll(check(vector(K.KShortVector.class, (short)-10, (short)10, (short)3), "-10 10 3", "-10 10 3h"));
        tests.addAll(check(vector(K.KShortVector.class), "`short$()", "`short$()"));
        tests.addAll(check(vector(K.KShortVector.class, (short)0), "enlist 0", "enlist 0h"));
        tests.addAll(check(vector(K.KShortVector.class, (short)5, Short.MIN_VALUE, Short.MAX_VALUE, (short)-Short.MAX_VALUE), "5 0N 0W -0W", "5 0N 0W -0Wh"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testByteToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KByte((byte)123), "0x7b", "0x7b"));
        tests.addAll(check(new K.KByte((byte)0 ), "0x00", "0x00"));
        tests.addAll(check(new K.KByte((byte)-1 ), "0xff", "0xff"));
        tests.addAll(check(new K.KByte((byte)127), "0x7f", "0x7f"));
        tests.addAll(check(new K.KByte((byte)-128), "0x80", "0x80"));
        tests.addAll(check(new K.KByte((byte)-127), "0x81", "0x81"));

        tests.addAll(check(vector(K.KByteVector.class, (byte)-10, (byte)10, (byte)3), "0xf60a03", "0xf60a03"));
        tests.addAll(check(vector(K.KByteVector.class), "`byte$()", "`byte$()"));
        tests.addAll(check(vector(K.KByteVector.class, (byte)0), "enlist 0x00", "enlist 0x00"));
        tests.addAll(check(vector(K.KByteVector.class, (byte)5, (byte)-127, (byte)128, (byte)0), "0x05818000", "0x05818000"));

        return tests;
    }


    @TestFactory
    public Collection<DynamicTest> testDoubleToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KDouble(-1.23), "-1.23", "-1.23"));
        tests.addAll(check(new K.KDouble(3), "3", "3f"));
        tests.addAll(check(new K.KDouble(0), "0", "0f"));
        tests.addAll(check(new K.KDouble(Double.POSITIVE_INFINITY ), "0w", "0w"));
        tests.addAll(check(new K.KDouble(Double.NEGATIVE_INFINITY), "-0w", "-0w"));
        tests.addAll(check(new K.KDouble(Double.NaN), "0n", "0n"));

        tests.addAll(check(vector(K.KDoubleVector.class, (double)-10, (double)10, (double)3), "-10 10 3f", "-10 10 3f"));
        tests.addAll(check(vector(K.KDoubleVector.class, (double)-10, 10.1, (double)3), "-10 10.1 3", "-10 10.1 3"));
        tests.addAll(check(vector(K.KDoubleVector.class), "`float$()", "`float$()"));
        tests.addAll(check(vector(K.KDoubleVector.class, (double)0), "enlist 0f", "enlist 0f"));
        tests.addAll(check(vector(K.KDoubleVector.class, (double)5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN), "5 -0w 0w 0n", "5 -0w 0w 0n"));

        return tests;
    }

    @Test
    public void testFloatToString2() throws Exception {
        check2(new K.KFloat(-1.23f), "-1.23", "-1.23");
        check2(new K.KFloat(3), "3", "3e");
        check2(new K.KFloat(0), "0", "0e");
        check2(new K.KFloat(Float.POSITIVE_INFINITY ), "0we", "0we");
        check2(new K.KFloat(Float.NEGATIVE_INFINITY), "-0we", "-0we");
        check2(new K.KFloat(Float.NaN), "0ne", "0ne");

        check2(vector(K.KFloatVector.class, -10f, 10f, 3f), "-10 10 3e", "-10 10 3e");
        check2(vector(K.KFloatVector.class, -10f, 10.1f, 3f), "-10 10.1000004 3", "-10 10.1000004 3");
        check2(vector(K.KFloatVector.class), "`real$()", "`real$()");
        check2(vector(K.KFloatVector.class, 0f), "enlist 0e", "enlist 0e");
        check2(vector(K.KFloatVector.class, 5f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN), "5 -0W 0W 0N", "5 -0W 0W 0N");
    }


    @TestFactory
    public Collection<DynamicTest> testFloatToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KFloat(-1.23f), "-1.23", "-1.23"));
        tests.addAll(check(new K.KFloat(3), "3", "3e"));
        tests.addAll(check(new K.KFloat(0), "0", "0e"));
        tests.addAll(check(new K.KFloat(Float.POSITIVE_INFINITY ), "0we", "0we"));
        tests.addAll(check(new K.KFloat(Float.NEGATIVE_INFINITY), "-0we", "-0we"));
        tests.addAll(check(new K.KFloat(Float.NaN), "0ne", "0ne"));

        tests.addAll(check(vector(K.KFloatVector.class, -10f, 10f, 3f), "-10 10 3e", "-10 10 3e"));
        tests.addAll(check(vector(K.KFloatVector.class, -10f, 10.1f, 3f), "-10 10.1000004 3", "-10 10.1000004 3"));
        tests.addAll(check(vector(K.KFloatVector.class), "`real$()", "`real$()"));
        tests.addAll(check(vector(K.KFloatVector.class, 0f), "enlist 0e", "enlist 0e"));
        tests.addAll(check(vector(K.KFloatVector.class, 5f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN), "5 -0W 0W 0N", "5 -0W 0W 0N"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testBooleanToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KBoolean(false), "0", "0b"));
        tests.addAll(check(new K.KBoolean(true), "1", "1b"));

        tests.addAll(check(vector(K.KBooleanVector.class, true, false), "10b", "10b"));
        tests.addAll(check(vector(K.KBooleanVector.class), "`boolean$()", "`boolean$()"));
        tests.addAll(check(vector(K.KBooleanVector.class, true), "enlist 1b", "enlist 1b"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testCharacterToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KCharacter(' '), " ", "\" \""));
        tests.addAll(check(new K.KCharacter('a'), "a", "\"a\""));

        tests.addAll(check(vector(K.KCharacterVector.class, ' ', 'a'), " a", "\" a\""));
        tests.addAll(check(vector(K.KCharacterVector.class), "", "\"\""));
        tests.addAll(check(vector(K.KCharacterVector.class, 'a'), "enlist a", "enlist \"a\""));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testSymbolToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KSymbol(""), "", "`"));
        tests.addAll(check(new K.KSymbol("a"), "a", "`a"));
        tests.addAll(check(new K.KSymbol("ab"), "ab", "`ab"));
        tests.addAll(check(new K.KSymbol(" "), " ", "` "));

        tests.addAll(check(vector(K.KSymbolVector.class, "b", "aa"), "`b`aa", "`b`aa"));
        tests.addAll(check(vector(K.KSymbolVector.class), "0#`", "0#`"));
        tests.addAll(check(vector(K.KSymbolVector.class, "", " ", "ab"), "`` `ab", "`` `ab"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testGuidToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KGuid(new UUID(12345,-987654)), "00000000-0000-3039-ffff-fffffff0edfa", "00000000-0000-3039-ffff-fffffff0edfa"));
        tests.addAll(check(new K.KGuid(new UUID(0,0)), "00000000-0000-0000-0000-000000000000", "00000000-0000-0000-0000-000000000000"));

        tests.addAll(check(vector(K.KGuidVector.class, new UUID(1,-1), new UUID(0,1), new UUID(-1,0)), "00000000-0000-0001-ffff-ffffffffffff 00000000-0000-0000-0000-000000000001 ffffffff-ffff-ffff-0000-000000000000", "00000000-0000-0001-ffff-ffffffffffff 00000000-0000-0000-0000-000000000001 ffffffff-ffff-ffff-0000-000000000000"));
        tests.addAll(check(vector(K.KGuidVector.class), "`guid$()", "`guid$()"));
        tests.addAll(check(vector(K.KGuidVector.class, new UUID(0,0)), "enlist 00000000-0000-0000-0000-000000000000", "enlist 00000000-0000-0000-0000-000000000000"));

        return tests;
    }
    @TestFactory
    public Collection<DynamicTest> testTimestampToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KTimestamp(-123456789), "1999.12.31 23:59:59.876543211", "1999.12.31 23:59:59.876543211"));
        tests.addAll(check(new K.KTimestamp(123456), "2000.01.01 00:00:00.000123456", "2000.01.01 00:00:00.000123456"));
        tests.addAll(check(new K.KTimestamp(-Long.MAX_VALUE), "-0Wp", "-0Wp"));
        tests.addAll(check(new K.KTimestamp(Long. MAX_VALUE), "0Wp", "0Wp"));
        tests.addAll(check(new K.KTimestamp(Long.MIN_VALUE), "0Np", "0Np"));

        tests.addAll(check(vector(K.KTimestampVector.class, -10, 10, 3), "1999.12.31 23:59:59.999999990 2000.01.01 00:00:00.000000010 2000.01.01 00:00:00.000000003", "1999.12.31 23:59:59.999999990 2000.01.01 00:00:00.000000010 2000.01.01 00:00:00.000000003"));
        tests.addAll(check(vector(K.KTimestampVector.class), "`timestamp$()", "`timestamp$()"));
        tests.addAll(check(vector(K.KTimestampVector.class, 0), "enlist 2000.01.01 00:00:00.000000000", "enlist 2000.01.01 00:00:00.000000000"));
        tests.addAll(check(vector(K.KTimestampVector.class, 5, Long.MIN_VALUE, Long.MAX_VALUE, -Long.MAX_VALUE), "2000.01.01 00:00:00.000000005 0Np 0Wp -0Wp", "2000.01.01 00:00:00.000000005 0Np 0Wp -0Wp"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testTimespanToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KTimespan(-765432123456789l), "-8D20:37:12.123456789", "-8D20:37:12.123456789"));
        tests.addAll(check(new K.KTimespan(123456), "00:00:00.000123456", "00:00:00.000123456"));
        tests.addAll(check(new K.KTimespan(-Long.MAX_VALUE), "-0Wn", "-0Wn"));
        tests.addAll(check(new K.KTimespan(Long. MAX_VALUE), "0Wn", "0Wn"));
        tests.addAll(check(new K.KTimespan(Long.MIN_VALUE), "0Nn", "0Nn"));

        tests.addAll(check(vector(K.KTimespanVector.class, -10, 10, 3), "-00:00:00.000000010 00:00:00.000000010 00:00:00.000000003", "-00:00:00.000000010 00:00:00.000000010 00:00:00.000000003"));
        tests.addAll(check(vector(K.KTimespanVector.class), "`timespan$()", "`timespan$()"));
        tests.addAll(check(vector(K.KTimespanVector.class, 0), "enlist 00:00:00.000000000", "enlist 00:00:00.000000000"));
        tests.addAll(check(vector(K.KTimespanVector.class, 5, Long.MIN_VALUE, Long.MAX_VALUE, -Long.MAX_VALUE), "00:00:00.000000005 0Nn 0Wn -0Wn", "00:00:00.000000005 0Nn 0Wn -0Wn"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testDateToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KDate(-1234), "1996.08.15", "1996.08.15"));
        tests.addAll(check(new K.KDate(123456), "2338.01.05", "2338.01.05"));
        tests.addAll(check(new K.KDate(-Integer.MAX_VALUE), "-0Wd", "-0Wd"));
        tests.addAll(check(new K.KDate(Integer. MAX_VALUE), "0Wd", "0Wd"));
        tests.addAll(check(new K.KDate(Integer.MIN_VALUE), "0Nd", "0Nd"));

        tests.addAll(check(vector(K.KDateVector.class, -10, 10, 3), "1999.12.22 2000.01.11 2000.01.04", "1999.12.22 2000.01.11 2000.01.04"));
        tests.addAll(check(vector(K.KDateVector.class), "`date$()", "`date$()"));
        tests.addAll(check(vector(K.KDateVector.class, 0), "enlist 2000.01.01", "enlist 2000.01.01"));
        tests.addAll(check(vector(K.KDateVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "2000.01.06 0N 0W -0W", "2000.01.06 0N 0W -0W"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testTimeToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        //@ToDo Fix me
        tests.addAll(check(new K.KTime(-1234567890), "17:03:52.110", "17:03:52.110"));
        tests.addAll(check(new K.KTime(323456789), "17:50:56.789", "17:50:56.789"));

        tests.addAll(check(new K.KTime(-Integer.MAX_VALUE), "-0Wt", "-0Wt"));
        tests.addAll(check(new K.KTime(Integer. MAX_VALUE), "0Wt", "0Wt"));
        tests.addAll(check(new K.KTime(Integer.MIN_VALUE), "0Nt", "0Nt"));

        tests.addAll(check(vector(K.KTimeVector.class, -10, 10, 3), "23:59:59.990 00:00:00.010 00:00:00.003", "23:59:59.990 00:00:00.010 00:00:00.003"));
        tests.addAll(check(vector(K.KTimeVector.class), "`time$()", "`time$()"));
        tests.addAll(check(vector(K.KTimeVector.class, 0), "enlist 00:00:00.000", "enlist 00:00:00.000"));
        tests.addAll(check(vector(K.KTimeVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "00:00:00.005 0Nt 0Wt -0Wt", "00:00:00.005 0Nt 0Wt -0Wt"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testMonthToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.Month(-12345), "0971.04", "0971.04m"));
        tests.addAll(check(new K.Month(123456), "12288.01", "12288.01m"));
        tests.addAll(check(new K.Month(-Integer.MAX_VALUE), "-0Wm", "-0Wm"));
        tests.addAll(check(new K.Month(Integer. MAX_VALUE), "0Wm", "0Wm"));
        tests.addAll(check(new K.Month(Integer.MIN_VALUE), "0Nm", "0Nm"));

        tests.addAll(check(vector(K.KMonthVector.class, -10, 10, 3), "1999.03 2000.11 2000.04", "1999.03 2000.11 2000.04m"));
        tests.addAll(check(vector(K.KMonthVector.class), "`month$()", "`month$()"));
        tests.addAll(check(vector(K.KMonthVector.class, 0), "enlist 2000.01", "enlist 2000.01m"));
        tests.addAll(check(vector(K.KMonthVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "2000.06 0N 0W -0W", "2000.06 0N 0W -0Wm"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testMinuteToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        //@ToDo Fix me
        tests.addAll(check(new K.Minute(-12345), "-205:-45", "-205:-45"));

        tests.addAll(check(new K.Minute(123456), "2057:36", "2057:36"));
        tests.addAll(check(new K.Minute(-Integer.MAX_VALUE), "-0Wu", "-0Wu"));
        tests.addAll(check(new K.Minute(Integer. MAX_VALUE), "0Wu", "0Wu"));
        tests.addAll(check(new K.Minute(Integer.MIN_VALUE), "0Nu", "0Nu"));

        tests.addAll(check(vector(K.KMinuteVector.class, -10, 10, 3), "00:-10 00:10 00:03", "00:-10 00:10 00:03"));
        tests.addAll(check(vector(K.KMinuteVector.class), "`minute$()", "`minute$()"));
        tests.addAll(check(vector(K.KMinuteVector.class, 0), "enlist 00:00", "enlist 00:00"));
        tests.addAll(check(vector(K.KMinuteVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "00:05 0Nu 0Wu -0Wu", "00:05 0Nu 0Wu -0Wu"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testSecondToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        //@ToDo Fix me
        tests.addAll(check(new K.Second(-12345), "-03:-25:-45", "-03:-25:-45"));

        tests.addAll(check(new K.Second(123456), "34:17:36", "34:17:36"));
        tests.addAll(check(new K.Second(-Integer.MAX_VALUE), "-0Wv", "-0Wv"));
        tests.addAll(check(new K.Second(Integer. MAX_VALUE), "0Wv", "0Wv"));
        tests.addAll(check(new K.Second(Integer.MIN_VALUE), "0Nv", "0Nv"));

        tests.addAll(check(vector(K.KSecondVector.class, -10, 10, 3), "00:00:-10 00:00:10 00:00:03", "00:00:-10 00:00:10 00:00:03"));
        tests.addAll(check(vector(K.KSecondVector.class), "`second$()", "`second$()"));
        tests.addAll(check(vector(K.KSecondVector.class, 0), "enlist 00:00:00", "enlist 00:00:00"));
        tests.addAll(check(vector(K.KSecondVector.class, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE), "00:00:05 0Nv 0Wv -0Wv", "00:00:05 0Nv 0Wv -0Wv"));

        return tests;
    }

    @TestFactory
    public Collection<DynamicTest> testDatetimeToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(new K.KDatetime(-123456.789), "1661.12.26 05:03:50.401", "1661.12.26 05:03:50.401"));
        tests.addAll(check(new K.KDatetime(123.456), "2000.05.03 10:56:38.400", "2000.05.03 10:56:38.400"));
        tests.addAll(check(new K.KDatetime(Double.NEGATIVE_INFINITY), "-0wz", "-0wz"));
        tests.addAll(check(new K.KDatetime(Double.POSITIVE_INFINITY), "0wz", "0wz"));
        tests.addAll(check(new K.KDatetime(Double.NaN), "0nz", "0nz"));

        tests.addAll(check(vector(K.KDatetimeVector.class, -10.0, 10.0, 3.0), "1999.12.22 00:00:00.000  2000.01.11 00:00:00.000  2000.01.04 00:00:00.000", "1999.12.22 00:00:00.000  2000.01.11 00:00:00.000  2000.01.04 00:00:00.000"));
        tests.addAll(check(vector(K.KDatetimeVector.class), "`datetime$()", "`datetime$()"));
        tests.addAll(check(vector(K.KDatetimeVector.class, 0.0), "enlist 2000.01.01 00:00:00.000", "enlist 2000.01.01 00:00:00.000"));
        tests.addAll(check(vector(K.KDatetimeVector.class, 5.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN), "2000.01.06 00:00:00.000  -0w  0w  0N", "2000.01.06 00:00:00.000  -0w  0w  0N"));

        return tests;
    }


    @TestFactory
    public Collection<DynamicTest> testListToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        tests.addAll(check(vector(K.KList.class), "()", "()"));
        tests.addAll(check(vector(K.KList.class, new K.KLong(10), new K.KLong(Long.MAX_VALUE)), "(10;0W)", "(10j;0Wj)"));
        tests.addAll(check(vector(K.KList.class, new K.KLong(10), new K.KInteger(10)), "(10;10)", "(10j;10i)"));
        tests.addAll(check(vector(K.KList.class, new K.KLong(10), new K.KInteger(10), vector(K.KList.class, new K.KDouble(1.1))), "(10;10;enlist 1.1)", "(10j;10i;enlist 1.1)"));

        return tests;
    }


    @TestFactory
    public Collection<DynamicTest> testOtherToString() throws Exception {
        Collection<DynamicTest> tests = new ArrayList<>();

        K.Function funcUnary = new K.Function(new K.KCharacterVector("{1+x}"));
        K.Function funcUnary2 = new K.Function(new K.KCharacterVector("{2*x}"));
        K.Function funcBinary = new K.Function(new K.KCharacterVector("{x+y}"));

        tests.addAll(check(funcUnary,"{1+x}", "{1+x}"));
        tests.addAll(check(funcBinary,"{x+y}", "{x+y}"));

        tests.addAll(check(new K.FEachLeft(funcBinary), "{x+y}\\:", "{x+y}\\:"));
        tests.addAll(check(new K.FEachRight(funcBinary), "{x+y}/:", "{x+y}/:"));
        tests.addAll(check(new K.Feach(funcBinary), "{x+y}'", "{x+y}'"));
        tests.addAll(check(new K.Fover(funcBinary), "{x+y}/", "{x+y}/"));
        tests.addAll(check(new K.Fscan(funcBinary), "{x+y}\\", "{x+y}\\"));
        tests.addAll(check(new K.FPrior(funcBinary), "{x+y}':", "{x+y}':"));

        tests.addAll(check(new K.FComposition(new Object[] {funcUnary, funcBinary}), "",""));
        tests.addAll(check(new K.Projection((K.KList)vector(K.KList.class, funcBinary, new K.KLong(1), new K.UnaryPrimitive(-1))), "{x+y}[1;]", "{x+y}[1j;]"));
        tests.addAll(check(new K.Projection((K.KList)vector(K.KList.class, funcBinary, new K.UnaryPrimitive(-1), new K.KLong(1))), "{x+y}[;1]", "{x+y}[;1j]"));

        tests.addAll(check(new K.BinaryPrimitive(15), "~", "~"));
        tests.addAll(check(new K.UnaryPrimitive(0), "::", "::"));
        tests.addAll(check(new K.UnaryPrimitive(41), "enlist", "enlist"));

        //wrong output
        //should it be 1+ ?
        tests.addAll(check(new K.Projection((K.KList)vector(K.KList.class, new K.BinaryPrimitive(1), new K.KLong(1))), "+[1]", "+[1j]"));
        //should it be '[;]
        tests.addAll(check(new K.Projection((K.KList)vector(K.KList.class, new K.TernaryOperator(0), new K.UnaryPrimitive(-1), new K.UnaryPrimitive(-1))), "(';;)", "(';;)"));


        return tests;
    }



}
