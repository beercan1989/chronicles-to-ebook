package com.jbacon.cte.models;

import java.net.URL;

public class ChroniclePage {
    private URL pageUrl;
    private String pageContent;
    private String processedPageContent;

    public ChroniclePage(final URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public URL getPageUrl() {
        return pageUrl;
    }

    public String getPageContent() {
        return pageContent;
    }

    public String getProcessedPageContent() {
        return processedPageContent;
    }

    public ChroniclePage setPageUrl(final URL pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public ChroniclePage setPageContent(final String pageContent) {
        this.pageContent = pageContent;
        return this;
    }

    public ChroniclePage setProcessedPageContent(final String processedPageContent) {
        this.processedPageContent = processedPageContent;
        return this;
    }
}
