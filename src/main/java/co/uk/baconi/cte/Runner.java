package co.uk.baconi.cte;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import co.uk.baconi.cte.utils.ResourceUtil;

/**
 * @author JBacon
 */
public final class Runner {

    public static final void main(final String[] programParams) throws IOException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        new Runner().run();
    }

    private final List<URL> chronicleCollectionPages = new ArrayList<URL>();

    private final File outputfolder = new File("output/");
    private final File imageOutputFolder = new File(outputfolder, "images/");
    private final File chronicleDownloadFolder = new File(outputfolder, "chronicles-downloaded/");
    private final File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

    public Runner() throws MalformedURLException {
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

    private void outputCoverImage() {
        final File coverImage = ResourceUtil.getFile("/EveOnlineChroniclesCover.jpg");
        try {
            FileUtils.copyFileToDirectory(coverImage, imageOutputFolder);
        } catch (final IOException e) {
        }
    }
}
