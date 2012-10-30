package com.jbacon.cte;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.jbacon.cte.models.ChroniclePage;

/**
 * @author JBacon
 */
public final class ApplicationRunner {

    public static final void main(final String[] programParams) throws MalformedURLException {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }

        final ApplicationRunner applicationRunner = new ApplicationRunner();
        applicationRunner.findChronicleUrls();
        applicationRunner.readChroniclePages();
        applicationRunner.processReadPage();
        applicationRunner.createEbook();
    }

    private final Map<String, URL> chronicleGroupings = new HashMap<String, URL>();
    private final Map<String, ChroniclePage> chroniclePages = new HashMap<String, ChroniclePage>();

    public ApplicationRunner() throws MalformedURLException {
        chronicleGroupings.put("PRE-LAUNCH", new URL("http://wiki.eveonline.com/en/wiki/Pre-Launch_(chronicles)"));
        chronicleGroupings.put("2003", new URL("http://wiki.eveonline.com/en/wiki/2003_(chronicles)"));
        chronicleGroupings.put("2004", new URL("http://wiki.eveonline.com/en/wiki/2004_(chronicles)"));
        chronicleGroupings.put("2005", new URL("http://wiki.eveonline.com/en/wiki/2005_(chronicles)"));
        chronicleGroupings.put("2006", new URL("http://wiki.eveonline.com/en/wiki/2006_(chronicles)"));
        chronicleGroupings.put("2007", new URL("http://wiki.eveonline.com/en/wiki/2007_(chronicles)"));
        chronicleGroupings.put("2008", new URL("http://wiki.eveonline.com/en/wiki/2008_(chronicles)"));
        chronicleGroupings.put("2009", new URL("http://wiki.eveonline.com/en/wiki/2009_(chronicles)"));
        chronicleGroupings.put("2010", new URL("http://wiki.eveonline.com/en/wiki/2010_(chronicles)"));
        chronicleGroupings.put("2011", new URL("http://wiki.eveonline.com/en/wiki/2011_(chronicles)"));
        chronicleGroupings.put("2012", new URL("http://wiki.eveonline.com/en/wiki/2012_(chronicles)"));
    }

    private void findChronicleUrls() {
        // TODO Auto-generated method stub
    }

    private void readChroniclePages() {
        // TODO Auto-generated method stub
    }

    private void processReadPage() {
        // TODO Auto-generated method stub
    }

    private void createEbook() {
        // TODO Auto-generated method stub
    }
}
