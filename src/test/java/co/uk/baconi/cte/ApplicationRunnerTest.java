package co.uk.baconi.cte;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import co.uk.baconi.cte.models.ChroniclePage;
import co.uk.baconi.cte.utils.ResourceUtil;
import co.uk.baconi.cte.utils.WebPageUtilTest;

public class ApplicationRunnerTest {

    private static final String EXPECTED_CHRONICLE_PART_III = "<img src=\"testOutputFolder/images/chapter-001.jpg\" alt=\"Chapter 001 - Fedo\" />";
    private static final String EXPECTED_CHRONICLE_PART_II = "<mbp:pagebreak/>";
    private static final String EXPECTED_CHRONICLE_PART_I = "<a name=\"chap1\">";
    private static final String EXPECTED_IMAGE_FILENAME = "chapter-001.jpg";
    private static final String CHRONICLE_GROUP_PAGE = "/pre-launch-page.html";
    private static final String CHRONICLE_PAGE = "/fedo-chronicle-page.html";
    private static final String EXPECTED_TEST_IMAGE_URL = "/wikiEN/images/d/d6/Fedo_big.jpg";

    private static final File TEST_OUTPUT_FOLDER = new File("testOutputFolder/");
    private static final File TEST_IMAGES_FOLDER = new File(TEST_OUTPUT_FOLDER, "images/");

    @Test
    public void shouldBeAbleToCreateChronicleFromTemplate() throws IOException {
        final ChroniclePage page = new ChroniclePage(null);
        page.setPageContent(ResourceUtil.getResource(CHRONICLE_PAGE));
        page.setPageImageUrl(new URL(ApplicationRunner.BASE_URL + EXPECTED_TEST_IMAGE_URL));
        page.setPageTitle("Fedo");

        final File mockedImageFile = Mockito.mock(File.class);
        Mockito.when(mockedImageFile.getPath()).thenReturn("testOutputFolder/images/chapter-001.jpg");
        page.setPageImage(mockedImageFile);

        assertThat(page.getPageImageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getProcessedPageContent(), is(nullValue()));

        ChronicleParser.createChronicleFromTemplate(page, 1);

        assertThat(page.getPageImageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getProcessedPageContent(), is(not(nullValue())));
        assertThat(page.getProcessedPageContent(), containsString(EXPECTED_CHRONICLE_PART_I));
        assertThat(page.getProcessedPageContent(), containsString(EXPECTED_CHRONICLE_PART_II));
        assertThat(page.getProcessedPageContent(), containsString(EXPECTED_CHRONICLE_PART_III));

        System.out.println(page.getProcessedPageContent());
    }

    @Test
    public void shouldBeAbleToDownloadChronicleImage() throws IOException {
        assertThat(TEST_OUTPUT_FOLDER.exists(), is(not(true)));

        TEST_OUTPUT_FOLDER.mkdir();
        assertThat(TEST_OUTPUT_FOLDER.isDirectory(), is(true));

        final ChroniclePage page = new ChroniclePage(null);
        page.setPageImageUrl(new URL(ApplicationRunner.BASE_URL + EXPECTED_TEST_IMAGE_URL));

        assertThat(page.getPageImageUrl(), is(not(nullValue())));
        assertThat(page.getPageUrl(), is(nullValue()));

        try {
            ChronicleParser.downloadChronicleImage(page, 1, TEST_OUTPUT_FOLDER);

            assertThat(TEST_OUTPUT_FOLDER.isDirectory(), is(true));
            TEST_IMAGES_FOLDER.mkdir();
            assertThat(TEST_IMAGES_FOLDER.isDirectory(), is(true));
            assertThat(Arrays.asList(TEST_IMAGES_FOLDER.listFiles()), is(not(empty())));

            final File pageImage = page.getPageImage();

            assertThat(pageImage, is(not(nullValue())));
            assertThat(pageImage.isFile(), is(true));
            assertThat(pageImage.getName(), is(equalTo(EXPECTED_IMAGE_FILENAME)));
        } finally {
            FileUtils.deleteDirectory(TEST_OUTPUT_FOLDER);
            assertThat(TEST_OUTPUT_FOLDER.exists(), is(not(true)));
        }
    }

    @Test
    public void shouldBeAbleToGetChronicleImageUrl() throws IOException {
        final ChroniclePage page = new ChroniclePage(null);
        page.setPageContent(ResourceUtil.getResource(CHRONICLE_PAGE));

        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getPageImageUrl(), is(nullValue()));

        ChronicleParser.getChronicleImageUrl(page, ApplicationRunner.BASE_URL);

        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getPageImageUrl(), is(not(nullValue())));

        final String chronicleImageUrl = page.getPageImageUrl().toString();
        assertThat(chronicleImageUrl, containsString(EXPECTED_TEST_IMAGE_URL));
    }

    @Test
    public void shouldReadPageContent() throws MalformedURLException {
        final ChroniclePage page = new ChroniclePage(new URL(WebPageUtilTest.SUCCESSFUL_TEST_URL));

        assertThat(page.getPageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(nullValue()));

        new ApplicationRunner().readPageContent(page);

        assertThat(page.getPageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getPageContent(), containsString(WebPageUtilTest.SUCCESSFUL_PAGE_CONTENT));
    }

    @Test
    public void shouldReadChronicleUrlsFromGroupPage() throws IOException {
        final ChroniclePage groupPage = new ChroniclePage(new URL(WebPageUtilTest.SUCCESSFUL_TEST_URL));
        groupPage.setPageContent(ResourceUtil.getResource(CHRONICLE_GROUP_PAGE));

        assertThat(groupPage.getPageUrl(), is(not(nullValue())));
        assertThat(groupPage.getPageContent(), is(not(nullValue())));

        final ApplicationRunner app = new ApplicationRunner();

        final String firstGroupName = app.getChronicleGroupings().keySet().toArray(new String[0])[0];

        ChronicleParser.readChronicleUrlsFromGroupPage(firstGroupName, groupPage, ApplicationRunner.BASE_URL);

        assertThat(groupPage.getPageUrl(), is(not(nullValue())));

        final Set<ChroniclePage> list = app.getChroniclePagesMap().keySet();

        assertThat(list, is(not(nullValue())));
        assertThat(list, is(not(empty())));

        for (final ChroniclePage page : list) {
            assertThat(page, is(not(nullValue())));
            assertThat(page.getPageUrl(), is(not(nullValue())));
            assertThat(page.getPageTitle(), is(not(nullValue())));
            assertThat(page.getPageTitle(), is(not(equalTo(StringUtils.EMPTY))));
        }
    }
}
