package co.uk.baconi.cte;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import co.uk.baconi.cte.models.ChroniclePage;
import co.uk.baconi.cte.utils.ResourceUtil;
import co.uk.baconi.cte.utils.WebPageUtil;

public final class ChronicleParser {
    private static final String NEW_LINE = "\n";

    private static final String CHRONICLE_PARAGRAPHS_I = "#{for paragraph in chronicle; do}";
    private static final String CHRONICLE_PARAGRAPHS_II = "#{chronicle-paragraph-template.html}";
    private static final String CHRONICLE_PARAGRAPHS_III = "#\\{chronicle index\\}";
    private static final String CHRONICLE_PARAGRAPHS_IV = "#\\{padded chronicle index\\}";
    private static final String CHRONICLE_PARAGRAPHS_V = "#\\{chronicle Title\\}";
    private static final String CHRONICLE_PARAGRAPHS_VI = "#\\{Chronicle Image Path/URL\\}";

    private static final String CHRONICLE_TEMPLATE = "/chronicle-template.html";
    private static final String CHRONICLE_PARAGRAPH_TEMPLATE = "/chronicle-paragraph-template.html";

    private static final String DONE = "#{done}";
    private static final String CHRONICLE_PARAGRAPH = "#{single chronicle paragraph}";

    private static final String CHAPTER_NUMBER_PLACER = "#{ChapterNumber}";
    private static final String FILE_TYPE_PLACER = "#{FileType}";
    private static final String IMAGE_FILENAME_TEMPLATE = "chapter-" + CHAPTER_NUMBER_PLACER + "." + FILE_TYPE_PLACER;

    private static final Pattern CHRONICLE_FILTER_I = Pattern.compile("<!--[^>]*-->");
    private static final Pattern CHRONICLE_FILTER_II = Pattern.compile("<div id='catlinks'.*", DOTALL);
    private static final Pattern CHRONICLE_FILTER_III = Pattern.compile("<center>.*</center>");
    private static final Pattern CHRONICLE_FILTER_IV = Pattern.compile("^$\n", MULTILINE);
    private static final Pattern CHRONICLE_FILTER_V = Pattern.compile("<a[^>]*>");
    private static final Pattern CHRONICLE_FILTER_VI = Pattern.compile("</a>");
    private static final Pattern CHRONICLE_FILTER_VII = Pattern.compile("\n");
    private static final Pattern CHRONICLE_FILTER_VIII = Pattern.compile("</p><p>");
    private static final Pattern CHRONICLE_FILTER_IX = Pattern.compile("</*p>");

    private static final String findChronicleContentRegex = "<!-- start content -->(.*)<!-- end content -->";
    private static final String findChronicleImageUrlRegex = "<center>.*<img[^>]*src=\"([^\"]*)\".*</center>";
    private static final String findChroniclesOnGroupPageRegex = "<h2>\\s*<span\\s*class=\"mw-headline\"\\s*id=\"Chronological\">\\s*Chronological\\s*</span></h2>.<ul>(?:<li>(<a href=\"[^\"]*\" title=\"[^\"]*\">[^\"]*</a>).</li>)*</ul>";

    private static final String matchH2HeaderRegex = "<h2>[^\n]*</h2>";
    private static final String matchHtmlBulletPointTags = "<[/]{0,1}(ul|li)>";
    private static final String matchEmptyLine = "^$\n";
    private static final String matchNewLine = NEW_LINE;
    private static final String matchChroniclePageUrlTitleAndName = "<a href=\"([^\"]*)\" title=\"([^\"]*)\">([^<]*)</a>";

    private static final Pattern findChronicleContent = Pattern.compile(findChronicleContentRegex, DOTALL);
    private static final Pattern findChronicleImageUrl = Pattern.compile(findChronicleImageUrlRegex, DOTALL);
    private static final Pattern findChroniclesOnGroupPage = Pattern.compile(findChroniclesOnGroupPageRegex, DOTALL);
    private static final Pattern findChronicleUrlTitleName = Pattern.compile(matchChroniclePageUrlTitleAndName);
    private static final Pattern findEmptyLines = Pattern.compile(matchEmptyLine, MULTILINE);

    private ChronicleParser() {
    }

    public static void createChronicleFromTemplate(final ChroniclePage page, final int chronicleIndex) {
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
        final String paragraphTemplate = ResourceUtil.getString(CHRONICLE_PARAGRAPH_TEMPLATE);
        for (final String paragraph : paragraphs) {
            paragraphsProcessed.append(paragraphTemplate.replace(CHRONICLE_PARAGRAPH, paragraph)).append(NEW_LINE);
        }

        final String chronicleTemplate = ResourceUtil.getString(CHRONICLE_TEMPLATE);
        if (chronicleTemplate == null || chronicleTemplate.isEmpty()) {
            return;
        }

        final String processedPageContent = chronicleTemplate.replace(CHRONICLE_PARAGRAPHS_I, EMPTY)
                .replace(CHRONICLE_PARAGRAPHS_II, paragraphsProcessed.toString()).replace(DONE, EMPTY)
                .replaceAll(CHRONICLE_PARAGRAPHS_III, String.valueOf(chronicleIndex))
                .replaceAll(CHRONICLE_PARAGRAPHS_IV, formatIndex(chronicleIndex))
                .replaceAll(CHRONICLE_PARAGRAPHS_V, page.getPageTitle())
                .replaceAll(CHRONICLE_PARAGRAPHS_VI, page.getPageImage().getPath());

        page.setProcessedPageContent(processedPageContent);
    }

    public static void downloadChronicleImage(final ChroniclePage page, final int index, final File imageOutputFolder) {
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

    public static void getChronicleImageUrl(final ChroniclePage page, final URL baseUrl) throws MalformedURLException {
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

        page.setPageImageUrl(new URL(baseUrl + chroniclePageUrl));
    }

    public static Entry<ChroniclePage, String> readChronicleUrlsFromGroupPage(final String groupingName,
            final ChroniclePage groupPage, final URL baseUrl) {
        final String groupPageContents = WebPageUtil.getWebPageContent(groupPage.getPageUrl());

        if (groupPageContents == null || groupPageContents.isEmpty()) {
            return null;
        }

        final Matcher chronologicalChronicleMatcher = findChroniclesOnGroupPage.matcher(groupPageContents);

        if (!chronologicalChronicleMatcher.find()) {
            return null;
        }

        final String groupPageContentsReduced = chronologicalChronicleMatcher.group();

        if (groupPageContentsReduced == null || groupPageContentsReduced.isEmpty()) {
            return null;
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

                    final ChroniclePage chroniclePage = new ChroniclePage(new URL(baseUrl + pageUrl));
                    chroniclePage.setPageTitle(pageTitle);

                    return new Entry<ChroniclePage, String>() {
                        @Override
                        public String setValue(final String value) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getValue() {
                            return groupingName;
                        }

                        @Override
                        public ChroniclePage getKey() {
                            return chroniclePage;
                        }
                    };
                } catch (final MalformedURLException e) {
                }
            }
        }

        return null;
    }

    private static String formatIndex(final int index) {
        return String.format("%03d", index);
    }
}
