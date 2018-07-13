package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TextParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testParseEmptyFileRaisesException() throws IOException, ParserException {
        var file = folder.newFile();
        var parser = new TextParser(file.toPath());

        thrown.expect(ParserException.class);

        parser.parse();
    }

    @Test
    public void testParseEmpyPhoneBill() throws IOException, ParserException {
        var file = folder.newFile();
        var bill = new PhoneBill("name");
        var dumper = new TextDumper(file.getPath());
        var parser = new TextParser(file.toPath());

        dumper.dump(bill);
        var parsedBill = parser.parse();

        assertThat(parsedBill.getCustomer(), is(equalTo(bill.getCustomer())));
        // hamcrest doesn't have a 'contains all'
        assertThat(parsedBill.getPhoneCalls().toString(), (equalTo(bill.getPhoneCalls().toString())));
    }

    @Test
    public void testParsePopulatedPhoneBill() throws IOException, ParserException {
        var file = folder.newFile();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var dumper = new TextDumper(file.getPath());
        var parser = new TextParser(file.toPath());

        dumper.dump(bill);
        var parsedBill = parser.parse();

        assertThat(parsedBill.getCustomer(), is(equalTo(bill.getCustomer())));
        // hamcrest doesn't have a 'contains all'
        assertThat(parsedBill.getPhoneCalls().toString(), (equalTo(bill.getPhoneCalls().toString())));
    }

    @Test
    public void testParseMissingCustomerName() throws IOException, ParserException {
        var file = folder.newFile();
        var parser = new TextParser(file.toPath());

        thrown.expect(ParserException.class);
        parser.parse();
    }

    @Test
    public void testParseMalformedLine() throws IOException {
        var file = folder.newFile();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var dumper = new TextDumper(file.getPath());
        var parser = new TextParser(file.toPath());
        var dummyString = "dummy string";

        dumper.dump(bill);

        var buffer = new FileOutputStream(file, true);
        buffer.write(dummyString.getBytes());

        try {
            var parsedBill = parser.parse();
            fail("No exception thrown: " + parsedBill);
        } catch (ParserException e) {
            assertThat(e.getMessage(), containsString(dummyString));
        }
    }
}