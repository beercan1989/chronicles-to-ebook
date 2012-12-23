package co.uk.baconi.matchers;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

public class Are {

    public static <T> Matcher<T> are(final Matcher<T> matcher) {
        return new Is<T>(matcher);
    }

    public static <T> Matcher<T> are(final T value) {
        return are(equalTo(value));
    }

    public static Matcher<Object> are(final Class<?> type) {
        return are(instanceOf(type));
    }
}