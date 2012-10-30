package com.jbacon.cte.models;

import java.net.URL;

public class ChroniclePage {
    private URL pageUrl;
    private String pageContent;

    public ChroniclePage() {
    }

    public ChroniclePage(final URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public ChroniclePage(final URL pageUrl, final String pageContent) {
        this.pageUrl = pageUrl;
        this.setPageContent(pageContent);
    }

    public URL getPageUrl() {
        return pageUrl;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageUrl(final URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setPageContent(final String pageContent) {
        this.pageContent = pageContent;
    }
}
