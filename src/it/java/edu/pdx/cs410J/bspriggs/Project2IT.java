package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project2IT extends Project1IT {
    /**
     * Invokes the main method of {@link Project2} with the given arguments.
     */
    protected MainMethodResult invokeMain(String... args) {
        return invokeMain(Project2.class, args);
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        var bspriggsDir = Paths.get("./bspriggs");

        if (!bspriggsDir.toFile().exists())
            Files.createDirectory(Paths.get("./bspriggs"));
    }

    @After
    public void tearDown() {
        var bspriggsDir = Paths.get("./bspriggs").toFile();
        String[] entries = bspriggsDir.list();
        for (String s : entries) {
            File currentFile = new File(bspriggsDir.getPath(), s);
            currentFile.delete();
        }
        bspriggsDir.delete();
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
     * Tests that invoking the main method with no arguments issues an error.
     */
    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    }

    /**
     * Tests that invoking the main method with README issues readme text.
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
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

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
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

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
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

        MainMethodResult result = invokeMain(startFormatted, endFormatted,
                "-textFile", existingPhoneBill.getAbsolutePath(), "", callerNumber, calleeNumber);
        assertThat(result.getExitCode(), equalTo(1));
    }

    /**
     * Tests that when given a non-numberic phone number, the commmand line rejects with a relevant message.
     *
     * @throws IOException
     */
    @Test
    public void testNonNumbericPhoneNumber() throws IOException {
        var bill = getPopulatedPhoneBill();
        var bspriggs = new File("bspriggs/bspriggs-x.txt");

        bspriggs.deleteOnExit();

        new TextDumper(bspriggs.toPath()).dump(bill);

        MainMethodResult result = invokeMain("-textFile bspriggs/bspriggs-x.txt Test3 ABC-123-4567 123-456-7890 03/03/2018 12:00 AM 03/03/2018 16:00 PM".split(" "));

        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
        assertThat(result.getTextWrittenToStandardError(), containsString("Invalid phone number"));
    }

    /**
     * Tests that when given an incorrect start time, the commmand line rejects with a relevant message.
     *
     * @throws IOException
     */
    @Test
    public void testMaformedStartTime() {
        var start = "01/08/2018/1 1:00 AM";
        var end = "01/08/2018 10:00 AM";
        MainMethodResult result = invokeMain(
                start.split(" "),
                end.split(" "),
                "-textFile bspriggs/bspriggs-x.txt Test4 123-456-7890 234-567-8901".split(" "));

        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
        assertThat(result.getTextWrittenToStandardError(), containsString("parsed"));
    }

    /**
     * Tests that when given an incorrect end time, the commmand line rejects with a relevant message.
     *
     * @throws IOException
     */
    @Test
    public void testMalformedEndTime() {
        var start = "01/08/2018 1:00 AM";
        var end = "01/08/2018/1 10:00 AM";
        MainMethodResult result = invokeMain(
                start.split(" "),
                end.split(" "),
                "-textFile bspriggs/bspriggs-x.txt Test5 123-456-7890 234-567-8901".split(" "));

        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
        assertThat(result.getTextWrittenToStandardError(), containsString("parsed"));
    }

    /**
     * Tests that when starting a new phone bill file with a relative path, the phone bill is created.
     *
     * @throws IOException
     */
    @Test
    public void testStartNewFile() throws IOException {
        var start = "01/08/2018 1:00 AM";
        var end = "01/08/2018 11:00 AM";
        var bspriggsDir = Paths.get("./bspriggs");

        if (!bspriggsDir.toFile().exists())
            Files.createDirectory(Paths.get("./bspriggs"));

        var bspriggs = new File("bspriggs/bspriggs.txt");
        bspriggs.deleteOnExit();

        MainMethodResult result = invokeMain(
                start.split(" "),
                end.split(" "),
                "-textFile bspriggs/bspriggs.txt -print Project2 123-456-7890 234-567-9081".split(" "));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardError(), is(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString("Project2"));
    }

    /**
     * Tests that when opening an existing phone bill file with a relative path, the phone bill is updated.
     *
     * @throws IOException
     */
    @Test
    public void testExistingFile() throws IOException {
        var start = "01/08/2018 1:00 AM";
        var end = "01/08/2018 9:00 AM";
        var bill = getPopulatedPhoneBill();
        var bspriggs = new File("bspriggs/bspriggs-x.txt");

        bspriggs.deleteOnExit();

        new TextDumper(bspriggs.toPath()).dump(bill);
        MainMethodResult result = invokeMain(start.split(" "),
                end.split(" "),
                "-textFile bspriggs/bspriggs.txt -print Project2 123-456-7890 456-789-0123".split(" "));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardError(), is(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString("Project2"));
    }
}