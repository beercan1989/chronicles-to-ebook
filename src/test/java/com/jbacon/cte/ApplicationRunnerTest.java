package com.jbacon.cte;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.jbacon.cte.models.ChroniclePage;
import com.jbacon.cte.utils.WebPageUtilTest;

public class ApplicationRunnerTest {

    @Test
    @Ignore
    public void testReadChronicleGroupingPages() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testFindChronicleUrls() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testReadChroniclePages() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testProcessChroniclePages() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testCreateEbook() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testSetupChronicleGroupings() {
        fail("Not yet implemented");
    }

    @Test
    public void testReadPageContent() throws MalformedURLException {
        final ChroniclePage page = new ChroniclePage(new URL(WebPageUtilTest.SUCCESSFUL_TEST_URL));

        assertThat(page.getPageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(nullValue()));

        new ApplicationRunner().readPageContent(page);

        assertThat(page.getPageUrl(), is(not(nullValue())));
        assertThat(page.getPageContent(), is(not(nullValue())));
        assertThat(page.getPageContent(), containsString(WebPageUtilTest.SUCCESSFUL_PAGE_CONTENT));
    }

    @Test
    public void testRegex() throws IOException {
        final URL preLaunchPageResource = this.getClass().getResource("/pre-launch-page.html");
        final File preLaunchPageFile = FileUtils.toFile(preLaunchPageResource);
        final String preLaunchPageContents = FileUtils.readFileToString(preLaunchPageFile);

        assertThat(preLaunchPageContents, is(not(nullValue())));

        final Pattern pattern = Pattern
                .compile(
                        "<h2>\\s*<span\\s*class=\"mw-headline\"\\s*id=\"Chronological\">\\s*Chronological\\s*</span></h2>.<ul>(?:<li>(<a href=\"[^\"]*\" title=\"[^\"]*\">[^\"]*</a>).</li>)*</ul>",
                        Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(preLaunchPageContents);

        assertThat("Page contains required content.", matcher.find());

        // Cuts down the HTML to just the Table Of Contents for the Chronological order of Chronicles.
        System.out.println(matcher.group());
    }
}
