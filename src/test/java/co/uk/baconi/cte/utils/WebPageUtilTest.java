package co.uk.baconi.cte.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import co.uk.baconi.cte.utils.WebPageUtil;

public class WebPageUtilTest {

    public static final String SUCCESSFUL_PAGE_CONTENT = "<title>Pre-Launch (chronicles) - EVElopedia</title>";
    public static final String SUCCESSFUL_TEST_URL = "http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)";

    public static final String FAIL_TEST_URL = "http://localhost:9999";

    @Test
    public void shouldSuccessfullyGetPage() throws IOException {
        final String webPageContent = WebPageUtil.getWebPageContent(new URL(SUCCESSFUL_TEST_URL));

        assertThat(webPageContent, is(not(nullValue())));
        assertThat(webPageContent, containsString(SUCCESSFUL_PAGE_CONTENT));
    }

    public void shouldFailToGetPage() throws MalformedURLException {
        final String webPageContent = WebPageUtil.getWebPageContent(new URL(FAIL_TEST_URL));

        assertThat(webPageContent, is(nullValue()));
    }

}
