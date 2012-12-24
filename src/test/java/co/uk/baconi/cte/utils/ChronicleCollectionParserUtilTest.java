package co.uk.baconi.cte.utils;

import static co.uk.baconi.cte.utils.ChronicleCollectionParserUtil.getChroniclePages;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import co.uk.baconi.cte.AbstractChronicleTest;

public class ChronicleCollectionParserUtilTest extends AbstractChronicleTest {

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

}
