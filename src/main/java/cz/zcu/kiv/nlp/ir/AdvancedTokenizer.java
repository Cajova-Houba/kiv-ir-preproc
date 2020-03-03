/**
 * Copyright (c) 2014, Michal Konkol
 * All rights reserved.
 */
package cz.zcu.kiv.nlp.ir;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michal Konkol
 */
public class AdvancedTokenizer implements Tokenizer {
    // link | kombinace pismen, znaku a cisel zacinajici pismenem | kombinace cisel, *, x, . zacinajici cislem | html | tecky a sracky |
    public static final String defaultRegex = "(http://[\\p{L}\\d:/.?=&+*-]+)|(\\p{L}[\\p{L}\\d:/?=&+*-]+)|(\\d[\\d*x.]+)|(<.*?>)|([\\p{Punct}])";

    public static String[] tokenize(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);

        ArrayList<String> words = new ArrayList<String>();

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            words.add(text.substring(start, end));
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
