package co.uk.baconi.cte.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ChronicleParserUtil {
    private ChronicleParserUtil() {
    }

    /**
     * Appends all the given children to the parent Element.
     */
    public static Element appendChildren(final Element parent, final Elements children) {
        for (final Element paragraph : children) {
            parent.appendChild(paragraph);
        }
        return parent;
    }

    /**
     * Pads the index with zero's to three characters.
     */
    public static String padded(final int index) {
        return String.format("%03d", index);
    }

    /**
     * Removes all the HTML tags inside of each element.
     */
    public static Elements cleanInnerHtml(final Elements elements) {
        for (final Element element : elements) {
            element.text(element.text());
        }
        return elements;
    }

    /**
     * Finds the name from the URL for an image.
     */
    public static String getImageFileName(final URL imageUrl) {
        return last(imageUrl.toString().split("/"));
    }

    /**
     * Get the last element in the array
     */
    public static <T> T last(final T[] array) {
        return (array == null || array.length < 0) ? null : array[array.length - 1];
    }

    /**
     * Builds an image URL that can be used to download a chronicle image.
     */
    public static URL getDownloadableImageUrl(final Document chronicle, final Element image)
            throws MalformedURLException {
        final URL baseUrl = new URL(chronicle.baseUri());
        return new URL(baseUrl.getProtocol() + "://" + baseUrl.getHost() + image.attr("src"));
    }

    /**
     * Creates the basic shell of the e-book that is ready for populating.
     */
    public static Document buildBaseEbook(final File imageOutputFolder) {
        final Document ebook = Document.createShell("http://localhost/ebook/");
        final String coverImageName = imageOutputFolder.getName() + "/EveOnlineChroniclesCover.jpg";

        // Set the e-book title.
        ebook.head().appendElement("title").text("Eve Online Chronicles");

        // Add the CSS to the header.
        ebook.head().appendElement("style").attr("type", "text/css").text(ResourceUtil.getString("/css/ebook.css"));

        // Create the cover page.
        final Element coverPage = ebook.body().appendElement("div").attr("class", "CoverPage");
        coverPage.appendElement("a").attr("id", "start");
        coverPage.appendElement("h2").text("Eve Online Chronicles");
        coverPage.appendElement("img").attr("src", coverImageName).attr("alt", "Cover");
        coverPage.appendElement("p").text("Content copyright © CCP hf. All rights reserved");
        coverPage.append("<mbp:pagebreak />");

        // Create the table of contents.
        final Element tableOfContents = ebook.body().appendElement("div").attr("class", "TableOfConents");
        tableOfContents.appendElement("a").attr("id", "TOC");
        tableOfContents.appendElement("h3").text("Table of Contents");
        tableOfContents.append("<mbp:pagebreak />");

        // Create the area for the chronicle content.
        ebook.body().appendElement("div").attr("class", "BookBody");

        return ebook;
    }
}
