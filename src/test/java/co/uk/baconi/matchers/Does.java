package co.uk.baconi.matchers;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

public class Does {

    public static <T> Matcher<T> does(final Matcher<T> matcher) {
        return new Is<T>(matcher);
    }

    public static <T> Matcher<T> does(final T value) {
        return does(equalTo(value));
    }

    public static Matcher<Object> does(final Class<?> type) {
        return does(instanceOf(type));
    }
}