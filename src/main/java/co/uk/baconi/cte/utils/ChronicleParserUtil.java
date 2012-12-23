package co.uk.baconi.cte.utils;

import java.net.URL;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class ChronicleParserUtil {
    private ChronicleParserUtil() {
    }

    /**
     * Appends all the given children to the parent Element.
     */
    public static Element appendChildren(final Element parent, final Elements children) {
        for (final Element paragraph : children) {
            parent.appendChild(paragraph);
        }
        return parent;
    }

    /**
     * Pads the index with zero's to three characters.
     */
    public static String padded(final int index) {
        return String.format("%03d", index);
    }

    /**
     * Removes all the HTML tags inside of each element.
     */
    public static Elements cleanInnerHtml(final Elements elements) {
        for (final Element element : elements) {
            element.text(element.text());
        }
        return elements;
    }

    /**
     * Finds the name from the URL for an image.
     */
    public static String getImageFileName(final URL imageUrl) {
        return last(imageUrl.toString().split("/"));
    }

    /**
     * Get the last element in the array
     */
    public static <T> T last(final T[] array) {
        return (array == null || array.length < 0) ? null : array[array.length - 1];
    }
}
