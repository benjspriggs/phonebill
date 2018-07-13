package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
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

    private File generateExistingPhoneBill() throws IOException {
        File file = folder.newFile();
        TextDumper dumper = new TextDumper(file.toPath());

        dumper.dump(TextDumperTest.getPopulatedPhoneBill());

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

        MainMethodResult result = invokeMain(customer, callerNumber, calleeNumber, startDate, startTime, endDate, endTime);

        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    }

    /**
     * Tests that invoking the command line with an actual phone bill adds the new phone bill to the file.
     */
    @Test
    public void testExistingPhoneBill() throws IOException {
        File existingPhoneBill = generateExistingPhoneBill();

        var customer = "customer";
        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var startDate = generateDate();
        var startTime = generateTime();
        var endDate = generateDate();
        var endTime = generateTime();

        MainMethodResult result = invokeMain("-textFile", existingPhoneBill.getAbsolutePath(), customer, callerNumber, calleeNumber, startDate, startTime, endDate, endTime);

        assertThat(result.getExitCode(), equalTo(0));
    }
}