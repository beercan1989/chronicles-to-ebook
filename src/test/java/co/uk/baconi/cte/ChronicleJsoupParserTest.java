package co.uk.baconi.cte;

import static co.uk.baconi.cte.ChronicleJsoupParser.buildChronicleBodyEntry;
import static co.uk.baconi.cte.ChronicleJsoupParser.buildTableOfContentsEntry;
import static co.uk.baconi.cte.ChronicleJsoupParser.calculateCurrentChronicleIndex;
import static co.uk.baconi.cte.ChronicleJsoupParser.downloadChronicleImage;
import static co.uk.baconi.cte.ChronicleJsoupParser.downloadChroniclePageFromWiki;
import static co.uk.baconi.cte.ChronicleJsoupParser.getChronicleImageDetails;
import static co.uk.baconi.cte.ChronicleJsoupParser.getChronicleParagraphs;
import static co.uk.baconi.cte.ChronicleJsoupParser.getChronicleTitle;
import static co.uk.baconi.cte.ChronicleJsoupParser.updateChronicleImageElement;
import static co.uk.baconi.matchers.Are.are;
import static co.uk.baconi.matchers.Does.does;
import static co.uk.baconi.matchers.FileMatchers.exists;
import static co.uk.baconi.matchers.FileMatchers.isDirectory;
import static co.uk.baconi.matchers.FileMatchers.isFile;
import static co.uk.baconi.matchers.FileMatchers.named;
import static co.uk.baconi.matchers.FileMatchers.sized;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.uk.baconi.cte.utils.ChronicleParserUtil;
import co.uk.baconi.cte.utils.ResourceUtil;

public class ChronicleJsoupParserTest {

    private static final File TEST_OUTPUT_FOLDER = new File("test-output-folder");
    private static final String TEST_CHRONICLE_PAGE = "/fedo-chronicle-page.html";

    @Before
    public void before() {
        TEST_OUTPUT_FOLDER.mkdir();

        assertThat(TEST_OUTPUT_FOLDER, does(exists()));
        assertThat(TEST_OUTPUT_FOLDER, isDirectory());
    }

    @Test
    public void shouldBeAbleToBuildTableOfContentsEntry() {
        final Document ebook = getTestEbook();
        assertThat(ebook, is(not(nullValue())));

        buildTableOfContentsEntry(ebook, 1, "First Chronicle");

        final Element tableOfContents = ebook.select("div.TableOfConents").first();
        assertThat(tableOfContents.select("a").last().attr("href"), is(equalTo("#chap-1")));
    }

    @Test
    public void shouldBeAbleToBuildChronicleBodyEntry() throws IOException {
        final Document ebook = getTestEbook();
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        final Element chronicleImage = getChronicleImageDetails(downloadedChronicle);
        final Elements chronicleParagraphs = getChronicleParagraphs(downloadedChronicle);

        assertThat(ebook, is(not(nullValue())));
        assertThat(downloadedChronicle, is(not(nullValue())));
        assertThat(chronicleImage, is(not(nullValue())));
        assertThat(chronicleParagraphs, is(not(nullValue())));

        buildChronicleBodyEntry(ebook, 1, "First Chronicle", chronicleImage, chronicleParagraphs);

        final Element chronicleEntry = ebook.select("div.Chronicle").first();
        assertThat(chronicleEntry.children().size(), is(equalTo(5)));
        assertThat(chronicleEntry.select("a").first().attr("id"), is(equalTo("chap-1")));
        assertThat(chronicleEntry.select("h4").first().text(), containsString("First Chronicle"));
        assertThat(chronicleEntry.select("div.ChronicleBody").first().text(),
                containsString("Fedos on many of their ships for cleaning"));
    }

    @Test
    public void shouldBeAbleToUpdateChronicleImageElement() throws IOException {
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        final Element chronicleImage = getChronicleImageDetails(downloadedChronicle);

        assertThat(downloadedChronicle, is(not(nullValue())));
        assertThat(chronicleImage, is(not(nullValue())));
        assertThat(TEST_OUTPUT_FOLDER, is(not(nullValue())));
        assertThat(chronicleImage.attr("src"), is(not(equalTo("test-output-folder/testImage.gif"))));

        updateChronicleImageElement(chronicleImage, new File("testImage.gif"), TEST_OUTPUT_FOLDER);

        assertThat(chronicleImage, is(not(nullValue())));
        assertThat(chronicleImage.attr("src"), is(not(nullValue())));
        assertThat(chronicleImage.attr("src"), is(equalTo("test-output-folder/testImage.gif")));
    }

