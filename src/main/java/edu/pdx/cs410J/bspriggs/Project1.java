package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

/**
 * The main class for the CS410J Phone Bill Project
 */
public class Project1 {
  private static final Map<String, String> ARGUMENTS = Map.ofEntries(
          entry("customer", "Person whose phone bill we’re modeling"),
          entry("callerNumber", "Phone number of caller"),
          entry("calleeNumber", " Phone number of person who was called"),
          entry("startTime", " Date and time call began (24-hour time)"),
          entry("endTime", " Date and time call ended (24-hour time)")
  );

  private static final Map<String, String> OPTIONS = Map.ofEntries(
          entry("-print", "Prints a description of the new phone call"),
          entry("-README", "Prints a README for this project and exits")
  );

  private static String build(Map<String, String> f) {
    var b = new StringBuilder();

    for (Map.Entry<String, String> pair : f.entrySet()) {
      b.append("  ");
      b.append(pair.getKey());
      b.append("\t\t");
      b.append(pair.getValue());
      b.append('\n');
    }

    return b.toString();
  }

  private static String usage() {
    return "usage: java edu.pdx.cs410J.bspriggs.Project1 [options] <args>\n" +
            "args are (in this order):\n" +
            build(ARGUMENTS) +
            "options are (options may appear in any order):\n" +
            build(OPTIONS) +
            "Date and time should be in the format: mm/dd/yyyy hh:mm";
  }

  public static void main(String[] args) {
    PhoneCall call = new PhoneCall();  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    System.err.println("Missing command line arguments");
    for (String arg : args) {
      System.out.println(arg);
    }
    System.err.println(usage());
    System.exit(1);
  }

}