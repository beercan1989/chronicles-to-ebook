package co.uk.baconi.cte;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import co.uk.baconi.annotations.VisibleForTesting;
import co.uk.baconi.cte.models.ChroniclePage;
import co.uk.baconi.cte.utils.ResourceUtil;
import co.uk.baconi.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ApplicationRunner {

    @VisibleForTesting
    static final URL BASE_URL = WebPageUtil.getUrl("http://wiki.eveonline.com");

    private static final String FOR_EACH_PLACER = "#\\{for Chronicle in Chronicles; do\\}";
    private static final String CHRONICLE_PLACER = "#{chronicle-template.html}";
    private static final String TABLE_OF_CONTENTS__PLACER = "#{table-of-contents-entry-template.html}";
    private static final String IMAGES_FOLDER_PLACER = "#{imagesFolder}";
    private static final String EBOOK_TEMPLATE = "ebook-template.html";
    private static final String DONE_PLACER = "#\\{done\\}";
    private static final String CHRONICLE_COVER_IMAGE = "/EveOnlineChroniclesCover.jpg";
    private static final String TABLE_OF_CONTENTS_TEMPLATE = "table-of-contents-entry-template.html";

    public static final void main(final String[] programParams) throws IOException {
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
            ChronicleParser.readChronicleUrlsFromGroupPage(group.getKey(), group.getValue(), BASE_URL);
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

    private void createEbook() throws IOException {
        outputCoverImage();

        // get table of contents
        final String tableOfContents = getTableOfContents();

        // get chronicle templates
        final String chronicleContents = getChroniclesContent();

        final String ebook = ResourceUtil.getString(EBOOK_TEMPLATE)
                .replace(IMAGES_FOLDER_PLACER, imageOutputFolder.getName())
                .replace(TABLE_OF_CONTENTS__PLACER, tableOfContents).replace(CHRONICLE_PLACER, chronicleContents)
                .replaceAll(DONE_PLACER, EMPTY).replaceAll(FOR_EACH_PLACER, EMPTY);

        FileUtils.write(ebookOutputFile, ebook, "UTF-8");
    }

    private String getChroniclesContent() {
        final StringBuilder chronicleContents = new StringBuilder();

        for (final ChroniclePage page : chroniclePagesMap.keySet()) {
            chronicleContents.append(page.getProcessedPageContent());
        }

        return chronicleContents.toString();
    }

    private String getTableOfContents() {
        final StringBuilder tableOfContents = new StringBuilder();
        final String tableOfContentsTemplate = ResourceUtil.getString(TABLE_OF_CONTENTS_TEMPLATE);

        int index = 1;
        for (final ChroniclePage page : chroniclePagesMap.keySet()) {
            tableOfContents.append(tableOfContentsTemplate.replace("#{chronicle index}", String.valueOf(index))
                    .replace("#{Chronicle Title}", page.getPageTitle()));
            index++;
        }

        return tableOfContents.toString();
    }

    @VisibleForTesting
    void outputCoverImage() {
        final File coverImage = ResourceUtil.getFile(CHRONICLE_COVER_IMAGE);
        try {
            FileUtils.copyFileToDirectory(coverImage, imageOutputFolder);
        } catch (final IOException e) {
        }
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
