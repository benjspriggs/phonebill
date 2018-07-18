package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project3IT extends Project2IT {
    private MainMethodResult invokeMain(String... args) {
        return invokeMain(null, null, args);
    }

    /**
     * Invokes the main method of {@link Project3} with the given arguments.
     */
    protected MainMethodResult invokeMain(String[] start, String[] end, String... args) {
        var list = new ArrayList<>(Arrays.asList(start));
        list.addAll(Arrays.asList(end));
        list.addAll(Arrays.asList(args));
        String[] fullArgs = (String[]) list.toArray();
        return invokeMain(Project3.class, fullArgs);
    }

    private SimpleDateFormat timeFormat = new SimpleDateFormat("H:M a");
    private SimpleDateFormat timeOfDateFormat = new SimpleDateFormat("a");

    @Test
    public void testCommandlineAcceptsNewDateFormat() {
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "customer", generatePhoneNumber(), generatePhoneNumber());

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    @Test
    public void testPrettyPrintsContentsOfFile() throws IOException {
        var printer = new PrettyPrinter();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "customer", generatePhoneNumber(), generatePhoneNumber());

        var expectedBuffer = new ByteArrayOutputStream();
        printer.dumpTo(bill, expectedBuffer);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), is(not("")));
        assertThat(result.getTextWrittenToStandardOut(), containsString(new String(expectedBuffer.toByteArray())));
    }

    @Test
    public void testImproperArgumentsHaveErrorMessage() {
        var result = invokeMain("asf");

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    }

    @Test
    public void testIncorrectDateTimeFormat() {
        var startDate = "1/11/11";
        var endDate = "1/11/12";
        var startTime = "11:11";
        var endTime = "12:11";

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, "am", endDate, endTime, "pm");

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("date format"));
    }

    @Test
    public void testInvalidStartAndEndTimes() {
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.DATE_FORMAT.format(start).split(" ");
        var endFormatted = PhoneCall.DATE_FORMAT.format(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "customer", generatePhoneNumber(), generatePhoneNumber());

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("after"));
    }
}