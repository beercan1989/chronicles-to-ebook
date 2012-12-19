package co.uk.baconi.cte;

import java.net.URL;

import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ChronicleJsoupParser {
    private ChronicleJsoupParser() {
    }

    // http://jsoup.org/cookbook/extracting-data/dom-navigation
    // http://jsoup.org/cookbook/extracting-data/selector-syntax
    // http://jsoup.org/cookbook/extracting-data/attributes-text-html

    public static void parseChroniclePage(final Document ebook, final URL chronicleUrl) {
        try {
            // Download chronicle page from the wiki.
            final Document downloadedChronicle = HttpConnection.connect(chronicleUrl).get();

            // Parse chronicle page
            // - Get chronicle title.
            // - Get all the chronicle paragraphs.
            // - Get chronicle image.
            final String chronicleTitle = downloadedChronicle.select("h1.header").first().text();
            final Elements chronicleParagraphs = cleanInnerHtml(downloadedChronicle.select("div#bodyContent p"));
            final Element chronicleImage = downloadedChronicle.select("a.image img").first();

            // Calculate chronicle index
            final int chronicleIndex = ebook.select("div.Chronicle").size() + 1;

            // Build chronicle entry
            // - Add bookmark point.
            // - Add chronicle title.
            // - Add chronicle image.
            // - Add chronicle paragraphs.
            // - Add amazon page break.
            final Element chronicleEntry = ebook.createElement("div").attr("class", "Chronicle");
            chronicleEntry.appendElement("a").attr("id", "chap" + chronicleIndex);
            chronicleEntry.appendElement("h4").text("Chapter " + padded(chronicleIndex) + " - " + chronicleTitle);
            chronicleEntry.appendChild(chronicleImage);
            appendChildren(chronicleEntry.appendElement("div").attr("class", "ChronicleBody"), chronicleParagraphs);
            chronicleEntry.append("<mbp:pagebreak/>");

            // Build TOC entry

            // Add chronicle content to ebook.
            // - Create TOC entry
            // - Add content to BookBody
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    private static Element appendChildren(final Element parent, final Elements children) {
        for (final Element paragraph : children) {
            parent.appendChild(paragraph);
        }
        return parent;
    }

    /**
     * Pads the index with zero's to three characters.
     */
    private static String padded(final int index) {
        return String.format("%03d", index);
    }

    /**
     * Removes all the HTML tags inside of each element.
     */
    private static Elements cleanInnerHtml(final Elements elements) {
        for (final Element element : elements) {
            element.text(element.text());
        }
        return elements;
    }
}
