package com.evolutionnext.patternmatching;

public class PatternMatching {
    @SuppressWarnings("PatternVariableCanBeUsed")
    public static String matchOldWay(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            if ("Hello".equals(s)) return "Hello to you";
            else return "A String";
        } else if (o instanceof Integer) {
            Integer i = (Integer) o;
            return String.valueOf(i + 30);
        }
        return "Nothing";
    }

    public static String match(Object o) {
        if (o instanceof String s) {
            if ("Hello".equals(s)) return "Hello to you";
            else return "A String";
        } else if (o instanceof Integer i) {
            return String.valueOf(i + 30);
        }
        return "Nothing";
    }


    public static String compoundMatch(Object o) {
        if (o instanceof String s && !s.isEmpty()) {
            return "non empty string";
        }
        return "not a string or is empty";
    }

    public static void matchString(String s) {
        switch (s) {
            case null         -> System.out.println("Oops");
            case "Foo", "Bar" -> System.out.println("Great");
            default           -> System.out.println("Ok");
        }
    }

    public static String formatterPatternSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> obj.toString();
        };
    }


    public static String matchRecordPatterns(Object object) {
        return switch (object) {
            case Team(String city, String name, int wins, int losses) ->
                String.format("Team %s from %s with a record of (%d-%d)", name, city, wins, losses);
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> object.toString();
        };
    }

    public static String matchRecordPatternsWithUnnamedVariables(Object object) {
        return switch (object) {
            case Team(String city, String name, int _, int _) ->
                String.format("Team %s from %s", name, city);
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> object.toString();
        };
    }

    public static String matchRecordPatternsWhen(Object object) {
        return switch (object) {
            case Team(String city, String name, int _, int _) when city.startsWith("M") ->
                String.format("Team %s from %s, a city that starts with M", name, city);
            case Team(String city, String name, int _, int _) ->
                String.format("Team %s from %s", name, city);
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> object.toString();
        };
    }

    public static String matchRecordPatternsWithWinningRecords(Team team) {
        return switch (team) {
            case Team(String city, String name, int wins, int losses) when wins > losses ->
                String.format("Team %s from %s has a winning record", name, city);
            case Team(String city, String name, int _, int _) ->
                String.format("Team %s from %s has a losing record", name, city);
        };
    }
}
