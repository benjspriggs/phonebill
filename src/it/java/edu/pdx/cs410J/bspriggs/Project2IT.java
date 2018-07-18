package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Project2IT extends Project1IT {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MainMethodResult invokeMain(String... args) {
        return invokeMain(new String[]{}, new String[]{}, args);
    }

    /**
     * Invokes the main method of {@link Project2} with the given arguments.
     */
    protected MainMethodResult invokeMain(String[] start, String[] end, String... args) {
        var list = new ArrayList<>(Arrays.asList(args));
        list.addAll(Arrays.asList(start));
        list.addAll(Arrays.asList(end));
        var fullArgs = list.toArray(new String[list.size()]);
        return invokeMain(Project2.class, fullArgs);
    }

    protected File generateExistingPhoneBill(AbstractPhoneBill with) throws IOException {
        File file = folder.newFile();
        TextDumper dumper = new TextDumper(file.toPath());

        if (with == null) {
            with = getPopulatedPhoneBill();
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
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        MainMethodResult result = invokeMain(startFormatted, endFormatted,
                "-textFile", "", customer, callerNumber, calleeNumber);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    }

    /**
     * Tests that invoking the command line with an actual phone bill adds the new phone bill to the file.
     */
    @Test
    public void testExistingPhoneBill() throws IOException {
        var bill = getPopulatedPhoneBill();
        File existingPhoneBill = generateExistingPhoneBill(bill);

        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        MainMethodResult result = invokeMain(startFormatted, endFormatted,
                "-textFile", existingPhoneBill.getAbsolutePath(), bill.getCustomer(), callerNumber, calleeNumber);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    /**
     * Test that providing a different customer exits.
     */
    @Test
    public void testExistingPhoneBillNameMismatch() throws IOException {
        var bill = getPopulatedPhoneBill();
        File existingPhoneBill = generateExistingPhoneBill(bill);

        var customer = "new customer";
        var calleeNumber = generatePhoneNumber();
        var callerNumber = generatePhoneNumber();
        var start = generateDate();
        var end = generateDate();
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        MainMethodResult result = invokeMain(startFormatted, endFormatted,
                "-textFile", existingPhoneBill.getAbsolutePath(), "", callerNumber, calleeNumber);
        assertThat(result.getExitCode(), equalTo(1));
    }
}