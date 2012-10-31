package com.jbacon.cte.models;

import java.io.File;
import java.net.URL;

public class ChroniclePage {
    private URL pageUrl;
    private URL pageImageUrl;
    private File pageImage;

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

    public URL getPageImageUrl() {
        return pageImageUrl;
    }

    public File getPageImage() {
        return pageImage;
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

    public ChroniclePage setPageImageUrl(final URL pageImageUrl) {
        this.pageImageUrl = pageImageUrl;
        return this;
    }

    public ChroniclePage setPageImage(final File pageImage) {
        this.pageImage = pageImage;
        return this;
    }
}
