package co.uk.baconi.cte;

import static co.uk.baconi.cte.parsers.ChronicleParser.parseChroniclePage;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.buildBaseEbook;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

/**
 * Runs the e-book parser in a form of offline mode, but currently does not respect the chronological order they were
 * written.
 * 
 * @author JBacon
 */
public final class OfflineRunner extends AbstractRunner {

    public static final void main(final String[] programParams) {
        final Log logger = LogFactory.getLog(OfflineRunner.class);

        logger.debug("Starting");
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        new OfflineRunner().run();
        logger.debug("Finished");
    }

    private OfflineRunner() {
    }

    @Override
    public void run() {
        // 1) Find all chronicle url's and store them chronologically.
        LOG.debug("Finding all chronicle URLs.");
        final Collection<File> chronicleFiles = FileUtils.listFiles(chronicleDownloadFolder, new RegexFileFilter(
                "^(.*\\(Chronicle\\)\\.html?)"), DirectoryFileFilter.DIRECTORY);

        // 2) Create e-book from template.
        LOG.debug("Building base e-book from template.");
        final Document ebook = buildBaseEbook(imageOutputFolder);

        // 3) Download and parse each chronicle page.
        // .... Should include chronicle image download & setting up links
        // .... Should include adding TOC entry in e-book
        // .... Should include adding chronicle body into e-book.
        int index = 1;
        for (final File chroniclefile : chronicleFiles) {
            LOG.debug("Parsing chronicle [" + index++ + "], [" + chroniclefile + "].");
            parseChroniclePage(ebook, chroniclefile, imageOutputFolder, chronicleDownloadFolder);
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
