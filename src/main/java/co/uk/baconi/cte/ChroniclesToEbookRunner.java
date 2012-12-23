package co.uk.baconi.cte;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;

import co.uk.baconi.cte.models.ChroniclePage;
import co.uk.baconi.cte.utils.ResourceUtil;
import co.uk.baconi.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ChroniclesToEbookRunner {

    private static final URL BASE_URL = WebPageUtil.getUrl("http://wiki.eveonline.com");

    public static final void main(final String[] programParams) throws IOException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        new ChroniclesToEbookRunner().run();
    }

    private final LinkedHashMap<String, ChroniclePage> chronicleGroupings;
    private final LinkedHashMap<ChroniclePage, String> chroniclePagesMap;

    private final File outputfolder = new File("output/");
    private final File imageOutputFolder = new File(outputfolder, "images/");
    private final File chronicleDownloadFolder = new File(outputfolder, "chronicles-downloaded/");
    private final File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

    public ChroniclesToEbookRunner() throws MalformedURLException {
        chronicleGroupings = new LinkedHashMap<String, ChroniclePage>();
        chroniclePagesMap = new LinkedHashMap<ChroniclePage, String>();

        setupChronicleGroupings("PRE-LAUNCH", BASE_URL, "/en/wiki/Pre-Launch_(chronicles)");
        setupChronicleGroupings("2003", BASE_URL, "/en/wiki/2003_(chronicles)");
        setupChronicleGroupings("2004", BASE_URL, "/en/wiki/2004_(chronicles)");
        setupChronicleGroupings("2005", BASE_URL, "/en/wiki/2005_(chronicles)");
        setupChronicleGroupings("2006", BASE_URL, "/en/wiki/2006_(chronicles)");
        setupChronicleGroupings("2007", BASE_URL, "/en/wiki/2007_(chronicles)");
        setupChronicleGroupings("2008", BASE_URL, "/en/wiki/2008_(chronicles)");
        setupChronicleGroupings("2009", BASE_URL, "/en/wiki/2009_(chronicles)");
        setupChronicleGroupings("2010", BASE_URL, "/en/wiki/2010_(chronicles)");
        setupChronicleGroupings("2011", BASE_URL, "/en/wiki/2011_(chronicles)");
        setupChronicleGroupings("2012", BASE_URL, "/en/wiki/2012_(chronicles)");
    }

    public void run() {
        // 1) Find all chronicle url's and store them chronologically.
        // 2) Create e-book from template.
        // 3) Download and parse each chronicle page.
        // .... Should include chronicle image download & setting up links
        // .... Should include adding TOC entry in e-book
        // .... Should include adding chronicle body into e-book.
        // 4) Write e-book to file

        // # Optional - add in the ability to run amazon's tool with this application.

    }

    private void setupChronicleGroupings(final String groupingName, final URL baseUrl, final String groupingUrl)
            throws MalformedURLException {
        chronicleGroupings.put(groupingName, new ChroniclePage(new URL(baseUrl + groupingUrl)));
    }

    private void outputCoverImage() {
        final File coverImage = ResourceUtil.getFile("/EveOnlineChroniclesCover.jpg");
        try {
            FileUtils.copyFileToDirectory(coverImage, imageOutputFolder);
        } catch (final IOException e) {
        }
    }
}
