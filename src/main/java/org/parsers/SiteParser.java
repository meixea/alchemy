package org.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public abstract class SiteParser<T> implements Parser<T> {
    private String url;
    public SiteParser(String url) {
        this.url = url;
    }
    protected Document getDocument() throws IOException {
        return Jsoup.connect(url).get();
    }
}
