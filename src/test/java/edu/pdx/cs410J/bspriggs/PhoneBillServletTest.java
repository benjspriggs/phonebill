package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.generatePhoneCall;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link PhoneBillServlet}.  It uses mockito to
 * provide mock http requests and responses.
 */
public class PhoneBillServletTest {

  @Test
  public void testInitiallyServletHasNoPhoneBills() throws ServletException, IOException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter pw = mock(PrintWriter.class);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    int expectedPhoneBillCount = 0;
    verify(pw).println(Messages.formatPhoneBillCount(expectedPhoneBillCount));
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }

  /**
   * POST creates a new call from the HTTP request parameters:
   * - customer
   * - callerNumber
   * - calleeNumber
   * - startTime
   * - endTime
   * <p>
   * If the phone bill does not exist, a new one should be created.
   *
   * @throws ServletException
   * @throws IOException
   * @throws ParserException
   */
  @Test
  public void testAddNewPhoneBillWithCall() throws ServletException, IOException, ParserException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    String customer = "customer";
    PhoneBill bill = new PhoneBill(customer);
    PhoneCall call = (PhoneCall) generatePhoneCall();

    bill.addPhoneCall(call);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("name")).thenReturn(bill.getCustomer());
    when(request.getParameter("callerNumber")).thenReturn(call.getCaller());
    when(request.getParameter("calleeNumber")).thenReturn(call.getCallee());
    when(request.getParameter("startTime")).thenReturn(call.getStartTimeString());
    when(request.getParameter("endTime")).thenReturn(call.getEndTimeString());

    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter pw = mock(PrintWriter.class);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);
    // verify(pw).println(Messages.formatPhoneBill(bill));
    verify(response).setStatus(HttpServletResponse.SC_OK);

    assertThat(servlet.getPhoneBill(customer), equalTo(bill));
  }

  /**
   * GET returns all calls in the phone bill formatted using the PrettyPrinter.
   *
   * @throws ServletException
   * @throws IOException
   * @throws ParserException
   */
  @Test
  public void testGetReturnsAllCallsFormatted() throws ServletException, IOException, ParserException {
    PhoneBillServlet servlet = new PhoneBillServlet();

    String customer = "customer";
    PhoneBill bill = new PhoneBill(customer);
    PhoneCall call = (PhoneCall) generatePhoneCall();

    bill.addPhoneCall(call);

    HttpServletRequest postRequest = mock(HttpServletRequest.class);
    when(postRequest.getParameter("name")).thenReturn(bill.getCustomer());
    when(postRequest.getParameter("callerNumber")).thenReturn(call.getCaller());
    when(postRequest.getParameter("calleeNumber")).thenReturn(call.getCallee());
    when(postRequest.getParameter("startTime")).thenReturn(call.getStartTimeString());
    when(postRequest.getParameter("endTime")).thenReturn(call.getEndTimeString());

    HttpServletResponse postResponse = mock(HttpServletResponse.class);
    PrintWriter pw = mock(PrintWriter.class);

    when(postResponse.getWriter()).thenReturn(pw);

    // we add the phone bill
    servlet.doPost(postRequest, postResponse);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(postRequest.getParameter("name")).thenReturn(bill.getCustomer());
    HttpServletResponse response = mock(HttpServletResponse.class);

    // and we get the phone bill
    servlet.doGet(postRequest, postResponse);

    verify(pw).println(Messages.formatPhoneBill(bill));
    verify(response).setStatus(HttpServletResponse.SC_OK);
  }
}
