package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
}