package edu.pdx.cs410J.bspriggs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class TextParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testParseEmptyFileRaisesException() {
        try {
            var file = folder.newFile();
            var parser = new TextParser(file.toPath());

            thrown.expect(ParseException.class);

            parser.parse();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseEmpyPhoneBill() {
        try {
            var file = folder.newFile();
            var bill = new PhoneBill("name");
            var dumper = new TextDumper(file.getPath());
            var parser = new TextParser(file.toPath());

            dumper.dump(bill);

            assertEquals(bill, parser.parse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParsePopulatedPhoneBill() {
        try {
            var file = folder.newFile();
            var bill = TextDumperTest.getPopulatedPhoneBill();

            var dumper = new TextDumper(file.getPath());
            var parser = new TextParser(file.toPath());

            dumper.dump(bill);

            assertEquals(bill, parser.parse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}