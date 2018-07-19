package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project3IT extends Project2IT {
    protected MainMethodResult invokeMain(String... args) {
        return this.invokeMain(new String[]{}, new String[]{}, args);
    }

    /**
     * Invokes the main method of {@link Project3} with the given arguments.
     */
    protected MainMethodResult invokeMain(String[] start, String[] end, String... args) {
        var list = new ArrayList<>(Arrays.asList(args));
        list.addAll(Arrays.asList(start));
        list.addAll(Arrays.asList(end));
        var fullArgs = list.toArray(new String[0]);
        return invokeMain(Project3.class, fullArgs);
    }

    @Test
    public void testCommandlineAcceptsNewDateFormat() {
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "customer", generatePhoneNumber(), generatePhoneNumber());

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    @Test
    public void testPrettyPrintsContentsOfFile() throws IOException {
        var printer = new PrettyPrinter();
        var prettyFile = folder.newFile();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "-pretty", prettyFile.getAbsolutePath(),
                "customer", generatePhoneNumber(), generatePhoneNumber());

        var expectedBuffer = new ByteArrayOutputStream();
        printer.dumpTo(bill, expectedBuffer);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), is(""));
        assertThat(String.join(TextDumper.NEWLINE, Files.readAllLines(prettyFile.toPath())), containsString(new String(expectedBuffer.toByteArray())));
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
        var startTime = "21:11";
        var endTime = "22:11";

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, "am", endDate, endTime, "pm");

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("date format"));
    }

    @Test
    public void testInvalidStartAndEndTimes() {
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

        var result = invokeMain(startFormatted, endFormatted,
                "customer", generatePhoneNumber(), generatePhoneNumber());

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("after"));
    }

    @Test
    public void testUsageHasProject3Text() {
        MainMethodResult result = invokeMain("-README");

        assertThat(result.getTextWrittenToStandardOut(), containsString("Project3"));
        assertThat(result.getTextWrittenToStandardOut(), not(containsString("Project2")));
        assertThat(result.getTextWrittenToStandardOut(), not(containsString("Project1")));
    }
}