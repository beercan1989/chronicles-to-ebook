package co.uk.baconi.cte;

import static co.uk.baconi.matchers.Does.does;
import static co.uk.baconi.matchers.FileMatchers.exists;
import static co.uk.baconi.matchers.FileMatchers.isDirectory;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractChronicleTest {

    protected static final File TEST_OUTPUT_FOLDER = new File("test-output-folder");
    protected static final String TEST_CHRONICLE_PAGE_FEDO = "/fedo-chronicle-page.html";
    protected static final String TEST_CHRONICLE_PAGE_MINDCLASH = "/mind-clash-chronicle.html";
    protected static final String EMPTY = "";

    @Before
    public void before() {
        TEST_OUTPUT_FOLDER.mkdir();

        assertThat(TEST_OUTPUT_FOLDER, does(exists()));
        assertThat(TEST_OUTPUT_FOLDER, isDirectory());
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(TEST_OUTPUT_FOLDER);

        assertThat(TEST_OUTPUT_FOLDER, does(not(exists())));
    }
}
