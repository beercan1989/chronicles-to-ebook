package co.uk.baconi.cte;

import static co.uk.baconi.cte.parsers.ChronicleParser.parseChroniclePage;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.buildBaseEbook;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import co.uk.baconi.cte.utils.ResourceUtil;

/**
 * @author JBacon
 */
public final class OfflineRunner {

    private static Log LOG = LogFactory.getLog(OfflineRunner.class);

    public static final void main(final String[] programParams) {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        new OfflineRunner().run();
    }

    private final File outputfolder = new File("output/");
    private final File imageOutputFolder = new File(outputfolder, "images/");
    private final File chronicleDownloadFolder = new File(outputfolder, "chronicles-downloaded/");
    private final File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

    public OfflineRunner() {
    }

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

    private void saveEbooktoOutputFolder(final Document ebook) {
        try {
            FileUtils.writeStringToFile(ebookOutputFile, ebook.toString(), "UTF-8");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCoverImageToOutputFolder() {
        final File coverImage = ResourceUtil.getFile("/images/EveOnlineChroniclesCover.jpg");
        try {
            FileUtils.copyFileToDirectory(coverImage, imageOutputFolder);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
