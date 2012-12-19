package co.uk.baconi.cte;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ChronicleJsoupParser {
    private ChronicleJsoupParser() {
    }

    /**
     * Downloads a chronicle page, parses it and add's it to the ebook document.
     * Along with downloading the chronicle image and the chronicles source code to a file.
     */
    public static void parseChroniclePage(final Document ebook, final URL chronicleUrl, final File imageOutputFolder,
            final File dumpingFolder) {
        try {
            // Download chronicle page from the wiki.
            final Document downloadedChronicle = HttpConnection.connect(chronicleUrl).get();
            FileUtils.write(dumpingFolder, downloadedChronicle.toString(), "UTF-8");

            // Parse chronicle page
            // - Get chronicle title.
            // - Get all the chronicle paragraphs.
            // - Get chronicle image.
            final String chronicleTitle = downloadedChronicle.select("h1.header").first().text();
            final Elements chronicleParagraphs = cleanInnerHtml(downloadedChronicle.select("div#bodyContent p"));
            final Element chronicleImage = downloadedChronicle.select("a.image img").first();

            // Calculate chronicle index
            final int chronicleIndex = ebook.select("div.Chronicle").size() + 1;

            // Download chronicle image
            // - Get image url
            // - Download to image output folder
            // - Update image element to point at image folder
            final URL imageUrl = new URL(downloadedChronicle.baseUri() + chronicleImage.attr("src"));
            final File imageDestination = new File(imageOutputFolder, getImageFileName(imageUrl));
            FileUtils.copyURLToFile(imageUrl, imageDestination);
            chronicleImage.attr("src", imageOutputFolder.getName() + "/" + imageDestination.getName());

            // Build chronicle entry
            // - Add bookmark point.
            // - Add chronicle title.
            // - Add chronicle image.
            // - Add chronicle paragraphs.
            // - Add amazon page break.
            final Element chronicleEntry = ebook.select("div.BookBody").first().appendElement("div");
            chronicleEntry.attr("class", "Chronicle");
            chronicleEntry.appendElement("a").attr("id", "chap-" + chronicleIndex);
            chronicleEntry.appendElement("h4").text("Chapter " + padded(chronicleIndex) + " - " + chronicleTitle);
            chronicleEntry.appendChild(chronicleImage);
            appendChildren(chronicleEntry.appendElement("div").attr("class", "ChronicleBody"), chronicleParagraphs);
            chronicleEntry.append("<mbp:pagebreak/>");

            // Build TOC entry
            // - Create toc entry
            // - Add to correct place in TOC area
            final Element tocEntry = ebook.createElement("a").attr("href", "#chap-" + chronicleIndex);
            tocEntry.appendElement("h4").text(chronicleTitle);
            ebook.select("div.TableOfConents").first().children().last().before(tocEntry);

        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Appends all the given children to the parent Element.
     */
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

    /**
     * Finds the name from the URL for an image.
     */
    private static String getImageFileName(final URL imageUrl) {
        return last(imageUrl.toString().split("/"));
    }

    /**
     * Get the last element in the array
     */
    private static <T> T last(final T[] array) {
        return (array == null || array.length < 0) ? null : array[array.length - 1];
    }
}
