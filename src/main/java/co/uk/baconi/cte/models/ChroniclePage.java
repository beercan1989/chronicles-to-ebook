package co.uk.baconi.cte.models;

import java.io.File;
import java.net.URL;

import org.jsoup.nodes.Element;

public class ChroniclePage {
    private URL pageUrl;
    private URL pageImageUrl;
    private File pageImage;

    private String pageTitle;
    private String pageContent;
    private Element processedPageContent;

    public ChroniclePage(final URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public URL getPageUrl() {
        return pageUrl;
    }

    public String getPageContent() {
        return pageContent;
    }

    public Element getProcessedPageContent() {
        return processedPageContent;
    }

    public URL getPageImageUrl() {
        return pageImageUrl;
    }

    public File getPageImage() {
        return pageImage;
    }

    public void setPageUrl(final URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setPageContent(final String pageContent) {
        this.pageContent = pageContent;

    }

    public void setProcessedPageContent(final Element processedPageContent) {
        this.processedPageContent = processedPageContent;
    }

    public void setPageImageUrl(final URL pageImageUrl) {
        this.pageImageUrl = pageImageUrl;
    }

    public void setPageImage(final File pageImage) {
        this.pageImage = pageImage;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(final String pageTitle) {
        this.pageTitle = pageTitle;
    }
}
