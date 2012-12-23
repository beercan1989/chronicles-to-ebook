package co.uk.baconi.cte.utils;

import static co.uk.baconi.cte.utils.ChronicleParserUtil.downloadChroniclePageFromWiki;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getChroniclePages;
import static co.uk.baconi.matchers.Does.does;
import static co.uk.baconi.matchers.FileMatchers.exists;
import static co.uk.baconi.matchers.FileMatchers.isDirectory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChronicleParserUtilTest {

    private static final File TEST_OUTPUT_FOLDER = new File("test-output-folder");

    @Before
    public void before() {
        TEST_OUTPUT_FOLDER.mkdir();

        assertThat(TEST_OUTPUT_FOLDER, does(exists()));
        assertThat(TEST_OUTPUT_FOLDER, isDirectory());
    }

    @Test
    public void shouldBeAbleToGetChroniclePages() throws MalformedURLException, IOException {
        final URL colletionUrl = new URL("http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)");
        final File outputFile = new File(TEST_OUTPUT_FOLDER, "test-output.html");

        assertThat(colletionUrl, is(not(nullValue())));
        assertThat(outputFile, is(not(nullValue())));

        final List<URL> chroniclePages = getChroniclePages(colletionUrl, outputFile);

        assertThat(chroniclePages, is(not(nullValue())));
        assertThat(chroniclePages, is(not(empty())));
        assertThat(chroniclePages, hasSize(65));
        assertThat(chroniclePages.get(0).toString(), containsString("Fedo_(Chronicle)"));
    }

    @Test
    public void shouldBeAbleToAppendChildren() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToPadded() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToCleanInnerHtml() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToGetImageFileName() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToLastInArray() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToGetDownloadableImageUrl() {
        fail("Not yet implemented");
    }

    @Test
    public void shouldBeAbleToBuildBaseEbook() {
        fail("Not yet implemented");
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

}
