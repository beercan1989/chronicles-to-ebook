package co.uk.baconi.cte.parsers;

import static co.uk.baconi.cte.utils.ChronicleCollectionParserUtil.getChroniclePages;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ChronicleCollectionParser {

    private static final Log LOG = LogFactory.getLog(ChronicleCollectionParser.class);

    private ChronicleCollectionParser() {
    }

    /**
     * Takes a list of all the chronicle collections and returns a list of all chronicles found, in chronological order.
     */
    public static List<URL> parseChronicleCollections(final List<URL> chronicleCollections,
            final File collectionDownloadFolder) {
        final List<URL> chroniclePages = new ArrayList<URL>();

        for (final URL chronicleCollection : chronicleCollections) {
            try {
                chroniclePages.addAll(getChroniclePages(chronicleCollection, collectionDownloadFolder));
            } catch (final Throwable t) {
                LOG.error("Failed to parse chronicle page [" + chronicleCollection + "]", t);
            }
        }

        return chroniclePages;
    }
}
