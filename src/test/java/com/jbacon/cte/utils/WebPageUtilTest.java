package com.jbacon.cte.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class WebPageUtilTest {

    @Test
    public void shouldSuccessfullyGetPage() throws IOException {
        final String webPageContent = WebPageUtil.getWebPageContent(new URL(
                "http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)"));

        assertThat(webPageContent, is(not(nullValue())));
        assertThat(webPageContent, containsString("<title>Pre-Launch (chronicles) - EVElopedia</title>"));
    }

    public void shouldFailToGetPage() throws MalformedURLException {
        final String webPageContent = WebPageUtil.getWebPageContent(new URL("http://localhost:9999"));

        assertThat(webPageContent, is(nullValue()));
    }

}
