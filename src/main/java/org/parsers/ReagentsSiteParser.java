package org.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.main.AlchemyProperty;
import org.main.AlchemyType;
import org.main.Reagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReagentsSiteParser extends SiteParser<Reagent> {

    private static final String REAGENTS_PAGE = "http://www.tesmagic.ru/skyrim/ingredients.html";

    public ReagentsSiteParser(){
        super(REAGENTS_PAGE);
    }
    @Override
    public List<Reagent> parse() throws IOException {

        List<Reagent> result = new ArrayList<>();

        Document doc = getDocument();

        Elements rows = doc.select("table#list tr:gt(0)");
        int findedNumber = rows.size();

        int standardNumber = 0;
        int advancedNumber = 0;
        int skipped = 0;
        int skippedProperties = 0;

        Reagent reagent;
        for(Element row : rows) {
            Elements el = row.select("td h4");
            if (el.size() == 1) {
                standardNumber++;

                reagent = new Reagent(el.text());

                skippedProperties += parseProperties(reagent, row.select("td:gt(0)"));

                result.add(reagent);
                continue;
            }
            el = row.select("td.reagent");
            if(el.size() == 1){
                advancedNumber++;

                String name = el.html().split("<sup>")[0];
                reagent = new Reagent(name);

                skippedProperties += parseProperties(reagent, row.select("td:gt(0)"));

                result.add(reagent);
                continue;
            }
            skipped++;
        }
        System.out.printf("Finded: %d, Standard: %d, Advanced: %d, Skipped: %d\n",
                findedNumber,
                standardNumber,
                advancedNumber,
                skipped);

        System.out.println("Skipped properties: " + skippedProperties);

        return result;
    }
    private int parseProperties(Reagent reagent, Elements properties){

        int skipped = 0;
        for(var prop : properties){

            String name = prop.text()
                    .replace("Тяжелая", "Тяжёлая")
                    .replace("Легкая", "Лёгкая");

            var p = AlchemyProperty.findProperty(name);
            if( p == null) {
                skipped++;
                System.out.printf("Skipped property %s: %s\n", reagent.getName(), prop.text());
            }
            else
                reagent.addProperty(p);
        }

        return skipped;
    }
}
