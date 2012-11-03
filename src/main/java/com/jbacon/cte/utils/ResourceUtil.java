package com.jbacon.cte.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public final class ResourceUtil {
    private ResourceUtil() {
    }

    public static String getResource(final String resourceName) {
        final URL preLaunchPageResource = ResourceUtil.class.getResource(resourceName);
        final File preLaunchPageFile = FileUtils.toFile(preLaunchPageResource);
        try {
            return FileUtils.readFileToString(preLaunchPageFile);
        } catch (final IOException e) {
            return null;
        }
    }
}
