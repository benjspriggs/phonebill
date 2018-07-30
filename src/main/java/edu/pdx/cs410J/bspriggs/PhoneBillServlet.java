package edu.pdx.cs410J.bspriggs;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>PhoneBill</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class PhoneBillServlet extends HttpServlet
{
    public static final String CUSTOMER_PARAMETER = "customer";
    public static final String PHONE_CALLER_PARAMETER = "callerNumber";
    public static final String PHONE_CALLEE_PARAMETER = "calleeNumber";
    public static final String START_TIME_PARAMETER = "startTime";
    public static final String END_TIME_PARAMETER = "endTime";

    private HashMap<String, PhoneBill> phoneBills = new HashMap<>();

    /**
     * Handles an HTTP GET request from a client by writing
     * all of the phone bills, formatted by {@link PrettyPrinter#dumpTo(AbstractPhoneBill, OutputStream)}.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType( "text/plain" );

        String customer = getParameter(CUSTOMER_PARAMETER, request);

        if (customer == null) {
            missingRequiredParameter(response, CUSTOMER_PARAMETER);
            return;
        }

        PrintWriter pw = response.getWriter();
        String startTime = getParameter(START_TIME_PARAMETER, request);
        String endTime = getParameter(END_TIME_PARAMETER, request);

        PhoneBill bill = getPhoneBill(customer);

        if (startTime == null && endTime == null) {
            if (bill == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                pw.println(Messages.formatPhoneBillPretty(bill));
                pw.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
            return;
        }

        if (startTime == null || endTime == null) {
            if (startTime == null)
                missingRequiredParameter(response, START_TIME_PARAMETER);
            else
                missingRequiredParameter(response, END_TIME_PARAMETER);
            return;
        }

        List<AbstractPhoneCall> matches = getPhoneCallsInPeriod(customer, startTime, endTime);

        if (matches == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        pw.println(Messages.formatPhoneCalls(matches));
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns all of the calls for a customer that happened in a time range.
     *
     * @param customer
     * @param startTime
     * @param endTime
     * @return
     */
    private List<AbstractPhoneCall> getPhoneCallsInPeriod(String customer, String startTime, String endTime) {
        PhoneBill bill = getPhoneBill(customer);

        if (bill == null)
            return null;

        Function<String, Date> makeItDate = s -> new Date(LocalDateTime.parse(s, PhoneCall.DATE_FORMAT).toInstant(ZoneOffset.UTC).toEpochMilli());

        var start = makeItDate.apply(startTime);
        var end = makeItDate.apply(endTime);

        Collection<AbstractPhoneCall> calls = bill.getPhoneCalls();
        return calls
                .stream()
                .map(AbstractPhoneCall.class::cast)
                .filter(call -> call.getStartTime().compareTo(start) >= 0)
                .filter(call -> call.getEndTime().compareTo(end) <= 0)
                .collect(Collectors.toList());
    }

    /**
     * Handles an HTTP POST request by storing the phone bill/ phone call.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType( "text/plain");

        String customer = getParameter(CUSTOMER_PARAMETER, request);
        String caller = getParameter(PHONE_CALLER_PARAMETER, request);
        String callee = getParameter(PHONE_CALLEE_PARAMETER, request);
        String startTime = getParameter(START_TIME_PARAMETER, request);
        String endTime = getParameter(END_TIME_PARAMETER, request);

        if (customer == null) {
            missingRequiredParameter(response, CUSTOMER_PARAMETER);
            return;
        }

        if (caller == null) {
            missingRequiredParameter(response, PHONE_CALLER_PARAMETER);
            return;
        }

        if (callee == null) {
            missingRequiredParameter(response, PHONE_CALLEE_PARAMETER);
            return;
        }

        if (startTime == null) {
            missingRequiredParameter(response, START_TIME_PARAMETER);
            return;
        }

        if (endTime == null) {
            missingRequiredParameter(response, END_TIME_PARAMETER);
            return;
        }

        PhoneBill bill = getPhoneBill(customer);

        if (bill == null) {
            bill = new PhoneBill(customer);
        }

        try {
            bill.addPhoneCall(new PhoneCall(caller, callee, startTime, endTime));
        } catch (ParserException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        addPhoneBill(bill);

        PrintWriter pw = response.getWriter();
        pw.println(Messages.formatPhoneBillPretty(bill));
        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

    @VisibleForTesting
    PhoneBill getPhoneBill(String customer) {
        return phoneBills.get(customer);
    }

    @VisibleForTesting
    void addPhoneBill(PhoneBill bill) {
        phoneBills.put(bill.getCustomer(), bill);
    }
}
