package com.example.mord.myapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by narhwal on 2/6/2017.
 */

public class TermHandler {
    private static Map<String, Term> termMap = new HashMap<>();

    public static void addTerm(Term term) {
        termMap.put(term.getTermTitle(), term);
    }
    public static Term getTerm(String termTitle) {
       return termMap.get(termMap.get(termTitle));
    }
}
