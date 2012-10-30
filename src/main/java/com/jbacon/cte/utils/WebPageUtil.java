package com.jbacon.cte.utils;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public final class WebPageUtil {
    private WebPageUtil() {
    }

    public static String getWebPageContent(final URL webPageUrl) {
        try {
            return IOUtils.toString(webPageUrl);
        } catch (final IOException e) {
            return null;
        }
    }
}
