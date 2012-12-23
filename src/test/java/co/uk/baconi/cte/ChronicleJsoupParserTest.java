package co.uk.baconi.cte;

import static co.uk.baconi.cte.ChronicleJsoupParser.downloadChroniclePageFromWiki;
import static co.uk.baconi.cte.ChronicleJsoupParser.getChronicleParagraphs;
import static co.uk.baconi.cte.ChronicleJsoupParser.getChronicleTitle;
import static co.uk.baconi.matchers.Are.are;
import static co.uk.baconi.matchers.Does.does;
import static co.uk.baconi.matchers.FileMatchers.exists;
import static co.uk.baconi.matchers.FileMatchers.isDirectory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToBuildChronicleBodyEntry() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToUpdateChronicleImageElement() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToDownloadChronicleImage() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToCalculateCurrentChronicleIndex() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToGetChronicleImageDetails() {
        fail("Not yet implemented");
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

}
