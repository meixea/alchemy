package org.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.main.AlchemyProperty;
import org.main.AlchemyType;
import org.main.Reagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlchemyPropertiesSiteParser extends SiteParser<AlchemyProperty> {
    private static final String PROPERTIES_PAGE = "https://elderscrolls.fandom.com/ru/wiki/Алхимия_(Skyrim)";

    public AlchemyPropertiesSiteParser() {
        super(PROPERTIES_PAGE);
    }
    @Override
    public List<AlchemyProperty> parse() throws IOException {

        List<AlchemyProperty> result = new ArrayList<>();

        Document doc = getDocument();
        Elements tables = doc.select("table.wikitable.mw-collapsible.mw-collapsed.sortable");

        result.addAll(parseTable(tables.get(0), AlchemyType.POSITIVE));
        result.addAll(parseTable(tables.get(1), AlchemyType.NEGATIVE));

        return result;
    }
    private List<AlchemyProperty> parseTable(Element table, AlchemyType type){

        List<AlchemyProperty> result = new ArrayList<>();

        Elements rows = table.select("tr:gt(1)");
        int skipped = 0;

        for(Element row : rows){

            String name = row.select("th").text();

            int price;

            try {
                price = Integer.valueOf(row.select("td").get(2).text());
            }
            catch(NumberFormatException e){
                skipped++;
                continue;
            }

            result.add(new AlchemyProperty(name, type, price));
        }

        System.out.printf("Finded %d, skipped %d rows\n", rows.size(), skipped);

        return result;
    }
}
