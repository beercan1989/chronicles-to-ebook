package co.uk.baconi.cte;

import static co.uk.baconi.cte.parsers.ChronicleCollectionParser.parseChronicleCollections;
import static co.uk.baconi.cte.parsers.ChronicleParser.parseChroniclePage;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.buildBaseEbook;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

/**
 * @author JBacon
 */
public final class LiveRunner extends AbstractRunner {

    public static final void main(final String[] programParams) throws MalformedURLException {
        final Log logger = LogFactory.getLog(LiveRunner.class);
        logger.debug("Starting");
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        new LiveRunner().run();
        logger.debug("Finished");
    }

    public LiveRunner() throws MalformedURLException {
        super();
        final String baseUrl = "http://wiki.eveonline.com";
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/Pre-Launch_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2003_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2004_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2005_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2006_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2007_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2008_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2009_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2010_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2011_(chronicles)"));
        chronicleCollectionPages.add(new URL(baseUrl + "/en/wiki/2012_(chronicles)"));
    }

    @Override
    public void run() {
        // 1) Find all chronicle url's and store them chronologically.
        LOG.debug("Finding all chronicle URLs.");
        final Set<URL> chronicleUrls = parseChronicleCollections(chronicleCollectionPages, chronicleDownloadFolder);

        // 2) Create e-book from template.
        LOG.debug("Building base e-book from template.");
        final Document ebook = buildBaseEbook(imageOutputFolder);

        // 3) Download and parse each chronicle page.
        // .... Should include chronicle image download & setting up links
        // .... Should include adding TOC entry in e-book
        // .... Should include adding chronicle body into e-book.
        int index = 1;
        for (final URL chronicleUrl : chronicleUrls) {
            LOG.debug("Parsing chronicle [" + index++ + "], [" + chronicleUrl + "].");
            parseChroniclePage(ebook, chronicleUrl, imageOutputFolder, chronicleDownloadFolder);
        }

        // 4) Write e-book to file
        LOG.debug("Saving e-book to file [" + ebookOutputFile + "]");
        saveEbooktoOutputFolder(ebook);

        // 5) Output the cover image.
        LOG.debug("Saving cover image to folder [" + imageOutputFolder + "]");
        saveCoverImageToOutputFolder();

        // # Optional - add in the ability to run amazon's tool with this application.
    }
}
