package co.uk.baconi.cte;

import static co.uk.baconi.cte.utils.ChronicleParserUtil.appendChildren;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.cleanInnerHtml;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getDownloadableImageUrl;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getImageFileName;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.padded;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import co.uk.baconi.annotations.VisibleForTesting;

public final class ChronicleJsoupParser {

    private ChronicleJsoupParser() {
    }

    /**
     * Downloads a chronicle page, parses it and add's it to the e-book document. Along with downloading the chronicle
     * image and the chronicles source code to a file.
     */
    public static void parseChroniclePage(final Document ebook, final URL chronicleUrl, final File imageOutputFolder,
            final File chronicleDownloadFolder) {
        try {
            // Download chronicle page from the wiki.
            final Document downloadedChronicle = downloadChroniclePageFromWiki(chronicleUrl, chronicleDownloadFolder);

            // Parse chronicle page
            // - Get chronicle title.
            // - Get all the chronicle paragraphs.
            // - Get chronicle image.
            final String chronicleTitle = getChronicleTitle(downloadedChronicle);
            final Elements chronicleParagraphs = getChronicleParagraphs(downloadedChronicle);
            final Element chronicleImage = getChronicleImageDetails(downloadedChronicle);

            // Calculate chronicle index
            final int chronicleIndex = calculateCurrentChronicleIndex(ebook);

            // Download chronicle image
            // - Get image URL
            // - Download to image output folder
            // - Update image element to point at image folder
            final File downloadedImage = downloadChronicleImage(downloadedChronicle, chronicleImage, imageOutputFolder);
            updateChronicleImageElement(chronicleImage, downloadedImage, imageOutputFolder);

            // Build chronicle entry
            // - Add bookmark point.
            // - Add chronicle title.
            // - Add chronicle image.
            // - Add chronicle paragraphs.
            // - Add amazon page break.
            buildChronicleBodyEntry(ebook, chronicleIndex, chronicleTitle, chronicleImage, chronicleParagraphs);

            // Build TOC entry
            // - Create TOC entry
            // - Add to correct place in TOC area
            buildTableOfContentsEntry(ebook, chronicleIndex, chronicleTitle);

        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Builds a table of contents entry for the chronicle and adds it to the e-book template.
     */
    @VisibleForTesting
    static void buildTableOfContentsEntry(final Document ebook, final int chronicleIndex, final String chronicleTitle) {
        final Element tocEntry = ebook.createElement("a").attr("href", "#chap-" + chronicleIndex);
        tocEntry.appendElement("h4").text(chronicleTitle);
        ebook.select("div.TableOfConents").first().children().last().before(tocEntry);
    }

    /**
     * Builds a chronicle body entry for the chronicle and adds it to the e-book template.
     */
    @VisibleForTesting
    static void buildChronicleBodyEntry(final Document ebook, final int chronicleIndex, final String chronicleTitle,
            final Element chronicleImage, final Elements chronicleParagraphs) {
        final Element chronicleEntry = ebook.select("div.BookBody").first().appendElement("div");
        chronicleEntry.attr("class", "Chronicle");
        chronicleEntry.appendElement("a").attr("id", "chap-" + chronicleIndex);
        chronicleEntry.appendElement("h4").text("Chapter " + padded(chronicleIndex) + " - " + chronicleTitle);
        chronicleEntry.appendChild(chronicleImage);
        appendChildren(chronicleEntry.appendElement("div").attr("class", "ChronicleBody"), chronicleParagraphs);
        chronicleEntry.append("<mbp:pagebreak />");
    }

    /**
     * Updates the chronicle image element to point to the download folder.
     */
    @VisibleForTesting
    static void updateChronicleImageElement(final Element chronicleImage, final File imageDestination,
            final File imageOutputFolder) {
        chronicleImage.attr("src", imageOutputFolder.getName() + "/" + imageDestination.getName());
    }

    /**
     * Downloads the chronicle image to the given folder.
     */
    @VisibleForTesting
    static File downloadChronicleImage(final Document downloadedChronicle, final Element chronicleImage,
            final File imageOutputFolder) throws IOException {
        final URL imageUrl = getDownloadableImageUrl(downloadedChronicle, chronicleImage);
        final File imageDestination = new File(imageOutputFolder, getImageFileName(imageUrl));
        FileUtils.copyURLToFile(imageUrl, imageDestination);
        return imageDestination;
    }

    /**
     * Calculates the current chronicles index number from the e-book template.
     */
    @VisibleForTesting
    static int calculateCurrentChronicleIndex(final Document ebook) {
        return ebook.select("div.Chronicle").size() + 1;
    }

    /**
     * Get the chronicle image element from the downloaded chronicle.
     */
    @VisibleForTesting
    static Element getChronicleImageDetails(final Document downloadedChronicle) {
        return downloadedChronicle.select("a.image img").first();
    }

    /**
     * Get the chronicle paragraphs from the downloaded chronicle
     */
    @VisibleForTesting
    static Elements getChronicleParagraphs(final Document downloadedChronicle) {
        return cleanInnerHtml(downloadedChronicle.select("div#bodyContent p"));
    }

    /**
     * Get the chronicle title from the downloaded chronicle.
     */
    @VisibleForTesting
    static String getChronicleTitle(final Document downloadedChronicle) {
        return downloadedChronicle.select("h1.header").first().text();
    }

    /**
     * Download the chronicle page from the wiki.
     */
    @VisibleForTesting
    static Document downloadChroniclePageFromWiki(final URL chronicleUrl, final File chronicleDownloadFolder)
            throws IOException {
        final Document downloadedChronicle = HttpConnection.connect(chronicleUrl).get();
        FileUtils.write(chronicleDownloadFolder, downloadedChronicle.toString(), "UTF-8");
        return downloadedChronicle;
    }
}
