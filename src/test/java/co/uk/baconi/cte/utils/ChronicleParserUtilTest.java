package co.uk.baconi.cte.utils;

import static co.uk.baconi.cte.utils.ChronicleParserUtil.appendChildren;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.cleanInnerHtml;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.downloadChroniclePageFromWiki;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getBaseUrl;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getChroniclePages;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getDownloadableImageUrl;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.getImageFileName;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.last;
import static co.uk.baconi.cte.utils.ChronicleParserUtil.padded;
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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChronicleParserUtilTest {

    private static final String EMPTY = "";
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
    public void shouldBeAbleToDownloadChroniclePageFromWiki() throws IOException {
        final URL chronicleUrl = new URL("http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)");
        final File chronicleDownloadFolder = new File(TEST_OUTPUT_FOLDER, "chronicleDownloads");

        final Document result = downloadChroniclePageFromWiki(chronicleUrl, chronicleDownloadFolder);

        assertThat(result, is(not(nullValue())));
        assertThat(result.hasText(), is(true));
        assertThat(result.text(), containsString("Fedo"));
    }

    @Test
    public void shouldBeAbleToAppendChildren() {
        final Element parent = new Element(Tag.valueOf("div"), EMPTY);
        final Elements children = new Elements(new Element(Tag.valueOf("p"), EMPTY), new Element(Tag.valueOf("p"), EMPTY));

        assertThat(parent.children(), hasSize(0));

        final Element result = appendChildren(parent, children);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(equalTo(parent)));
        assertThat(result.children(), hasSize(2));
    }

    @Test
    public void shouldBeAbleToPadded() {
        final String result = padded(1);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(equalTo("001")));
    }

    @Test
    public void shouldBeAbleToCleanInnerHtml() {
        final Element elementOne = new Element(Tag.valueOf("p"), EMPTY).html("<p>Test 1</p>");
        final Element elementTwo = new Element(Tag.valueOf("p"), EMPTY).html("<p>Test 2</p>");
        final Elements elements = new Elements(elementOne, elementTwo);

        final Elements result = cleanInnerHtml(elements);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(equalTo(elements)));
        assertThat(result.first(), is(equalTo(elementOne)));
        assertThat(result.first().html(), is(equalTo("Test 1")));
        assertThat(result.last(), is(equalTo(elementTwo)));
        assertThat(result.last().html(), is(equalTo("Test 2")));
    }

    @Test
    public void shouldBeAbleToGetImageFileName() throws MalformedURLException {
        final URL imageUrl = new URL("http://wiki.eveonline.com/wikiEN/images/c/ca/Fedosong.jpg");

        final String result = getImageFileName(imageUrl);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(equalTo("Fedosong.jpg")));
    }

    @Test
    public void shouldBeAbleToLastInArray() {
        final String[] firstArray = null;
        final Integer[] secondArray = { 1, 2, 3, 4, 5 };
        final Object[] thirdArray = { 1L, 5F, "Test" };

        final String resultOne = last(firstArray);
        assertThat(resultOne, is(nullValue()));

        final Integer resultTwo = last(secondArray);
        assertThat(resultTwo, is(not(nullValue())));
        assertThat(resultTwo, is(equalTo(5)));

        final Object resultThree = last(thirdArray);
        assertThat(resultThree, is(not(nullValue())));
        assertThat(resultThree, is(instanceOf(String.class)));
        assertThat(resultThree.toString(), is(equalTo("Test")));
    }

    @Test
    public void shouldBeAbleToGetDownloadableImageUrl() throws MalformedURLException {
        final Document chronicle = Document.createShell("http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)");
        final Element image = new Element(Tag.valueOf("img"), EMPTY).attr("src", "/wikiEN/images/c/ca/Fedosong.jpg");

        final URL result = getDownloadableImageUrl(chronicle, image);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(equalTo(new URL("http://wiki.eveonline.com/wikiEN/images/c/ca/Fedosong.jpg"))));
    }

    @Test
    public void shouldBeAbleToGetBaseUrl() throws MalformedURLException {
        final Node testNode = new Element(Tag.valueOf("p"), "http://wiki.eveonline.com/en/wiki/Fedo_(Chronicle)");

        final String baseUrl = getBaseUrl(testNode);

        assertThat(baseUrl, is(not(nullValue())));
        assertThat(baseUrl, is(equalTo("http://wiki.eveonline.com")));
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(TEST_OUTPUT_FOLDER);

        assertThat(TEST_OUTPUT_FOLDER, does(not(exists())));
    }

}
