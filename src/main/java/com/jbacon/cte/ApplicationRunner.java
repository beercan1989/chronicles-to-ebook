package com.jbacon.cte;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.jbacon.annotations.VisibleForTesting;
import com.jbacon.cte.models.ChroniclePage;
import com.jbacon.cte.utils.ResourceUtil;
import com.jbacon.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ApplicationRunner {
    private static final String CHRONICLE_PARAGRAPHS_VI = "#\\{Chronicle Image Path/URL\\}";
    private static final String CHRONICLE_PARAGRAPHS_V = "#\\{chronicle Title\\}";
    private static final String CHRONICLE_PARAGRAPHS_IV = "#\\{padded chronicle index\\}";
    private static final String CHRONICLE_PARAGRAPHS_III = "#\\{chronicle index\\}";
    private static final String CHRONICLE_TEMPLATE = "/chronicle-template.html";
    private static final String CHRONICLE_PARAGRAPH_TEMPLATE = "/chronicle-paragraph-template.html";
    private static final String CHRONICLE_PARAGRAPHS_II = "#{chronicle-paragraph-template.html}";
    private static final String CHRONICLE_PARAGRAPHS_I = "#{for paragraph in chronicle; do}";
    private static final String DONE = "#{done}";
    private static final String CHRONICLE_PARAGRAPH = "#{single chronicle paragraph}";
    private static final Pattern CHRONICLE_FILTER_IX = Pattern.compile("</*p>");
    private static final Pattern CHRONICLE_FILTER_VIII = Pattern.compile("</p><p>");
    private static final Pattern CHRONICLE_FILTER_VII = Pattern.compile("\n");
    private static final Pattern CHRONICLE_FILTER_VI = Pattern.compile("</a>");
    private static final Pattern CHRONICLE_FILTER_V = Pattern.compile("<a[^>]*>");

    private static final String NEW_LINE = "\n";

    private static final Pattern CHRONICLE_FILTER_IV = Pattern.compile("^$\n", MULTILINE);
    private static final Pattern CHRONICLE_FILTER_III = Pattern.compile("<center>.*</center>");
    private static final Pattern CHRONICLE_FILTER_I = Pattern.compile("<!--[^>]*-->");
    private static final Pattern CHRONICLE_FILTER_II = Pattern.compile("<div id='catlinks'.*", DOTALL);

    public static final String BASE_URL = "http://wiki.eveonline.com";

    public static final String CHAPTER_NUMBER_PLACER = "#{ChapterNumber}";
    public static final String FILE_TYPE_PLACER = "#{FileType}";
    public static final String IMAGE_FILENAME_TEMPLATE = "chapter-" + CHAPTER_NUMBER_PLACER + "." + FILE_TYPE_PLACER;

    private final String findChroniclesOnGroupPageRegex = "<h2>\\s*<span\\s*class=\"mw-headline\"\\s*id=\"Chronological\">\\s*Chronological\\s*</span></h2>.<ul>(?:<li>(<a href=\"[^\"]*\" title=\"[^\"]*\">[^\"]*</a>).</li>)*</ul>";
    private final String matchH2HeaderRegex = "<h2>[^\n]*</h2>";
    private final String matchHtmlBulletPointTags = "<[/]{0,1}(ul|li)>";
    private final String matchEmptyLine = "^$\n";
    private final String matchNewLine = NEW_LINE;
    private final String matchChroniclePageUrlTitleAndName = "<a href=\"([^\"]*)\" title=\"([^\"]*)\">([^<]*)</a>";
    private final String findChronicleContentRegex = "<!-- start content -->(.*)<!-- end content -->";
    private final String findChronicleImageUrlRegex = "<center>.*<img[^>]*src=\"([^\"]*)\".*</center>";

    private final Pattern findChroniclesOnGroupPage = Pattern.compile(findChroniclesOnGroupPageRegex, DOTALL);
    private final Pattern findChronicleUrlTitleName = Pattern.compile(matchChroniclePageUrlTitleAndName);
    private final Pattern findEmptyLines = Pattern.compile(matchEmptyLine, MULTILINE);
    private final Pattern findChronicleContent = Pattern.compile(findChronicleContentRegex, DOTALL);
    private final Pattern findChronicleImageUrl = Pattern.compile(findChronicleImageUrlRegex, DOTALL);

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
            readChronicleUrlsFromGroupPage(group.getKey(), group.getValue());
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
            getChronicleImageUrl(page);
            downloadChronicleImage(page, index);
            createChronicleFromTemplate(page, index);
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
    void createChronicleFromTemplate(final ChroniclePage page, final int index) {
        final String pageContent = page.getPageContent();
        if (pageContent == null || pageContent.isEmpty()) {
            return;
        }

        final Matcher chronicleContentMatcher = findChronicleContent.matcher(pageContent);
        if (!chronicleContentMatcher.find()) {
            return;
        }

        final String filteredPageContent = chronicleContentMatcher.group();
        if (filteredPageContent == null || filteredPageContent.isEmpty()) {
            return;
        }

        // <!--[^>]*--> // Not Dot All
        final String filterOne = CHRONICLE_FILTER_I.matcher(filteredPageContent).replaceAll(EMPTY);

        // <div id='catlinks'.* // Dot All
        final String filterTwo = CHRONICLE_FILTER_II.matcher(filterOne).replaceAll(EMPTY);

        // <center>.*</center> // Not Dot All
        final String filterThree = CHRONICLE_FILTER_III.matcher(filterTwo).replaceAll(EMPTY);

        // ^$\n // Multiline
        final String filterFour = CHRONICLE_FILTER_IV.matcher(filterThree).replaceAll(EMPTY);

        // <a[^>]*> with EMPTY
        final String filterFive = CHRONICLE_FILTER_V.matcher(filterFour).replaceAll(EMPTY);

        // </a> with EMPTY
        final String filterSix = CHRONICLE_FILTER_VI.matcher(filterFive).replaceAll(EMPTY);

        // \n with EMPTY
        final String filterSeven = CHRONICLE_FILTER_VII.matcher(filterSix).replaceAll(EMPTY);

        // </p><p> with \n
        final String filterEight = CHRONICLE_FILTER_VIII.matcher(filterSeven).replaceAll(NEW_LINE);

        // </+p> with EMPTY
        final String filterNine = CHRONICLE_FILTER_IX.matcher(filterEight).replaceAll(EMPTY);

        // Split on \n
        final String[] paragraphs = filterNine.split(NEW_LINE);
        if (paragraphs.length <= 0) {
            return;
        }

        final StringBuilder paragraphsProcessed = new StringBuilder();
        final String paragraphTemplate = ResourceUtil.getResource(CHRONICLE_PARAGRAPH_TEMPLATE);
        for (final String paragraph : paragraphs) {
            paragraphsProcessed.append(paragraphTemplate.replace(CHRONICLE_PARAGRAPH, paragraph)).append(NEW_LINE);
        }

        final String chronicleTemplate = ResourceUtil.getResource(CHRONICLE_TEMPLATE);
        if (chronicleTemplate == null || chronicleTemplate.isEmpty()) {
            return;
        }

        final String processedPageContent = chronicleTemplate.replace(CHRONICLE_PARAGRAPHS_I, EMPTY)
                .replace(CHRONICLE_PARAGRAPHS_II, paragraphsProcessed.toString()).replace(DONE, EMPTY)
                .replaceAll(CHRONICLE_PARAGRAPHS_III, String.valueOf(index))
                .replaceAll(CHRONICLE_PARAGRAPHS_IV, formatIndex(index))
                .replaceAll(CHRONICLE_PARAGRAPHS_V, page.getPageTitle())
                .replaceAll(CHRONICLE_PARAGRAPHS_VI, page.getPageImage().getPath());

        page.setProcessedPageContent(processedPageContent);
    }

    @VisibleForTesting
    void downloadChronicleImage(final ChroniclePage page, final int index) {
        final URL pageImageUrl = page.getPageImageUrl();
        if (pageImageUrl == null) {
            return;
        }

        final File imageDestination = new File(imageOutputFolder, IMAGE_FILENAME_TEMPLATE.replace(
                CHAPTER_NUMBER_PLACER, formatIndex(index)).replace(FILE_TYPE_PLACER, "jpg"));

        if (imageDestination.isFile()) {
            return;
        }

        try {
            FileUtils.copyURLToFile(pageImageUrl, imageDestination);
            page.setPageImage(imageDestination);
        } catch (final IOException e) {
        }
    }

    private String formatIndex(final int index) {
        return String.format("%03d", index);
    }

    @VisibleForTesting
    void getChronicleImageUrl(final ChroniclePage page) throws MalformedURLException {
        final String pageContent = page.getPageContent();
        if (pageContent == null || pageContent.isEmpty()) {
            return;
        }

        final Matcher chronicleContentMatcher = findChronicleContent.matcher(pageContent);
        if (!chronicleContentMatcher.find()) {
            return;
        }

        final String filteredPageContent = chronicleContentMatcher.group();
        if (filteredPageContent == null || filteredPageContent.isEmpty()) {
            return;
        }

        final Matcher chronicleImageUrlMatcher = findChronicleImageUrl.matcher(filteredPageContent);
        if (!chronicleImageUrlMatcher.find()) {
            return;
        }

        final String chroniclePageUrl = chronicleImageUrlMatcher.group(1);
        if (chroniclePageUrl == null || chroniclePageUrl.isEmpty()) {
            return;
        }

        page.setPageImageUrl(new URL(BASE_URL + chroniclePageUrl));
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
    void readChronicleUrlsFromGroupPage(final String groupingName, final ChroniclePage groupPage) {
        final String groupPageContents = WebPageUtil.getWebPageContent(groupPage.getPageUrl());

        if (groupPageContents == null || groupPageContents.isEmpty()) {
            return;
        }

        final Matcher chronologicalChronicleMatcher = findChroniclesOnGroupPage.matcher(groupPageContents);

        if (!chronologicalChronicleMatcher.find()) {
            return;
        }

        final String groupPageContentsReduced = chronologicalChronicleMatcher.group();

        if (groupPageContentsReduced == null || groupPageContentsReduced.isEmpty()) {
            return;
        }

        final String firstReplacement = groupPageContentsReduced.replaceFirst(matchH2HeaderRegex, EMPTY);
        final String secondReplacement = firstReplacement.replaceAll(matchHtmlBulletPointTags, EMPTY);
        final String thirdReplacement = findEmptyLines.matcher(secondReplacement).replaceAll(EMPTY);
        final String[] contentPerLine = thirdReplacement.split(matchNewLine);

        for (final String line : contentPerLine) {
            final Matcher chronicleDetails = findChronicleUrlTitleName.matcher(line);
            if (chronicleDetails.find()) {
                try {
                    final String pageUrl = chronicleDetails.group(1);
                    final String pageTitle = chronicleDetails.group(3);

                    if (pageUrl == null || pageUrl.isEmpty() || pageTitle == null || pageTitle.isEmpty()) {
                        continue;
                    }

                    final ChroniclePage chroniclePage = new ChroniclePage(new URL(BASE_URL + pageUrl));
                    chroniclePage.setPageTitle(pageTitle);
                    chroniclePagesMap.put(chroniclePage, groupingName);

                } catch (final MalformedURLException e) {
                }
            }
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

    private void setupChronicleGroupings(final String groupingName, final String baseUrl, final String groupingUrl)
            throws MalformedURLException {
        chronicleGroupings.put(groupingName, new ChroniclePage(new URL(baseUrl + groupingUrl)));
    }
}
