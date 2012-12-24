package co.uk.baconi.cte.parsers;

import static co.uk.baconi.cte.parsers.ChronicleParser.buildChronicleBodyEntry;
import static co.uk.baconi.cte.parsers.ChronicleParser.buildTableOfContentsEntry;
import static co.uk.baconi.cte.parsers.ChronicleParser.calculateCurrentChronicleIndex;
import static co.uk.baconi.cte.parsers.ChronicleParser.downloadChronicleImage;
import static co.uk.baconi.cte.parsers.ChronicleParser.getChronicleImageDetails;
import static co.uk.baconi.cte.parsers.ChronicleParser.getChronicleParagraphs;
import static co.uk.baconi.cte.parsers.ChronicleParser.getChronicleTitle;
import static co.uk.baconi.cte.parsers.ChronicleParser.updateChronicleImageElement;
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
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import co.uk.baconi.cte.AbstractChronicleTest;
import co.uk.baconi.cte.utils.ChronicleParserUtil;
import co.uk.baconi.cte.utils.ResourceUtil;

public class ChronicleParserTest extends AbstractChronicleTest {

    @Test
    public void test1() throws MalformedURLException {
        ChronicleParser.parseChroniclePage(ChronicleParserUtil.buildBaseEbook(TEST_OUTPUT_FOLDER), new URL(
                "http://wiki.eveonline.com/en/wiki/The_Hanging_Long-limb_(Chronicle)"), TEST_OUTPUT_FOLDER,
                TEST_OUTPUT_FOLDER);
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

    private Document getTestData(final String testDataFilename) throws IOException {
        return Jsoup.parse(ResourceUtil.getFile(testDataFilename), "UTF-8");
    }

    private Document getTestEbook() {
        return ChronicleParserUtil.buildBaseEbook(TEST_OUTPUT_FOLDER);
    }
}
