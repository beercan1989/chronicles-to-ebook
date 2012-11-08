package com.jbacon.cte.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public final class ResourceUtil {
    private ResourceUtil() {
    }

    public static String getString(final String resourceName) {
        try {
            return FileUtils.readFileToString(getFile(resourceName));
        } catch (final IOException e) {
            return null;
        }
    }

    public static File getFile(final String resourceName) {
        final URL preLaunchPageResource = ResourceUtil.class.getResource(resourceName);
        return FileUtils.toFile(preLaunchPageResource);
    }
}
