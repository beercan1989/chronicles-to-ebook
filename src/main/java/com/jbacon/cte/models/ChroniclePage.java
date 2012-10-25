package com.jbacon.cte.models;

import java.net.URL;

public class ChroniclePage {
    private final URL pageUrl;
    private final String grouping;

    public ChroniclePage(final URL pageUrl, final String grouping) {
        this.pageUrl = pageUrl;
        this.grouping = grouping;
    }

    public URL getPageUrl() {
        return pageUrl;
    }

    public String getGrouping() {
        return grouping;
    }
}
