package co.uk.baconi.cte.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ChronicleCollectionParserUtil {
    private ChronicleCollectionParserUtil() {
    }

    /**
     * Takes a chronicle collection page URL and creates a single list of all the chronicles in chronological order.
     */
    public static List<URL> getChroniclePages(final URL chronicleCollectionPage, final File collectionDownloadFolder)
            throws IOException {
        final List<URL> chroniclePages = new ArrayList<URL>();

        // Download the chronicle collection page.
        final Document chronicleCollection = ChronicleParserUtil.downloadChroniclePageFromWiki(chronicleCollectionPage,
                collectionDownloadFolder);

        // Find the chronicle list, that is in chronological order.
        final Element chronicleList = chronicleCollection.select("#Chronological").first().parent()
                .nextElementSibling();

        // Get all the anchor tags in the list.
        final Elements chronicleLinks = chronicleList.select("a");
        final String baseUrl = ChronicleParserUtil.getBaseUrl(chronicleCollection);

        // Populate the list of all the chronicle page URLs.
        for (final Element chronicleLink : chronicleLinks) {
            final String chronicleUrl = chronicleLink.attr("href");
            chroniclePages.add(new URL(baseUrl + chronicleUrl));
        }

        return chroniclePages;
    }
}