package org.csstudio.archive.reader.appliance;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestApplianceArchiveReader;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorOptimized;
import org.csstudio.archive.reader.appliance.testClasses.TestGenMsgIteratorRaw;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVString;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/**
 *
 * <code>ApplianceArchiveReaderOptimizedTest</code> tests the optimized data
 * retrieval using a dummy loopback appliance server. This class test the
 * retrieval methods, if they decode the data correctly. It doesn't tests
 * if data makes sense.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ApplianceArchiveReaderOptimizedTest extends AbstractArchiverReaderTesting {

    @Override
    protected ArchiveReader getReader() {
        return new TestApplianceArchiveReader(false,false);
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a double type PV where the requested number of points is greater
     * than total number of points.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalWhenThereAreNotManyPoints() throws Exception{
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_double",true,1800,start,end);
        assertEquals("Number of values comparison", 10, vals.length);

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Double.valueOf(TestGenMsgIteratorRaw.VALUES_DOUBLE[i%TestGenMsgIteratorRaw.VALUES_DOUBLE.length]),(Double)val.getValue(),0.0001);
            assertEquals("Timestamp comparison", start.toEpochMilli() + i, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorRaw.SEVERITIES[i%TestGenMsgIteratorRaw.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorRaw.STATUS_STRING[i%TestGenMsgIteratorRaw.SEVERITIES.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a double type PV.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalDouble() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_double",true,100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Double.valueOf(TestGenMsgIteratorOptimized.VALUES_DOUBLE[i%TestGenMsgIteratorOptimized.VALUES_DOUBLE.length]),(Double)val.getValue(),0.0001);
            assertEquals("Timestamp comparison", startM + i*step, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a float type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalFloat() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_float",true,100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Float.valueOf(TestGenMsgIteratorOptimized.VALUES_FLOAT[i%TestGenMsgIteratorOptimized.VALUES_FLOAT.length]),(Float)val.getValue(),0.0001);
            assertEquals("Timestamp comparison", startM + i*step, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for an int type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalInt() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_int",true,100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Integer.valueOf(TestGenMsgIteratorOptimized.VALUES_INT[i%TestGenMsgIteratorOptimized.VALUES_INT.length]),val.getValue());
            assertEquals("Timestamp comparison", startM + i*step, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a short type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalShort() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_short",true,100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Short.valueOf(TestGenMsgIteratorOptimized.VALUES_SHORT[i%TestGenMsgIteratorOptimized.VALUES_SHORT.length]),val.getValue());
            assertEquals("Timestamp comparison", startM + i*step, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a byte type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalByte() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVNumber[] vals = getValuesNumber("test_pv_byte",true,100,start,end);
        assertEquals("Number of values comparison", 100, vals.length);

        long startM = start.toEpochMilli();
        long step = (end.toEpochMilli() - startM)/100;

        ArchiveVNumber val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", Byte.valueOf(TestGenMsgIteratorOptimized.VALUES_BYTE[i%TestGenMsgIteratorOptimized.VALUES_BYTE.length]),val.getValue());
            assertEquals("Timestamp comparison", startM + i*step, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for a string type pv.
     *
     * @throws Exception
     */
    @Test
    public void testDataRetrievalString() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVString[] vals = getValuesString("test_pv_string",true,100,start,end);
        //no optimization for the string type
        assertEquals("There should be 10 values all together", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);

        ArchiveVString val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", "9554.0",val.getValue());
            assertEquals("Timestamp comparison", start.toEpochMilli() + i, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }

    /**
     * Tests
     * {@code ApplianceArchiveReader#getOptimizedValues(int, String, Timestamp, Timestamp)}
     * method for an enum type pv.
     *
     * @throws Exception
     */
    @Test
    public void testataRetrievalEnum() throws Exception {
        Instant end = Instant.now();
        Instant start = end.minus(TimeDuration.ofHours(24.0));
        ArchiveVEnum[] vals = getValuesEnum("test_pv_enum",true,100,start,end);
        //no optimization for the enum type
        assertEquals("There should be 10 values all together", TestGenMsgIteratorRaw.MESSAGE_LIST_LENGTH, vals.length);

        ArchiveVEnum val = null;
        for (int i = 0; i < vals.length; i++) {
            val = vals[i];
            assertEquals("Value comparison", "Enum <" + TestGenMsgIteratorOptimized.VALUES_INT[i%TestGenMsgIteratorOptimized.VALUES_INT.length] + ">",val.getValue());
            assertEquals("Timestamp comparison", start.toEpochMilli() + i, val.getTimestamp().toEpochMilli());
            assertEquals("Severity", getSeverity(TestGenMsgIteratorOptimized.SEVERITIES[i%TestGenMsgIteratorOptimized.SEVERITIES.length]), val.getAlarmSeverity());
            assertEquals("Status", TestGenMsgIteratorOptimized.STATUS_STRING[i%TestGenMsgIteratorOptimized.STATUS.length], val.getAlarmName());
        }
    }
}
