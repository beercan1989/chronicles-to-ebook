package com.jbacon.cte;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.jbacon.annotations.VisibleForTesting;
import com.jbacon.cte.models.ChroniclePage;
import com.jbacon.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ApplicationRunner {
    public static final String BASE_URL = "http://wiki.eveonline.com";

    public static final String IMAGE_FILENAME_TEMPLATE = "chapter-#{ChapterNumber}.#{FileType}";

    private final String findChroniclesOnGroupPageRegex = "<h2>\\s*<span\\s*class=\"mw-headline\"\\s*id=\"Chronological\">\\s*Chronological\\s*</span></h2>.<ul>(?:<li>(<a href=\"[^\"]*\" title=\"[^\"]*\">[^\"]*</a>).</li>)*</ul>";
    private final String matchH2HeaderRegex = "<h2>[^\n]*</h2>";
    private final String matchHtmlBulletPointTags = "<[/]{0,1}(ul|li)>";
    private final String matchEmptyLine = "^$\n";
    private final String matchNewLine = "\n";
    private final String matchChroniclePageUrlTitleAndName = "<a href=\"([^\"]*)\" title=\"([^\"]*)\">([^<]*)</a>";
    private final String findChronicleContentRegex = "<!-- start content -->(.*)<!-- end content -->";
    private final String findChronicleImageUrlRegex = "<center>.*<img[^>]*src=\"([^\"]*)\".*</center>";

    private final Pattern findChroniclesOnGroupPage = Pattern.compile(findChroniclesOnGroupPageRegex, Pattern.DOTALL);
    private final Pattern findChronicleUrlTitleName = Pattern.compile(matchChroniclePageUrlTitleAndName);
    private final Pattern findEmptyLines = Pattern.compile(matchEmptyLine, Pattern.MULTILINE);
    private final Pattern findChronicleContent = Pattern.compile(findChronicleContentRegex, Pattern.DOTALL);
    private final Pattern findChronicleImageUrl = Pattern.compile(findChronicleImageUrlRegex, Pattern.DOTALL);

    public static final void main(final String[] programParams) throws MalformedURLException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        final ApplicationRunner applicationRunner = new ApplicationRunner();
        applicationRunner.readChronicleGroupingPages();
        applicationRunner.findChronicleUrls();
        applicationRunner.readChroniclePages();
        applicationRunner.processChroniclePages();
        // applicationRunner.createEbook();
    }

    private final Map<String, ChroniclePage> chronicleGroupings;
    private final Map<ChroniclePage, String> chroniclePagesMap;

    private File outputfolder = new File("output/");
    private final File imageOutputFolder = new File(outputfolder, "images/");
    private final File ebookOutputFile = new File(outputfolder, "EveOnline-Chronicles.html");

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
        for (final ChroniclePage page : chroniclePagesMap.keySet()) {
            getChronicleImageUrl(page);
            downloadChronicleImage(page);
            // createChronicleFromTemplate(page);
        }
    }

    @VisibleForTesting
    void downloadChronicleImage(final ChroniclePage page) {

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

        final String firstReplacement = groupPageContentsReduced.replaceFirst(matchH2HeaderRegex, StringUtils.EMPTY);
        final String secondReplacement = firstReplacement.replaceAll(matchHtmlBulletPointTags, StringUtils.EMPTY);
        final String thirdReplacement = findEmptyLines.matcher(secondReplacement).replaceAll(StringUtils.EMPTY);
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
    }

    private void setupChronicleGroupings(final String groupingName, final String baseUrl, final String groupingUrl)
            throws MalformedURLException {
        chronicleGroupings.put(groupingName, new ChroniclePage(new URL(baseUrl + groupingUrl)));
    }
}
