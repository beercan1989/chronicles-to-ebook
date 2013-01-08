package co.uk.baconi.cte.parsers;

import static co.uk.baconi.cte.parsers.ChronicleCollectionParser.parseChronicleCollections;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import co.uk.baconi.cte.AbstractChronicleTest;

public class ChronicleCollectionParserTest extends AbstractChronicleTest {

    @Test
    public void shouldbeAbleToParseChronicleCollections() throws IOException {
        final List<URL> chronicleCollections = new ArrayList<URL>();
        chronicleCollections.add(new URL("http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)"));
        chronicleCollections.add(new URL("http://wiki.eveonline.com/en/wiki/2003_(chronicles)"));

        final Set<URL> results = parseChronicleCollections(chronicleCollections, TEST_OUTPUT_FOLDER);

        assertThat(results, is(not(nullValue())));
        assertThat(results, is(not(empty())));
        assertThat(results, hasSize(65 + 8));
        assertThat(results.toArray(new URL[] {})[0].toString(), containsString("Fedo_(Chronicle)"));
    }

}
