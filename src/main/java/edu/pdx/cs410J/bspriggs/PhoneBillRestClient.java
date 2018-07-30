package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

import static edu.pdx.cs410J.bspriggs.PhoneBillServlet.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send dictionary entries.
 */
public class PhoneBillRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

    private final String url;


    /**
     * Creates a client to the Phone Bil REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public PhoneBillRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    public String getPhoneBill(String customer) throws IOException {
        Response response = get(this.url, CUSTOMER_PARAMETER, customer);
        return response.getContent();
    }

    public PhoneBill postNewCall(String customer, String callerNumber, String calleeNumber, String startTimeAndDate, String endTimeAndDate) throws IOException, ParserException {
        Response response = post(this.url,
                CUSTOMER_PARAMETER, customer,
                PHONE_CALLER_PARAMETER, callerNumber,
                PHONE_CALLEE_PARAMETER, calleeNumber,
                START_TIME_PARAMETER, startTimeAndDate,
                END_TIME_PARAMETER, endTimeAndDate);
        throwExceptionIfNotOkayHttpStatus(response);
        return Messages.parsePhoneBill(response.getContent());
    }

    public String searchPhoneCalls(String customer, String startTimeAndDate, String endTimeAndDate) throws IOException {
        Response response = get(this.url,
                CUSTOMER_PARAMETER, customer,
                START_TIME_PARAMETER, startTimeAndDate,
                END_TIME_PARAMETER, endTimeAndDate);
        throwExceptionIfNotOkayHttpStatus(response);
        return response.getContent();
    }

    private Response throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getCode();
      if (code != HTTP_OK) {
        throw new PhoneBillRestException(code);
      }
      return response;
    }

    private class PhoneBillRestException extends RuntimeException {
      public PhoneBillRestException(int httpStatusCode) {
        super("Got an HTTP Status Code of " + httpStatusCode);
      }
    }

}
