package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.generatePhoneCall;
import static edu.pdx.cs410J.bspriggs.TextDumperTest.getPopulatedPhoneBill;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link PhoneBillServlet}.  It uses mockito to
 * provide mock http requests and responses.
 */
public class PhoneBillServletTest {
    private AbstractPhoneBill<AbstractPhoneCall> bill;
    private PhoneBillServlet servletWithPhoneBill;
    private Date startDate;
    private Date endDate;
    private List<AbstractPhoneCall> callsInSearchRange;

    @Before
    public void setUp() {
        this.bill = getPopulatedPhoneBill();
        this.servletWithPhoneBill = new PhoneBillServlet();
        servletWithPhoneBill.addPhoneBill(bill);

        var pair = this.bill.getPhoneCalls().stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(2)
                .collect(Collectors.toList());

        this.startDate = pair.get(0).getStartTime();
        this.endDate = pair.get(1).getEndTime();

        this.callsInSearchRange = this.bill.getPhoneCalls()
                .stream()
                .filter(call -> call.getStartTime().compareTo(startDate) >= 0
                        && call.getEndTime().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
    }

    private void addCallToRequest(PhoneBill bill, PhoneCall call, HttpServletRequest request) {
        when(request.getParameter("customer")).thenReturn(bill.getCustomer());
        when(request.getParameter("callerNumber")).thenReturn(call.getCaller());
        when(request.getParameter("calleeNumber")).thenReturn(call.getCallee());
        when(request.getParameter("startTime")).thenReturn(call.getStartTimeString());
        when(request.getParameter("endTime")).thenReturn(call.getEndTimeString());
    }

    @Test
    public void testInitiallyServletHasNoPhoneBills() throws IOException {
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
     * If the phone bill does not exist, a new one should be created.
     *
     * @throws IOException
     * @throws ParserException
     */
    @Test
    public void testAddNewPhoneBillWithCall() throws IOException, ParserException {
        PhoneBillServlet servlet = new PhoneBillServlet();

        String customer = "customer";
        PhoneBill bill = new PhoneBill(customer);
        PhoneCall call = (PhoneCall) generatePhoneCall();

        bill.addPhoneCall(call);

        HttpServletRequest request = mock(HttpServletRequest.class);
        addCallToRequest(bill, call, request);

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
     * @throws IOException
     * @throws ParserException
     */
    @Test
    public void testGetReturnsAllCallsFormatted() throws IOException, ParserException {
        PhoneBillServlet servlet = new PhoneBillServlet();

        String customer = "customer";
        PhoneBill bill = new PhoneBill(customer);
        PhoneCall call = (PhoneCall) generatePhoneCall();

        bill.addPhoneCall(call);

        HttpServletRequest postRequest = mock(HttpServletRequest.class);
        addCallToRequest(bill, call, postRequest);

        HttpServletResponse postResponse = mock(HttpServletResponse.class);
        PrintWriter pw = mock(PrintWriter.class);

        when(postResponse.getWriter()).thenReturn(pw);

        // we add the phone bill
        servlet.doPost(postRequest, postResponse);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(postRequest.getParameter("customer")).thenReturn(bill.getCustomer());
        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        servlet.doGet(request, response);

        verify(pw).println(Messages.formatPhoneBill(bill));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testSearchPhoneCallsReturnsCallsInRange() throws IOException {
        PhoneBillServlet servlet = new PhoneBillServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("customer")).thenReturn(bill.getCustomer());
        when(request.getParameter("startTime")).thenReturn(PhoneCall.formatDate(startDate));
        when(request.getParameter("endTime")).thenReturn(PhoneCall.formatDate(endDate));

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter pw = mock(PrintWriter.class);

        // and we get the phone bill
        servlet.doGet(request, response);

        verify(pw).println(Messages.formatPhoneCalls(this.callsInSearchRange));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Get on a customer that doesn't exist doesn't return anything.
     *
     * @throws IOException
     */
    @Test
    public void testGetCustomerReturnsInvalid() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("customer")).thenReturn("asdfasflk");

        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        this.servletWithPhoneBill.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Post on a customer with bad params give a bad status code.
     *
     * @throws IOException
     */
    @Test
    public void testPostBadParams() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("customer")).thenReturn("asdfasflk");
        when(request.getParameter("extra")).thenReturn(bill.getCustomer());

        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        this.servletWithPhoneBill.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Get with missing params fails.
     *
     * @throws IOException
     */
    @Test
    public void testGetMissingParams() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        this.servletWithPhoneBill.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Post with missing params fails.
     *
     * @throws IOException
     */
    @Test
    public void testPostMissingParams() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        this.servletWithPhoneBill.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Search on nonexistent bill returns not found.
     *
     * @throws IOException
     */
    @Test
    public void testSearchMissingPhoneBill() throws IOException {
        PhoneBillServlet servlet = new PhoneBillServlet();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("customer")).thenReturn("NONEXISTENT_CUSTOMER");
        when(request.getParameter("startTime")).thenReturn(PhoneCall.formatDate(startDate));
        when(request.getParameter("endTime")).thenReturn(PhoneCall.formatDate(endDate));

        HttpServletResponse response = mock(HttpServletResponse.class);

        // and we get the phone bill
        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
