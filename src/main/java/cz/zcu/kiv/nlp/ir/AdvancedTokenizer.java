/**
 * Copyright (c) 2014, Michal Konkol
 * All rights reserved.
 */
package cz.zcu.kiv.nlp.ir;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 */
public class AdvancedTokenizer implements Tokenizer {
    // link | kombinace pismen, znaku a cisel zacinajici pismenem | kombinace cisel, *, x, . zacinajici cislem | html | tecky a interpunkce |
    public static final String defaultRegex = "(http[s]?://[\\p{L}\\d:/.?=&+*-_]+)|(\\p{L}[\\p{L}\\d:/?=&+*'-]+)|(\\d[\\d*x.]+)|(<.*?>)|([\\p{Punct}])";


    // based on: https://gist.github.com/pauli31/3dce15096d87d8f32015ae519b32d418
    public static final String contrRegexNot = "(\\b)([Aa]re|[Cc]ould|[Dd]id|[Dd]oes|[Dd]o|[Hh]ad|[Hh]as|[Hh]ave|[Ii]s|[Mm]ight|[Mm]ust|[Ss]hould|[Ww]ere|[Ww]ould)n't";
    public static final String contrRegexWill = "(\\b)([Hh]e|[Ii]|[Ss]he|[Tt]hey|[Ww]e|[Ww]hat|[Ww]ho|[Yy]ou)'ll";
    public static final String contrRegexAre = "(\\b)([Tt]hey|[Ww]e|[Ww]hat|[Ww]ho|[Yy]ou)'re";
    public static final String contrRegexHave = "(\\b)([Ii]|[Ss]hould|[Tt]hey|[Ww]e|[Ww]hat|[Ww]ho|[Ww]ould|[Yy]ou)'ve";
    public static final List<String[]> contrRegexNonStandard = new ArrayList<>();



    private static Map<String, List<String>> problemWords = new HashMap<>();

    // todo: use this https://gist.github.com/pauli31/3dce15096d87d8f32015ae519b32d418
    static {
        contrRegexNonStandard.add(new String[] {"(\b)([Cc]a)n't", "$1$2n not"});
        contrRegexNonStandard.add(new String[] {"(\\b)([Ii])'m", "$1$2 am"});
        contrRegexNonStandard.add(new String[] {"(\\b)([Ll]et)'s", "$1$2 us"});
        contrRegexNonStandard.add(new String[] {"(\\b)([Ww])on't", "$1$2ill not"});
        contrRegexNonStandard.add(new String[] {"(\\b)([Ss])han't", "$1$2hall not"});
        contrRegexNonStandard.add(new String[] {"(\\b)([Yy])(?:'all|a'll)", "$1$2ou all"});

        problemWords.put("hes", Arrays.asList("he", "is"));
    }


    public static String[] tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        text = replaceContractions(text);

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String word = text.substring(start, end);
            if (problemWords.containsKey(word)) {
                words.addAll(problemWords.get(word));
            } else {
                words.add(text.substring(start, end));
            }
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }

    private static String replaceContractions(String text) {
        text = text.replaceAll(contrRegexNot, "$1$2 not");
        text = text.replaceAll(contrRegexWill, "$1$2 will");
        text = text.replaceAll(contrRegexAre, "$1$2 are");
        text = text.replaceAll(contrRegexHave, "$1$2 have");
        for(String[] nonStandardContr : contrRegexNonStandard) {
            text = text.replaceAll(nonStandardContr[0], nonStandardContr[1]);
        }

        return text;
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public String[] tokenize(String text) {
        return tokenize(text, defaultRegex);
    }
}
