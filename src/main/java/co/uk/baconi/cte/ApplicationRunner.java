package co.uk.baconi.cte;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.uk.baconi.annotations.VisibleForTesting;
import co.uk.baconi.cte.models.ChroniclePage;
import co.uk.baconi.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ApplicationRunner {

    public static final URL BASE_URL = WebPageUtil.getUrl("http://wiki.eveonline.com");

    public static final void main(final String[] programParams) throws MalformedURLException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        final ApplicationRunner applicationRunner = new ApplicationRunner();
        applicationRunner.readChronicleGroupingPages();
        applicationRunner.findChronicleUrls();
        applicationRunner.readChroniclePages();
        applicationRunner.processChroniclePages();
        applicationRunner.createEbook();
    }

    private final LinkedHashMap<String, ChroniclePage> chronicleGroupings;
    private final LinkedHashMap<ChroniclePage, String> chroniclePagesMap;

    private File outputfolder = new File("output/");
    private File imageOutputFolder = new File(outputfolder, "images/");
    private File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

    public ApplicationRunner() throws MalformedURLException {
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

    private void readChronicleGroupingPages() {
        for (final ChroniclePage groupPage : chronicleGroupings.values()) {
            readPageContent(groupPage);
        }
    }

    private void findChronicleUrls() {
        for (final Entry<String, ChroniclePage> group : chronicleGroupings.entrySet()) {
            final Entry<ChroniclePage, String> result = ChronicleParser.readChronicleUrlsFromGroupPage(group.getKey(),
                    group.getValue(), BASE_URL);

            if (result != null) {
                chroniclePagesMap.put(result.getKey(), result.getValue());
            }
        }
    }

    private void readChroniclePages() {
        for (final ChroniclePage page : chroniclePagesMap.keySet()) {
            readPageContent(page);
        }
    }

    private void processChroniclePages() throws MalformedURLException {
        int index = 1;
        for (final ChroniclePage page : chroniclePagesMap.keySet()) {
            ChronicleParser.getChronicleImageUrl(page, BASE_URL);
            ChronicleParser.downloadChronicleImage(page, index, imageOutputFolder);
            ChronicleParser.createChronicleFromTemplate(page, index);
            index++;
        }
    }

    private void createEbook() {
        // output Cover Image
        // get ebook template
        // add cover image location
        // get table of contents
        // get chronicle templates
    }

    @VisibleForTesting
    void readPageContent(final ChroniclePage page) {
        if (page == null) {
            return;
        }

        final URL pageUrl = page.getPageUrl();
        if (pageUrl != null) {
            final String webPageContent = WebPageUtil.getWebPageContent(pageUrl);
            page.setPageContent(webPageContent);
        }
    }

    @VisibleForTesting
    Map<String, ChroniclePage> getChronicleGroupings() {
        return chronicleGroupings;
    }

    @VisibleForTesting
    Map<ChroniclePage, String> getChroniclePagesMap() {
        return chroniclePagesMap;
    }

    @VisibleForTesting
    void setOutputFolder(final File outputfolder) {
        this.outputfolder = outputfolder;
        imageOutputFolder = new File(this.outputfolder, "images/");
        ebookOutputFile = new File(this.outputfolder, "EveOnline-Chronicles.html");
    }

    private void setupChronicleGroupings(final String groupingName, final URL baseUrl, final String groupingUrl)
            throws MalformedURLException {
        chronicleGroupings.put(groupingName, new ChroniclePage(new URL(baseUrl + groupingUrl)));
    }
}
