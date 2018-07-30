package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import net.bytebuddy.utility.RandomString;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test that tests the REST calls made by {@link PhoneBillRestClient}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PhoneBillRestClientIT {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private PhoneBillRestClient newPhoneBillRestClient() {
    int port = Integer.parseInt(PORT);
    return new PhoneBillRestClient(HOSTNAME, port);
  }

  @Test
  public void test0GetEmptyPhoneBills() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
      thrown.expect(Exception.class);
      client.getPhoneBill(RandomString.make());
  }

  @Test
  public void test1SearchWithNoPhoneBillsThrowsException() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
      var start = generateDate();
      var startTime = PhoneCall.formatDate(start);
      var endTime = PhoneCall.formatDate(generateDateAfter(start));

      thrown.expect(Exception.class);
      client.searchPhoneCalls(RandomString.make(), startTime, endTime);
  }

  @Test
  public void test2AddCallCreatesBill() throws IOException, ParserException {
    PhoneBillRestClient client = newPhoneBillRestClient();

      var bill = new PhoneBill(RandomString.make());
      var call = generatePhoneCall();

      bill.addPhoneCall(call);

      var actual = client.postNewCall(bill.getCustomer(),
              call.getCaller(),
              call.getCallee(),
              call.getStartTimeString(),
              call.getEndTimeString());

      assertThat(bill, equalTo(actual));
  }
}
