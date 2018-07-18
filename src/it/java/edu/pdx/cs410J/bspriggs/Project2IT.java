package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Project2IT extends Project1IT {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    protected Random r = new Random();
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("D/M/Y");
    protected SimpleDateFormat timeFormat = new SimpleDateFormat("H:M");

    /**
     * Invokes the main method of {@link Project2} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project2.class, args);
    }

    protected Date generateDate() {
        return new Date(r.nextLong());
    }

    protected String generatePhoneNumber() {
        return String.format("%03d-%03d-%04d",
                1 + r.nextInt(998),
                1 + r.nextInt(998),
                1 + r.nextInt(9998)
        );
    }

    protected File generateExistingPhoneBill(AbstractPhoneBill with) throws IOException {
        File file = folder.newFile();
        TextDumper dumper = new TextDumper(file.toPath());

        if (with == null) {
            with = TextDumperTest.getPopulatedPhoneBill();
        }

        dumper.dump(with);

        return file;
    }

    /**
     * Tests that invoking the main method with no arguments issues an error
     */
    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    }

    /**
     * Tests that invoking the main method with README issues readme
     */
    @Test
    public void testREADME() {
        MainMethodResult result = invokeMain("-README");
        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), containsString("usage"));
    }

    /**
     * Tests that invoking the command line with an empty phone bill creates a phone bill.
     */
    @Test
    public void testEmptyPhoneBill() {
        var customer = "customer";
        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var start = generateDate();
        var end = generateDate();

        MainMethodResult result = invokeMain("-textFile", "", customer, callerNumber, calleeNumber, dateFormat.format(start), timeFormat.format(start), dateFormat.format(end), timeFormat.format(end));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    }

    /**
     * Tests that invoking the command line with an actual phone bill adds the new phone bill to the file.
     */
    @Test
    public void testExistingPhoneBill() throws IOException {
        var bill = TextDumperTest.getPopulatedPhoneBill();
        File existingPhoneBill = generateExistingPhoneBill(bill);

        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var start = generateDate();
        var end = generateDate();

        MainMethodResult result = invokeMain("-textFile", existingPhoneBill.getAbsolutePath(), bill.getCustomer(), callerNumber, calleeNumber, dateFormat.format(start), timeFormat.format(start), dateFormat.format(end), timeFormat.format(end));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    /**
     * Test that providing a different customer exits.
     */
    @Test
    public void testExistingPhoneBillNameMismatch() throws IOException {
        var bill = TextDumperTest.getPopulatedPhoneBill();
        File existingPhoneBill = generateExistingPhoneBill(bill);

        var customer = "new customer";
        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var start = generateDate();
        var end = generateDate();

        MainMethodResult result = invokeMain("-textFile", existingPhoneBill.getAbsolutePath(), "", callerNumber, calleeNumber, dateFormat.format(start), timeFormat.format(start), dateFormat.format(end), timeFormat.format(end));
        assertThat(result.getExitCode(), equalTo(1));
    }
}