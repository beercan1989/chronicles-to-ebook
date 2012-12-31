package co.uk.baconi.cte;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import co.uk.baconi.cte.utils.EncodingUtil.Encoding;
import co.uk.baconi.cte.utils.ResourceUtil;

/**
 * @author JBacon
 */
public abstract class AbstractRunner {

    protected final Log LOG = LogFactory.getLog(getClass());

    protected final List<URL> chronicleCollectionPages = new ArrayList<URL>();

    protected final File outputfolder = new File("output/");
    protected final File imageOutputFolder = new File(outputfolder, "images/");
    protected final File chronicleDownloadFolder = new File(outputfolder, "chronicles-downloaded/");
    protected final File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

    public abstract void run();

    protected void saveEbooktoOutputFolder(final Document ebook) {
        try {
            FileUtils.writeStringToFile(ebookOutputFile, ebook.toString(), Encoding.UTF8.getName());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveCoverImageToOutputFolder() {
        final File coverImage = ResourceUtil.getFile("/images/EveOnlineChroniclesCover.jpg");
        try {
            FileUtils.copyFileToDirectory(coverImage, imageOutputFolder);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