    @Test
    public void shouldBeAbleToDownloadChronicleImage() throws IOException {
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        downloadedChronicle.setBaseUri("http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)");
        final Element chronicleImage = getChronicleImageDetails(downloadedChronicle);

        assertThat(downloadedChronicle, is(not(nullValue())));
        assertThat(chronicleImage, is(not(nullValue())));
        assertThat(TEST_OUTPUT_FOLDER, does(exists()));
        assertThat(TEST_OUTPUT_FOLDER, isDirectory());

        final File downloaded = downloadChronicleImage(downloadedChronicle, chronicleImage, TEST_OUTPUT_FOLDER);

        assertThat(downloaded, is(not(nullValue())));
        assertThat(downloaded, does(exists()));
        assertThat(downloaded, isFile());
        assertThat(downloaded, is(sized(greaterThanOrEqualTo(50000L))));
        assertThat(downloaded, is(named(equalTo("Fedo_big.jpg"))));
    }

    @Test
    public void shouldBeAbleToCalculateCurrentChronicleIndex() {
        final Document ebook = getTestEbook();
        assertThat(ebook, is(not(nullValue())));

        final Integer firstCalculatedIndex = calculateCurrentChronicleIndex(ebook);
        assertThat(firstCalculatedIndex, is(not(nullValue())));
        assertThat(firstCalculatedIndex, is(equalTo(1)));

        ebook.select("div.BookBody").first().appendElement("div").attr("class", "Chronicle");

        final Integer secondCalculatedIndex = calculateCurrentChronicleIndex(ebook);
        assertThat(secondCalculatedIndex, is(not(nullValue())));
        assertThat(secondCalculatedIndex, is(equalTo(2)));
    }

    @Test
    public void shouldBeAbleToGetChronicleImageDetails() throws IOException {
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        assertThat(downloadedChronicle, is(not(nullValue())));

        final Element chronicleImageDetails = getChronicleImageDetails(downloadedChronicle);
        assertThat(chronicleImageDetails, is(not(nullValue())));
        assertThat(chronicleImageDetails.attr("alt"), is(equalToIgnoringCase("Fedo big.jpg")));
        assertThat(chronicleImageDetails.attr("src"), endsWith("Fedo_big.jpg"));
    }

    @Test
    public void shouldBeAbleToGetChronicleParagraphs() throws IOException {
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        assertThat(downloadedChronicle, is(not(nullValue())));

        final Elements paragraphs = getChronicleParagraphs(downloadedChronicle);
        assertThat(paragraphs, are(not(nullValue())));
        assertThat(paragraphs, are(not(empty())));
        assertThat(paragraphs.size(), is(equalTo(4)));
        assertThat(paragraphs.first().text(), containsString("A Fedo is a fairly small"));
        assertThat(paragraphs.last().text(), containsString("the Fedo's life cycle is only a few weeks long."));
    }

    @Test
    public void shouldBeAbleToGetChronicleTitle() throws IOException {
        final Document downloadedChronicle = getTestData(TEST_CHRONICLE_PAGE);
        assertThat(downloadedChronicle, is(not(nullValue())));

        final String chronicleTitle = getChronicleTitle(downloadedChronicle);
        assertThat(chronicleTitle, is(not(nullValue())));
        assertThat(chronicleTitle, is(equalToIgnoringCase("Fedo (Chronicle)")));
    }

    @Test
    public void shouldBeAbleToDownloadChroniclePageFromWiki() throws IOException {
        final URL chronicleUrl = new URL("http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)");
        final File chronicleDownloadFolder = new File(TEST_OUTPUT_FOLDER, "chronicleDownloads");

        final Document result = downloadChroniclePageFromWiki(chronicleUrl, chronicleDownloadFolder);

        assertThat(result, is(not(nullValue())));
        assertThat(result.hasText(), is(true));
        assertThat(result.text(), containsString("Fedo"));
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(TEST_OUTPUT_FOLDER);

        assertThat(TEST_OUTPUT_FOLDER, does(not(exists())));
    }

    private Document getTestData(final String testDataFilename) throws IOException {
        return Jsoup.parse(ResourceUtil.getFile(testDataFilename), "UTF-8");
    }

    private Document getTestEbook() {
        return ChronicleParserUtil.buildBaseEbook(TEST_OUTPUT_FOLDER);
    }
}
