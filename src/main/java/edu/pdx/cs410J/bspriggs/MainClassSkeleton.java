package edu.pdx.cs410J.bspriggs;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MainClassSkeleton<T> {
    public interface Argument {
        String name();
        String description();
        List<String> consume(List<String> args, Map<String, Object> context);
    }

    public interface Option extends Argument {
        boolean isFlag();
    }

    public static Argument popN(String name, String description, int n) {
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

    public static Argument pop(String name, String description) {
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

    public static Option popOpt(String name, String description) {
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

    abstract String usage(String reason);
    abstract String Readme();

    abstract T doWork(HashMap<String, Object> context) throws Exception;


    public void wrapMain(String[] args) {
        var context = new HashMap<String, Object>();

        var arguments = Arrays.asList(args);

        if (arguments.size() == 0) {
            System.err.println(usage(null));
            System.exit(1);
        }

        if (arguments.contains("-README")) {
            System.out.println(usage(Readme()));
            System.exit(0);
        }

        try {
            if (arguments.size() < getArguments().size()) {
                System.err.println(usage("Missing command line arguments"));
                System.exit(1);
            }

            for (var opt : getOptions()) {
                arguments = opt.consume(arguments, context);
            }

            for (var arg : getArguments()) {
                arguments = arg.consume(arguments, context);
            }


            if (arguments.size() != 0) {
                System.err.println(usage("Extra command line arguments"));
                System.exit(1);
            }

            doWork(context);
        } catch (final Exception e) {
            System.err.println(usage(e.toString()));
            System.exit(1);
        }
    }

}
