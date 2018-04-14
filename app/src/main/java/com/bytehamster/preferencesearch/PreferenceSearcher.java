package com.bytehamster.preferencesearch;

import android.app.Activity;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class PreferenceSearcher {
    private Activity activity;
    private ArrayList<SearchResult> allEntries = new ArrayList<>();

    public PreferenceSearcher(Activity activity) {
        this.activity = activity;
    }

    public void addResourceFile(int resId) {
        allEntries.addAll(parseFile(resId));
    }

    private ArrayList<SearchResult> parseFile(int resId) {
        java.util.ArrayList<SearchResult> results = new ArrayList<>();
        XmlPullParser xpp = activity.getResources().getXml(resId);

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    SearchResult result = new SearchResult();
                    result.resId = resId;
                    for (int i = 0; i < xpp.getAttributeCount(); i++) {
                        String valSanitized = xpp.getAttributeValue(i);
                        if (valSanitized.startsWith("@")) {
                            try {
                                int id = Integer.parseInt(valSanitized.substring(1));
                                valSanitized = activity.getString(id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        switch (xpp.getAttributeName(i)) {
                            case "title":
                                result.title = valSanitized;
                                break;
                            case "summary":
                                result.summary = valSanitized;
                                break;
                            case "key":
                                result.key = valSanitized;
                                break;
                        }
                    }

                    if (result.hasData()) {
                        results.add(result);
                    }

                }
                xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<SearchResult> searchFor(final String keyword) {
        ArrayList<SearchResult> results = new ArrayList<>();
        for (SearchResult res : allEntries) {
            if (res.contains(keyword)) {
                results.add(res);
            }
        }
        return results;
    }

    class SearchResult {
        String title, summary, key;
        int resId;

        private boolean hasData() {
            return title != null || summary != null;
        }

        private boolean contains(String keyword) {
            return stringContains(title, keyword) || stringContains(summary, keyword);
        }

        @Override
        public String toString() {
            return "SearchResult: " + title + " " + summary + " " + key;
        }
    }

    private boolean stringContains(String s1, String s2) {
        return simplify(s1).contains(simplify(s2));
    }

    private String simplify (String s) {
        return s.toLowerCase().replace(" ", "");
    }
}