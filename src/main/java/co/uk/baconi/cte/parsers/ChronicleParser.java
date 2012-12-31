package co.uk.baconi.cte.parsers;

import static co.uk.baconi.cte.utils.ChronicleParserUtil.appendChildren;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.clean;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.cleanInnerHtml;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.downloadChroniclePageFromWiki;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getDownloadableImageUrl;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getImageFileName;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.padded;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import co.uk.baconi.annotations.VisibleForTesting;
import co.uk.baconi.cte.utils.EncodingUtil.Encoding;

public final class ChronicleParser {

    private static final String WIKI_URL = "http://wiki.eveonline.com/en/wiki/";
    private static final String EMPTY = "";
    private static final Log LOG = LogFactory.getLog(ChronicleParser.class);

    private ChronicleParser() {
    }

    /**
     * Downloads a chronicle page, parses it and add's it to the e-book document. Along with downloading the chronicle
     * image and the chronicles source code to a file.
     */
    public static void parseChroniclePage(final Document ebook, final URL chronicleUrl, final File imageOutputFolder,
            final File chronicleDownloadFolder) {
        try {
            final Document downloadedChronicle = downloadChroniclePageFromWiki(chronicleUrl, chronicleDownloadFolder);
            parseChroniclePage(ebook, downloadedChronicle, imageOutputFolder, chronicleDownloadFolder);
        } catch (final Throwable t) {
            LOG.error("Failed to parse chronicle page [" + chronicleUrl + "]", t);
        }
    }

    /**
     * Downloads a chronicle page, parses it and add's it to the e-book document. Along with downloading the chronicle
     * image and the chronicles source code to a file.
     */
    public static void parseChroniclePage(final Document ebook, final File chronicleFile, final File imageOutputFolder,
            final File chronicleDownloadFolder) {
        try {
            final String baseUri = WIKI_URL + chronicleFile.getName().replace(".html", EMPTY);
            final Document downloadedChronicle = Jsoup.parse(chronicleFile, Encoding.UTF8.getName(), baseUri);
            parseChroniclePage(ebook, downloadedChronicle, imageOutputFolder, chronicleDownloadFolder);
        } catch (final Throwable t) {
            LOG.error("Failed to parse chronicle page [" + chronicleFile + "]", t);
        }
    }

    private static void parseChroniclePage(final Document ebook, final Document downloadedChronicle,
            final File imageOutputFolder, final File chronicleDownloadFolder) throws IOException {
        // Download chronicle page from the wiki.
        // - This in done in the previous method call in this class.

        // Parse chronicle page
        // - Get chronicle title.
        // - Get all the chronicle paragraphs.
        // - Get chronicle image.
        final String chronicleTitle = getChronicleTitle(downloadedChronicle);
        final Element chronicleImage = getChronicleImageDetails(downloadedChronicle);
        final Elements chronicleParagraphs = getChronicleParagraphs(downloadedChronicle);

        // Calculate chronicle index
        final int chronicleIndex = calculateCurrentChronicleIndex(ebook);

        // Download chronicle image
        // - Get image URL
        // - Download to image output folder
        // - Update image element to point at image folder
        if (chronicleImage != null) {
            final File downloadedImage = downloadChronicleImage(downloadedChronicle, chronicleImage, imageOutputFolder);
            updateChronicleImageElement(chronicleImage, downloadedImage, imageOutputFolder);
        } else {
            LOG.debug(downloadedChronicle.select("img"));
        }

        // Remove any empty paragraphs.
        removeAnyEmptyParagraphs(chronicleParagraphs);

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
        // buildTableOfContentsEntry(ebook, chronicleIndex, chronicleTitle);
    }

    /**
     * Removes any paragraphs that are empty from the collection.
     */
    @VisibleForTesting
    static Elements removeAnyEmptyParagraphs(final Elements chronicleParagraphs) {
        final List<Element> toRemove = new ArrayList<Element>();
        for (final Element paragraph : chronicleParagraphs) {
            if (!paragraph.hasText()) {
                toRemove.add(paragraph);
            }
        }

        chronicleParagraphs.removeAll(toRemove);

        return chronicleParagraphs;
    }

    /**
     * Builds a table of contents entry for the chronicle and adds it to the e-book template.
     */
    @VisibleForTesting
    static void buildTableOfContentsEntry(final Document ebook, final int chronicleIndex, final String chronicleTitle) {
        final Element tocEntry = ebook.createElement("h4").appendElement("a").attr("href", "#chap-" + chronicleIndex);
        tocEntry.text(chronicleTitle);
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
        if (chronicleImage != null) {
            chronicleEntry.appendChild(chronicleImage);
        }
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

        if (!imageDestination.exists() || imageDestination.length() < 100L) {
            FileUtils.copyURLToFile(imageUrl, imageDestination);
        }

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
        final Element first = downloadedChronicle.select("a.image img").first();
        if (first == null) {
            return null;
        }
        return first.clone();
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
        return clean(downloadedChronicle.select("h1.header").first().text().replaceAll("\\(Chronicle\\)", EMPTY));
    }

}
