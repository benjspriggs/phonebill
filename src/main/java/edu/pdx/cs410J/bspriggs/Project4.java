package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 extends Project1 {

    public static final List<Option> OPTIONS = Arrays.asList(
            popOpt("-host hostname", "Host computer on which the server runs"),
            popOpt("-port port", "Port on which the server is listening"),
            popOpt("-search", "Phone calls should be searched for")
    );

    public static void main(String... args) {
        wrapMain(new Project4(), args);
    }

    /**
     * Makes sure that the give response has the expected HTTP status code
     * @param code The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode( int code, HttpRequestHelper.Response response )
    {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                                response.getCode(), response.getContent()));
        }
    }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    @Override
    List<List<Argument>> getArguments() {
        return Arrays.asList(
                ARGUMENTS,
                Arrays.asList(
                        pop("customer", "Person whose phone bill we’re modeling"),
                        popN("startTime", "Date and time call began (24-hour time)", 3),
                        popN("endTime", "Date and time call ended (24-hour time)", 3))
        );
    }

    @Override
    List<Option> getOptions() {
        return Stream.concat(Project1.OPTIONS.stream(), OPTIONS.stream())
                .collect(Collectors.toList());
    }

    @Override
    String Readme() {
        return "A REST client for a PhoneBill server. Allows creation, viewing, and searching of different phone calls" +
                "on a remote (or local) server.";
    }

    @Override
    PhoneBill doWork(HashMap context) throws Exception {
        var host = context.get("-host");
        var p = context.get("-port");
        var s = context.get("-search");
        var c = context.get("customer");
        var start = context.get("startTime");
        var end = context.get("endTime");

        if (host == null) {
            error(MISSING_ARGS);
        }

        if (p == null) {
            error(MISSING_ARGS);
        }

        if (c == null) {
            error(MISSING_ARGS);
        }

        String customer = (String) c;
        String hostname = (String) host;
        int port = Integer.parseInt((String) p);

        PhoneBillRestClient client = new PhoneBillRestClient(hostname, port);

        if (s != null && (boolean) s) {
            if (start == null) {
                error(MISSING_ARGS);
            }

            if (end == null) {
                error(MISSING_ARGS);
            }

            String startTime = String.join(" ", (List<String>) start);
            String endTime = String.join(" ", (List<String>) end);
            System.out.println(client.searchPhoneCalls(customer, startTime, endTime));
            return null;
        } else {
            // we're making a  new one
            PhoneBill bill = super.doWork(context);
            PhoneCall call = (PhoneCall) bill.getPhoneCalls().stream().findFirst().get();
            var returnedBill = client.postNewCall(bill.getCustomer(),
                    call.getCaller(), call.getCallee(), call.getStartTimeString(), call.getEndTimeString());

            System.out.println(Messages.formatPhoneBillPretty(returnedBill));
            return returnedBill;
        }
    }
}