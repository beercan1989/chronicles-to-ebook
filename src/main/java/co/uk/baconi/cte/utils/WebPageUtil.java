package co.uk.baconi.cte.utils;

import java.io.IOException;
import java.net.MalformedURLException;
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

    public static URL getUrl(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            return null;
        }
    }
}
