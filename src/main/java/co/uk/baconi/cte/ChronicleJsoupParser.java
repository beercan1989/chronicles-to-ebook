package co.uk.baconi.cte;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import co.uk.baconi.cte.models.ChroniclePage;

public final class ChronicleJsoupParser {
    private ChronicleJsoupParser() {
    }

    // http://jsoup.org/cookbook/extracting-data/dom-navigation
    // http://jsoup.org/cookbook/extracting-data/selector-syntax
    // http://jsoup.org/cookbook/extracting-data/attributes-text-html

    public static void parseChroniclePage(final ChroniclePage page, final int chronicleIndex) {
        try {
            final Document doc = Jsoup.connect(page.getPageUrl().toString()).get();

            final String chronicleTitle = doc.select("h1.header").first().text();
            final Element chronicleBody = doc.select("div#bodyContent").first();

        } catch (final Throwable t) {
            t.printStackTrace();
            return;
        }
    }

    public static void downloadChronicleImage(final ChroniclePage page, final int index, final File imageOutputFolder) {
    }

    public static void getChronicleImageUrl(final ChroniclePage page, final URL baseUrl) throws MalformedURLException {
    }

    public static Entry<ChroniclePage, String> readChronicleUrlsFromGroupPage(final String groupingName,
            final ChroniclePage groupPage, final URL baseUrl) {
        return null;
    }

    private static String formatIndex(final int index) {
        return String.format("%03d", index);
    }
}
