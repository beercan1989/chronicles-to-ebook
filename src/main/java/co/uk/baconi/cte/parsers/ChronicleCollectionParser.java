package co.uk.baconi.cte.parsers;

import static co.uk.baconi.cte.utils.ChronicleCollectionParserUtil.getChroniclePages;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ChronicleCollectionParser {
    private ChronicleCollectionParser() {
    }

    /**
     * Takes a list of all the chronicle collections and returns a list of all chronicles found, in chronological order.
     */
    public static List<URL> parseChronicleCollections(final List<URL> chronicleCollections,
            final File collectionDownloadFolder) throws IOException {
        final List<URL> chroniclePages = new ArrayList<URL>();

        for (final URL chronicleCollection : chronicleCollections) {
            chroniclePages.addAll(getChroniclePages(chronicleCollection, collectionDownloadFolder));
        }

        return chroniclePages;
    }
}
