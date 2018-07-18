package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TextDumperTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected final String newline = System.getProperty("line.separator");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("H:M a");

    protected static Random r = new Random();

    public static Date generateDate() {
        return new Date(r.nextLong());
    }

    public static String generatePhoneNumber() {
        return String.format("%03d-%03d-%04d",
                1 + r.nextInt(998),
                1 + r.nextInt(998),
                1 + r.nextInt(9998)
        );
    }

    public static AbstractPhoneCall generatePhoneCall() throws ParseException {
        var start = generateDate();
        var end = generateDateAfter(start);
        return new PhoneCall(generatePhoneNumber(), generatePhoneNumber(), PhoneCall.DATE_FORMAT.format(start), PhoneCall.DATE_FORMAT.format(end));
    }

    public static Date generateDateAfter(Date date) {
        return new Date(date.getTime() + r.nextInt(10));
    }


    public static AbstractPhoneBill<AbstractPhoneCall> getPopulatedPhoneBill() {
        var bill = new PhoneBill("name");

        try {
            bill.addPhoneCall(generatePhoneCall());
            bill.addPhoneCall(generatePhoneCall());
            bill.addPhoneCall(generatePhoneCall());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        return bill;
    }

    @Test
    public void testDumpToHandlesNull() throws IOException {
        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(null, outputStream);
    }

    /**
     * Tests that dumping an empty phone bill returns nothing.
     */
    @Test
    public void testDumpToEmptyPhoneBill() throws IOException {
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
    public void testDumpToSinglePhoneCall() throws IOException {
        var bill = new PhoneBill("name");
        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);

        dumper.dumpTo(bill, outputStream);
        assertEquals(bill.getCustomer() + newline, new String(out.toByteArray()));
    }

    /**
     * Tests that dumping multiple calls is the same as the toString.
     */
    @Test
    public void testDumpToMultiplePhoneCalls() throws IOException {
        var bill = getPopulatedPhoneBill();

        var dumper = new TextDumper();
        var out = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(out);
        var calls = bill.getPhoneCalls().stream()
                .map(TextDumper::serialize)
                .collect(Collectors.joining(newline));

        dumper.dumpTo(bill, outputStream);

        assertEquals(bill.getCustomer() + newline + calls + newline, new String(out.toByteArray()));
    }

    /**
     * Tests that creating a {@link TextDumper} with a filename has it writing to that file.
     */
    @Test
    public void testDumpEmptyFile() {
        try {
            File emptyFile = folder.newFile();

            var bill = new PhoneBill("name");
            var dumper = new TextDumper(emptyFile.toPath());

            dumper.dump(bill);

            var lines = Files.readAllLines(emptyFile.toPath(), Charset.defaultCharset());

            assertEquals(bill.getCustomer(), String.join(newline, lines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that if a file already exists {@link TextDumper} throws an exception.
     */
    @Test
    public void testDumpExistingFile() throws IOException {
        File emptyFile = folder.newFile();

        var bill = getPopulatedPhoneBill();

        var dumper = new TextDumper(emptyFile.toPath());

        dumper.dump(bill);

        thrown.expect(IOException.class);

        dumper.dump(bill);
    }
}