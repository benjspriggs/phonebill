package edu.pdx.cs410J.bspriggs;

import java.util.*;

public abstract class MainClassSkeleton<T> {
    public static final String MISSING_ARGS = "Missing command line arguments";
    public static final String EXTRA_ARGS = "Extra command line arguments";
    /**
     * Represents a mandatory command line argument.
     */
    public interface Argument {
        String name();

        String description();

        List<String> consume(List<String> args, Map<String, Object> context);

        int length();
    }

    /**
     * Represents an option that can appear in any order.
     */
    public interface Option extends Argument {
        boolean isFlag();
    }

    /**
     * Pops the first n arguments and stores it in an {@link ArrayList}.
     *
     * @param name
     * @param description
     * @param n
     * @return A formatted argument that will consume n items.
     */
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
                var l = args.subList(0, n);
                context.put(name(), l);
                return args.subList(n, args.size());
            }

            @Override
            public int length() {
                return n;
            }
        };
    }

    /**
     * A mandatory positional argument generator.
     * @param name
     * @param description
     * @return An {@link Argument} that consumes a single arg from an argument list.
     */
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
                context.put(name(), args.get(0));
                return args.subList(1, args.size());
            }

            @Override
            public int length() {
                return 1;
            }
        };
    }

    /**
     * Similar for {@link MainClassSkeleton#pop(String, String)}.
     * @param name
     * @param description
     * @return An {@link Option} that consumes an optional flag or non-flag option.
     */
    static Option popOpt(String name, String description) {
        final var pair = name.split(" ");
        var isFlag = pair.length <= 1;
        var actualName = isFlag ? name : pair[0];

        return new Option() {
            @Override
            public boolean isFlag() {
                return isFlag;
            }

            @Override
            public String name() {
                return actualName;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> consume(List<String> args, Map<String, Object> context) {
                if (!args.contains(name()))
                    return args;

                var pos = args.indexOf(name());
                var copy = new ArrayList<>(args);

                if (isFlag()) {
                    context.put(name(), true);
                    copy.remove(pos);
                } else {
                    copy.remove(pos);
                    context.put(name(), copy.get(pos));
                    copy.remove(pos);
                }

                return Collections.unmodifiableList(copy);
            }

            @Override
            public int length() {
                return isFlag ? 1 : 2;
            }
        };
    }

    abstract List<List<Argument>> getArguments();
    abstract List<Option> getOptions();

    abstract String Readme();

    abstract T doWork(HashMap<String, Object> context) throws Exception;

    /**
     * Builds a usage string.
     * @param reason
     * @return
     */
    private String usage(String reason) {
        var b = new StringBuilder();

        if (reason != null) {
            b.append(reason);
            b.append(System.lineSeparator());
        }

        b.append("usage: ");
        b.append(getClass().getCanonicalName());
        b.append(" [options] <args>");
        b.append(System.lineSeparator());
        b.append("args are (in the order provided):");
        b.append(System.lineSeparator());

        for (var argset : getArguments()) {
            for (var a : argset) {
                b.append(String.format("  %-10s\t%s", a.name(), a.description()));
                b.append(System.lineSeparator());
            }
            b.append(System.lineSeparator());
        }

        b.append("options are (options may appear in any order):");
        b.append(System.lineSeparator());

        for (var a : getOptions()) {
            b.append(String.format("  %-10s\t%s", a.name(), a.description()));
            b.append(System.lineSeparator());
        }

        b.append(String.format("  %-10s\t%s", "-README", "Displays README text"));
        b.append(System.lineSeparator());

        b.append("Date and time should be in the format: ");
        b.append(PhoneCall.DATE_FORMAT_STRING);

        return b.toString();
    }

    /**
     * Wraps the {@link MainClassSkeleton#doWork(HashMap)} with proper exception handling.
     * @param args
     * @return
     */
    public T wrapWork(String[] args) {
        var context = new HashMap<String, Object>();

        var arguments = Arrays.asList(args);

        if (arguments.contains("-README")) {
            System.out.println(usage(Readme()));
            System.exit(0);
        }

        if (arguments.size() < getArguments().size()) {
            System.err.println(usage(MISSING_ARGS));
            System.exit(1);
        }

        if (arguments.size() == 0) {
            System.err.println(usage(null));
            System.exit(1);
        }

        List<Option> options = getOptions();

        if (options != null) {
            for (var opt : options) {
                arguments = opt.consume(arguments, context);
            }
        }

        List<String> finalArguments = arguments;

        var possibleArgumentList = getArguments().stream().filter(argset -> minimumRequiredArguments(argset) == finalArguments.size()).findFirst();

        if (!possibleArgumentList.isPresent()) {
            System.err.println(usage(MISSING_ARGS));
            System.exit(1);
        }

        List<Argument> argumentList = possibleArgumentList.get();

        for (var arg : argumentList) {
            arguments = arg.consume(arguments, context);
        }

        if (arguments.size() != 0) {
            System.err.println(usage(EXTRA_ARGS));
            System.exit(1);
        }

        try {
            return doWork(context);
        } catch (final Exception e) {
            System.err.println(usage(e.toString()));
            System.exit(1);
        }

        return null;
    }

    private int minimumRequiredArguments(List<Argument> argset) {
        return argset.stream().mapToInt(Argument::length).sum();
    }

    /**
     * Use this method in place of the psvm.
     * @param s
     * @param args
     */
    public static void wrapMain(MainClassSkeleton s, String[] args) {
        s.wrapWork(args);
        System.exit(0);
    }

}
