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



    private static Map<String, List<String>> shortRules = new HashMap<>();
    private static Map<String, List<String>> problemWords = new HashMap<>();

    // todo: use this https://gist.github.com/pauli31/3dce15096d87d8f32015ae519b32d418
    static {
        shortRules.put("i'm", Arrays.asList("i am".split(" ")));
        shortRules.put("you're",  Arrays.asList("you are".split(" ")));
        shortRules.put("he's",  Arrays.asList("he is".split(" ")));
        shortRules.put("she's",  Arrays.asList("she is".split(" ")));
        shortRules.put("it's",  Arrays.asList("it is".split(" ")));
        shortRules.put("we're",  Arrays.asList("we are".split(" ")));
        shortRules.put("they're",  Arrays.asList("they are".split(" ")));
        shortRules.put("don't",  Arrays.asList("do not".split(" ")));
        shortRules.put("doesn't",  Arrays.asList("does not".split(" ")));
        shortRules.put("can't",  Arrays.asList("can not".split(" ")));
        shortRules.put("couldn't",  Arrays.asList("could not".split(" ")));
        shortRules.put("shouldn't",  Arrays.asList("should not".split(" ")));
        shortRules.put("should've",  Arrays.asList("should have".split(" ")));
        shortRules.put("mustn't",  Arrays.asList("must not".split(" ")));
        shortRules.put("haven't",  Arrays.asList("have not".split(" ")));
        shortRules.put("hasn't",  Arrays.asList("has not".split(" ")));

        problemWords.put("hes", Arrays.asList("he", "is"));
    }


    public static String[] tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String word = text.substring(start, end);
            if (shortRules.containsKey(word)) {
                words.addAll(shortRules.get(word));
            } else if (problemWords.containsKey(word)) {
                words.addAll(problemWords.get(word));
            } else {
                words.add(text.substring(start, end));
            }
        }

        String[] ws = new String[words.size()];
        ws = words.toArray(ws);

        return ws;
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    public String[] tokenize(String text) {
        return tokenize(text, defaultRegex);
    }
}
