package edu.pdx.cs410J.bspriggs;

import java.util.*;

public abstract class MainClassSkeleton<T> {
    public interface Argument {
        String name();
        String description();
        List<String> consume(List<String> args, Map<String, Object> context);
    }

    public interface Option extends Argument {
        boolean isFlag();
    }

    static Argument popN(String name, String description, int n) {
        return new Argument() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> consume(List<String> args, Map<String, Object> context) {
                var l = new ArrayList<>();
                for (var i = 0; i < n; ++i)
                    l.add(args.remove(0));
                context.put(name(), l);
                return args;
            }
        };
    }

    static Argument pop(String name, String description) {
        return new Argument() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> consume(List<String> args, Map<String, Object> context) {
                context.put(name(), args.remove(0));
                return args;
            }
        };
    }

    static Option popOpt(String name, String description) {
        var isFlag = name.split(" ").length == 0;

        return new Option() {
            @Override
            public boolean isFlag() {
                return isFlag;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> consume(List<String> args, Map<String, Object> context) {
                if (!isFlag()) {
                    args.remove(0);
                }

                context.put(name(), args.remove(0));
                return args;
            }
        };
    }

    abstract List<Argument> getArguments();
    abstract List<Option> getOptions();

    private String usage(String reason) {
        var b = new StringBuilder();

        if (reason != null) {
            b.append(reason);
            b.append(System.lineSeparator());
        }

        return b.toString() +
                "usage: " + getClass().getCanonicalName() + " [options] <args>\n" +
                "args are (in this order):\n" +
                build(getArguments()) +
                "options are (options may appear in any order):\n" +
                build(new ArrayList<>(getOptions())) +
                "Date and time should be in the format: " + PhoneCall.DATE_FORMAT_STRING;
    }

    private String build(List<Argument> arguments) {
        final var b = new StringBuilder();

        for (var a : arguments) {
            b.append(String.format("  %-10s\t%s", a.name(), a.description()));
            b.append(System.lineSeparator());
        }

        return b.toString();
    }

    abstract String Readme();

    abstract T doWork(HashMap<String, Object> context) throws Exception;

    public static void wrapMain(MainClassSkeleton s, String[] args) {
        var context = new HashMap<String, Object>();

        var arguments = Arrays.asList(args);

        if (arguments.size() < s.getArguments().size()) {
            System.err.println(s.usage("Missing command line arguments"));
            System.exit(1);
        }

        if (arguments.size() == 0) {
            System.err.println(s.usage(null));
            System.exit(1);
        }

        if (arguments.contains("-README")) {
            System.out.println(s.usage(s.Readme()));
            System.exit(0);
        }

        try {
            List<Option> options = s.getOptions();
            List<Argument> argumentList = s.getArguments();

            for (var opt : options) {
                arguments = opt.consume(arguments, context);
            }

            for (var arg : argumentList) {
                arguments = arg.consume(arguments, context);
            }


            if (arguments.size() != 0) {
                System.err.println(s.usage("Extra command line arguments"));
                System.exit(1);
            }

            s.doWork(context);
        } catch (final Exception e) {
            System.err.println(s.usage(e.toString()));
            System.exit(1);
        }
    }

}
