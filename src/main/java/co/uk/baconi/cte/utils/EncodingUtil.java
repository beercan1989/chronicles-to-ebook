package co.uk.baconi.cte.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import co.uk.baconi.annotations.VisibleForTesting;

public final class EncodingUtil {

    private static final Map<String, String> replacementMap = new HashMap<String, String>();
    static {
        final String singleQuote = "'";
        replacementMap.put("\u2019", singleQuote);
        replacementMap.put("\u2018", singleQuote);
        replacementMap.put("\u201B", singleQuote);
        replacementMap.put("\u2039", singleQuote);
        replacementMap.put("\u203A", singleQuote);

        final String doubleQuote = "\"";
        replacementMap.put("\u201C", doubleQuote);
        replacementMap.put("\u201F", doubleQuote);
        replacementMap.put("\u201D", doubleQuote);
        replacementMap.put("\u00AB", doubleQuote);
        replacementMap.put("\u00BB", doubleQuote);

        final String hyphen = "-";
        replacementMap.put("\u2010", hyphen);
        replacementMap.put("\u2011", hyphen);
        replacementMap.put("\u2012", hyphen);
        replacementMap.put("\u2013", hyphen);
        replacementMap.put("\u2014", hyphen);
        replacementMap.put("\u2015", hyphen);
        replacementMap.put("\u30A0", hyphen);
        replacementMap.put("\uFE58", hyphen);
        replacementMap.put("\uFE63", hyphen);
        replacementMap.put("\uFF0D", hyphen);

        final String trippleDot = "...";
        replacementMap.put("\u2026", trippleDot);
        replacementMap.put("\u22EF", trippleDot);
        replacementMap.put("\u22EF", trippleDot);
    }

    private EncodingUtil() {
    }

    public static String convertToKindleSafe(final String string) {
        String result = string;
        for (final Entry<String, String> entry : replacementMap.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @VisibleForTesting
    static String convert(final String string, final Encoding sourceEncoding, final Encoding targetEncoding) {
        final ByteBuffer inputBuffer = ByteBuffer.wrap(string.getBytes(sourceEncoding.getCharset()));
        final CharBuffer data = sourceEncoding.getCharset().decode(inputBuffer);
        final ByteBuffer outputBuffer = targetEncoding.getCharset().encode(data);
        return new String(outputBuffer.array());
    }

    public static enum Encoding {
        ISO_8859_1("ISO-8859-1"), //
        UTF8("UTF-8");

        private final String name;
        private final Charset charset;

        private Encoding(final String name) {
            this.name = name;
            charset = Charset.forName(name);
        }

        public String getName() {
            return name;
        }

        public Charset getCharset() {
            return charset;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}