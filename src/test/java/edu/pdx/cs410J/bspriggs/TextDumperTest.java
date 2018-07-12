package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextDumperTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests that dumping an empty phone bill returns nothing.
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
     * Tests that dumping a phone bill with only one call returns that one phone bill.
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
     * Tests that dumping multiple calls is the same as the toString.
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

    /**
     * Tests that creating a {@link TextDumper} with a filename has it writing to that file.
     */
    @Test
    public void testDumpEmptyFile() {
        try {
            File emptyFile = folder.newFile();

            var bill = new PhoneBill("name");
            var dumper = new TextDumper(emptyFile.getName());

            dumper.dump(bill);

            assertEquals(bill.toString(), String.join("\n", Files.readAllLines(emptyFile.toPath(), Charset.defaultCharset())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that if a file already exists {@link TextDumper} throws an exception.
     */
    @Test
    public void testDumpExistingFile() {
        try {
            File emptyFile = folder.newFile();

            var bill = new PhoneBill("name");

            try {
                bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
                bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
                bill.addPhoneCall(new PhoneCall("503-333-3333", "503-333-3333", "2/2/2 00:00", "2/2/3 0:00"));
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

            var dumper = new TextDumper(emptyFile.getName());

            dumper.dump(bill);

            thrown.expect(Exception.class);

            dumper.dump(bill);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}