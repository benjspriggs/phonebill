package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.assertEquals;

public class TextDumperTest {

    /**
     * Tests that dumping an empty phone bill returns nothing
     */
    @Test
    public void testDumpToEmptyPhoneBill() {
        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(outputStream);
        assertEquals("", new String(out.toByteArray()));
    }

    /**
     * Tests that dumping a phone bill with only one call returns that one phone bill
     */
    @Test
    public void testDumpToSinglePhoneCall() {
        var bill = new PhoneBill();
        var dumper = new TextDumper(bill);
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(outputStream);
        assertEquals(bill.toString(), new String(out.toByteArray()));
    }
}