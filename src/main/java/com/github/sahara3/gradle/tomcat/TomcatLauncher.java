package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;

public class TomcatLauncher {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String base = args[1];

        TomcatLauncher launcher = new TomcatLauncher(port, base);

        // add webapps.
        if ((args.length - 2) % 2 != 0) {
            throw new IllegalArgumentException("arguments must be a list of (war, contextPath).");
        }
        for (int n = 1; n < args.length / 2; n++) {
            String warPath = args[n];
            String contextPath = args[n + 1];
            launcher.addWebapp(contextPath, warPath);
        }

        // start and wait for exit request.
        launcher.start();
        while (System.in.read() >= 0) {
            // wait.
        }

        // exit.
        launcher.stop();
    }

    private int port;

    private String baseDir;

    private List<WebAppConfiguration> webapps;

    private Tomcat tomcat;

    TomcatLauncher(int port, String baseDir) {
        this.port = port;
        this.baseDir = baseDir;
        this.webapps = new ArrayList<>();
    }

    void addWebapp(String contextPath, String warFilePath) throws ServletException {
        WebAppConfiguration webapp = new WebAppConfiguration(new File(warFilePath));
        webapp.setContextPath(contextPath);
        this.webapps.add(webapp);
    }

    synchronized void start() throws ServletException, LifecycleException {
        if (this.tomcat != null) {
            throw new IllegalStateException("Tomcat is already started.");
        }

        // initialize Tomcat.
        this.tomcat = new Tomcat();
        this.tomcat.setPort(this.port);
        this.tomcat.setBaseDir(this.baseDir);
        for (WebAppConfiguration webapp : this.webapps) {
            this.tomcat.addWebapp(webapp.getContextPath(), webapp.getWarFile().getAbsolutePath());
        }

        this.tomcat.getServer().addLifecycleListener(new VersionLoggerListener());

        // add JVM shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.stop();
            } catch (LifecycleException e) {
                throw new RuntimeException(e);
            }
        }));

        // start.
        this.tomcat.start();
    }

    synchronized void stop() throws LifecycleException {
        if (this.tomcat != null) {
            this.tomcat.stop();
            this.tomcat.getServer().await();
            this.tomcat.destroy();
            this.tomcat = null;
        }
    }
}
