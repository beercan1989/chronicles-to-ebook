package com.jbacon.cte;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.jbacon.cte.models.ChroniclePage;
import com.jbacon.cte.utils.WebPageUtilTest;

public class ApplicationRunnerTest {

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
        groupPage.setPageContent(getResourceContents("/pre-launch-page.html"));

        assertThat(groupPage.getPageUrl(), is(not(nullValue())));
        assertThat(groupPage.getPageContent(), is(not(nullValue())));

        final ApplicationRunner app = new ApplicationRunner();

        final String firstGroupName = app.getChronicleGroupings().keySet().toArray(new String[0])[0];

        app.readChronicleUrlsFromGroupPage(firstGroupName, groupPage);

        assertThat(groupPage.getPageUrl(), is(not(nullValue())));

        final List<ChroniclePage> list = app.getChroniclePagesMap().get(firstGroupName);

        assertThat(list, is(not(nullValue())));
        assertThat(list, is(not(empty())));

        for (final ChroniclePage page : list) {
            assertThat(page, is(not(nullValue())));
            assertThat(page.getPageUrl(), is(not(nullValue())));
            assertThat(page.getPageTitle(), is(not(nullValue())));
            assertThat(page.getPageTitle(), is(not(equalTo(StringUtils.EMPTY))));
        }
    }

    private String getResourceContents(final String resourceName) throws IOException {
        final URL preLaunchPageResource = this.getClass().getResource(resourceName);
        final File preLaunchPageFile = FileUtils.toFile(preLaunchPageResource);
        return FileUtils.readFileToString(preLaunchPageFile);
    }
}
