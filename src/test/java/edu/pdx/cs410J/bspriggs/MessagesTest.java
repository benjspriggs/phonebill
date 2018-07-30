package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import net.bytebuddy.utility.RandomString;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.getPopulatedPhoneBill;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class MessagesTest {

  @Test
  public void missingRequiredParameter() {
      var anything = RandomString.make();
      assertThat(Messages.missingRequiredParameter(anything), containsString(anything));
  }

  @Test
  public void formatPhoneBillPretty() throws IOException {
      var bill = getPopulatedPhoneBill();

      assertThat(Messages.formatPhoneBillPretty(bill), containsString(bill.getCustomer()));
  }

  @Test
  public void formatPhoneCalls() {
      var bill = getPopulatedPhoneBill();
      var message = Messages.formatPhoneCalls(new ArrayList<>(bill.getPhoneCalls()));
      for (var call : bill.getPhoneCalls()) {
          assertThat(message, containsString(call.toString()));
      }
  }

  @Test
  public void parsePhoneBill() throws ParserException, IOException {
      var bill = getPopulatedPhoneBill();
      var out = new ByteArrayOutputStream();
      var dumper = new TextDumper();
      dumper.dumpTo(bill, out);

      var parsedBill = Messages.parsePhoneBill(out.toString());

      assertThat(parsedBill.getCustomer(), equalTo(bill.getCustomer()));

      for (var call : bill.getPhoneCalls()) {
          assertThat("All calls must be successfully parsed", bill.getPhoneCalls().contains(call));
      }
  }

    @Test
    public void formatPhoneBill() throws IOException {
        var bill = getPopulatedPhoneBill();
        var message = Messages.formatPhoneBill(bill);

        assertThat(message, containsString(bill.getCustomer()));

        for (var call : bill.getPhoneCalls()) {
            assertThat(message, containsString(TextDumper.serialize(call)));
        }
  }
}
