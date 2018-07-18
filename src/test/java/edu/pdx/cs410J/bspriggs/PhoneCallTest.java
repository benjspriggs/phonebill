package edu.pdx.cs410J.bspriggs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final String validPhoneNumber = "503-333-3333";
    private final String validTime = "1/15/2031 2:33 am";

  private PhoneCall getPhoneCall() {
    try {
      return new PhoneCall(validPhoneNumber, validPhoneNumber, validTime, validTime);
    } catch (Exception e) {
      return null;
    }
  }

  @Test
  public void getStartTimeStringNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getStartTimeString(), not(nullValue()));
  }

  @Test
  public void getCallerNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCaller(), not(nullValue()));
  }

  @Test
  public void getCalleeNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCallee(), not(nullValue()));
  }

  @Test
  public void forProject1ItIsOkayIfGetStartTimeReturnsNull() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getStartTime(), is(nullValue()));
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
        var start = new Date(end.getTime() + 230);
        new PhoneCall(validPhoneNumber, validPhoneNumber, PhoneCall.DATE_FORMAT.format(start), PhoneCall.DATE_FORMAT.format(end));
    }
}
