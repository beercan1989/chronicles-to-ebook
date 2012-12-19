package co.uk.baconi.cte;

//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.not;
//import static org.hamcrest.CoreMatchers.nullValue;
//import static org.hamcrest.Matchers.containsString;
//import static org.hamcrest.Matchers.empty;
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Arrays;
//import java.util.Set;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.StringUtils;
//import org.junit.Test;
//import org.mockito.Mockito;

public class ChronicleJsoupParserTest {
    @Test
    public void test() throws IOException {
        final Document doc = Jsoup.connect("http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)").get();
        final String chronicleTitle = doc.select("h1.header").first().text();
        final Elements chronicleBody = doc.select("div#bodyContent p");
        final Element pageImage = doc.select("a.image img").first();

        for (final Element element : chronicleBody) {
            element.text(element.text());
        }

        System.out.println(chronicleTitle);
        System.out.println(pageImage.attr("src"));
        System.out.println(chronicleBody);
        // for (final Element element : chronicleBody) {
        // System.out.println(element.text());
        // }
    }

    private static final String EBOOK_CSS = "img { display: block; margin-left: auto; margin-right: auto } h1,h2,h3,h4 { text-align: center }";

    @Test
    public void test2() {
        final Document ebook = Document.createShell("http://localhost/ebook");
        ebook.head().appendElement("title").text("Eve Online Chronicles");
        ebook.head().appendElement("style").attr("type", "text/css").text(EBOOK_CSS);

        buildCoverPage(ebook.body().appendElement("div").attr("class", "CoverPage"));
        buildTableOfContents(ebook.body().appendElement("div").attr("class", "TableOfConents"));
        buildBookBody(ebook.body().appendElement("div").attr("class", "BookBody"));

        System.out.println(ebook.toString());
    }

    private void buildCoverPage(final Element coverPage) {
        coverPage.appendElement("a").attr("id", "start");
        coverPage.appendElement("h2").text("Eve Online Chronicles");
        coverPage.appendElement("img").attr("src", "#{imagesFolder}/EveOnlineChroniclesCover.jpg").attr("alt", "Cover");
        coverPage.appendElement("p").text("Content copyright © CCP hf. All rights reserved");
        appendAmazonPageBreak(coverPage);
    }

    private void buildTableOfContents(final Element tableOfContents) {
        tableOfContents.appendElement("a").attr("id", "TOC");
        tableOfContents.appendElement("h3").text("Table of Contents");
        tableOfContents
                .appendText("#{for Chronicle in Chronicles; do} #{table-of-contents-entry-template.html} #{done}");
        appendAmazonPageBreak(tableOfContents);
    }

    private void buildBookBody(final Element bookBody) {
        bookBody.appendText("#{for Chronicle in Chronicles; do} #{chronicle-template.html} #{done}");
    }

    private void appendAmazonPageBreak(final Element element) {
        element.appendElement("mbp:pagebreak");
    }
}