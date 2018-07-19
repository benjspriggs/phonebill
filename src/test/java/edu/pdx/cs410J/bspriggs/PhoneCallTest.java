package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final String validPhoneNumber = "503-333-3333";
  private final String validTime = "1/15/2031 2:33 AM";

  private PhoneCall getPhoneCall() throws ParserException {
    return new PhoneCall(validPhoneNumber, validPhoneNumber, validTime, validTime);
  }

  @Test
  public void getStartTimeStringNeedsToBeImplemented() throws ParserException {
    PhoneCall call = getPhoneCall();
    assertThat(call.getStartTimeString(), not(nullValue()));
  }

  @Test
  public void getCallerNeedsToBeImplemented() throws ParserException {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCaller(), not(nullValue()));
  }

  @Test
  public void getCalleeNeedsToBeImplemented() throws ParserException {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCallee(), not(nullValue()));
  }

  @Test
  public void passingInvalidPhoneNumberFails() throws Exception {
    thrown.expect(Exception.class);
    new PhoneCall("cat", "dog", validTime, validTime);
  }

  @Test
  public void passingInvalidDateFails() throws Exception {
    thrown.expect(Exception.class);
    new PhoneCall(validPhoneNumber, validPhoneNumber, "cat", "dog");
  }

  @Test
  public void testStartingAfterEnding() throws Exception {
    thrown.expect(Exception.class);
    var end = new Date(10000L);
    var start = new Date(end.getTime() + 100000);
    new PhoneCall(validPhoneNumber, validPhoneNumber, PhoneCall.formatDate(start), PhoneCall.formatDate(end));
  }
}
