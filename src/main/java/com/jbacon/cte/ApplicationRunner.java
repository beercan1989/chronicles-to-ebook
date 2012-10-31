package com.jbacon.cte;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jbacon.annotations.VisibleForTesting;
import com.jbacon.cte.models.ChroniclePage;
import com.jbacon.cte.utils.WebPageUtil;

/**
 * @author JBacon
 */
public final class ApplicationRunner {

    public static final void main(final String[] programParams) throws MalformedURLException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        final ApplicationRunner applicationRunner = new ApplicationRunner();
        applicationRunner.readChronicleGroupingPages();
        applicationRunner.findChronicleUrls();
        applicationRunner.readChroniclePages();
        applicationRunner.processChroniclePages();
        applicationRunner.createEbook();
    }

    @VisibleForTesting
    final Map<String, ChroniclePage> chronicleGroupings;

    @VisibleForTesting
    final Map<String, List<ChroniclePage>> chroniclePages;

    public ApplicationRunner() throws MalformedURLException {
        chronicleGroupings = new HashMap<String, ChroniclePage>();
        chroniclePages = new HashMap<String, List<ChroniclePage>>();

        setupChronicleGroupings("PRE-LAUNCH", "http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)");
        setupChronicleGroupings("2003", "http://wiki.eveonline.com/en/wiki/2003_(chronicles)");
        setupChronicleGroupings("2004", "http://wiki.eveonline.com/en/wiki/2004_(chronicles)");
        setupChronicleGroupings("2005", "http://wiki.eveonline.com/en/wiki/2005_(chronicles)");
        setupChronicleGroupings("2006", "http://wiki.eveonline.com/en/wiki/2006_(chronicles)");
        setupChronicleGroupings("2007", "http://wiki.eveonline.com/en/wiki/2007_(chronicles)");
        setupChronicleGroupings("2008", "http://wiki.eveonline.com/en/wiki/2008_(chronicles)");
        setupChronicleGroupings("2009", "http://wiki.eveonline.com/en/wiki/2009_(chronicles)");
        setupChronicleGroupings("2010", "http://wiki.eveonline.com/en/wiki/2010_(chronicles)");
        setupChronicleGroupings("2011", "http://wiki.eveonline.com/en/wiki/2011_(chronicles)");
        setupChronicleGroupings("2012", "http://wiki.eveonline.com/en/wiki/2012_(chronicles)");
    }

    @VisibleForTesting
    void readChronicleGroupingPages() {
        for (final ChroniclePage page : chronicleGroupings.values()) {
            readPageContent(page);
        }
    }

    @VisibleForTesting
    void findChronicleUrls() {
        for (final ChroniclePage page : chronicleGroupings.values()) {

        }
    }

    @VisibleForTesting
    void readChroniclePages() {
        for (final String grouping : chronicleGroupings.keySet()) {
            if (chroniclePages.containsKey(grouping)) {
                for (final ChroniclePage page : chroniclePages.get(grouping)) {
                    readPageContent(page);
                }
            }
        }
    }

    @VisibleForTesting
    void processChroniclePages() {
        // TODO Auto-generated method stub
    }

    @VisibleForTesting
    void createEbook() {
        // TODO Auto-generated method stub
    }

    @VisibleForTesting
    void setupChronicleGroupings(final String groupingName, final String groupingUrl) throws MalformedURLException {
        chronicleGroupings.put(groupingName, new ChroniclePage(new URL(groupingUrl)));
        chroniclePages.put(groupingName, new ArrayList<ChroniclePage>());
    }

    @VisibleForTesting
    void readPageContent(final ChroniclePage page) {
        if (page == null) {
            return;
        }

        final URL pageUrl = page.getPageUrl();
        if (pageUrl != null) {
            final String webPageContent = WebPageUtil.getWebPageContent(pageUrl);
            page.setPageContent(webPageContent);
        }
    }
}
