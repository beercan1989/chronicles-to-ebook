package com.jbacon.cte;

/**
 * @author JBacon
 */
public final class ApplicationRunner {

    public ApplicationRunner() {
    }

    public static final void main(final String[] programParams) {
        if (programParams.length != 0) {
            System.err.println("This application does not support / require any parameters.");
        }
    }

}
