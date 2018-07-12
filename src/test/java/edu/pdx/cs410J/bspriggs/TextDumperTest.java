package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextDumperTest {

    /**
     * Tests that dumping an empty phone bill returns nothing
     */
    @Test
    public void testDumpToEmptyPhoneBill() {
        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(null, outputStream);
        assertEquals("", new String(out.toByteArray()));
    }

    /**
     * Tests that dumping a phone bill with only one call returns that one phone bill
     */
    @Test
    public void testDumpToSinglePhoneCall() {
        var bill = new PhoneBill("name");
        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(bill, outputStream);
        assertEquals(bill.toString(), new String(out.toByteArray()));
    }

    /**
     * Tests that dumping multiple calls is the same as the toString
     */
    @Test
    public void testDumpToMultiplePhoneCalls() {
        var bill = new PhoneBill("name");

        try {
            bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
            bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
            bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);
        var calls = bill.getPhoneCalls().parallelStream()
                .map(call -> TextDumper.serialize((AbstractPhoneCall) call))
                .collect(Collectors.joining("\n"));

        dumper.dumpTo(bill, outputStream);
        assertEquals(bill.toString() + "\n" + calls, new String(out.toByteArray()));
    }
}