package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project2IT extends InvokeMainTestCase {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Invokes the main method of {@link Project2} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project2.class, args);
    }

    private String generateTime() {
        return "1:11";
    }

    private String generateDate() {
        return "11/11/11";
    }

    private String generatePhoneNumber() {
        return "555-555-5555";
    }

    private File generateExistingPhoneBill(AbstractPhoneBill with) throws IOException {
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
        var startDate = generateDate();
        var startTime = generateTime();
        var endDate = generateDate();
        var endTime = generateTime();

        MainMethodResult result = invokeMain("-textFile", "", customer, callerNumber, calleeNumber, startDate, startTime, endDate, endTime);

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
        var startDate = generateDate();
        var startTime = generateTime();
        var endDate = generateDate();
        var endTime = generateTime();

        MainMethodResult result = invokeMain("-textFile", existingPhoneBill.getAbsolutePath(), bill.getCustomer(), callerNumber, calleeNumber, startDate, startTime, endDate, endTime);

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
        var startDate = generateDate();
        var startTime = generateTime();
        var endDate = generateDate();
        var endTime = generateTime();

        MainMethodResult result = invokeMain("-textFile", existingPhoneBill.getAbsolutePath(), customer, callerNumber, calleeNumber, startDate, startTime, endDate, endTime);
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    public void testNonNumbericPhoneNumber() throws IOException {
        var bill = TextDumperTest.getPopulatedPhoneBill();
        var bspriggs = new File("bspriggs/bspriggs-x.txt");

        bspriggs.deleteOnExit();

        new TextDumper(bspriggs.toPath()).dump(bill);

        MainMethodResult result = invokeMain("-textFile bspriggs/bspriggs-x.txt Test3 ABC-123-4567 123-456-7890 03/03/2018 12:00 03/03/2018 16:00".split(" "));

        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
        assertThat(result.getTextWrittenToStandardError(), containsString("Invalid phone number"));
    }

    @Test
    public void testMaformedStartTime() {
        MainMethodResult result = invokeMain("-textFile bspriggs/bspriggs-x.txt Test4 123-456-7890 234-567-8901 03/03/2018 12:XX 03/03/2018 16:00".split(" "));

        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
        assertThat(result.getTextWrittenToStandardError(), containsString("date"));
    }
}