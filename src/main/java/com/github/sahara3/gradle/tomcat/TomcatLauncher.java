package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class TomcatLauncher {

    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        int port = Integer.parseInt(args[0]);
        String base = args[1];
        String appBase = args[2];

        TomcatLauncher launcher = new TomcatLauncher(port, base, appBase);
        launcher.start();

        while (System.in.read() >= 0) {
            // wait for EOF. (Ctrl+D, etc.)
        }
        launcher.stop();
    }

    private int port;

    private String baseDir;

    private String appBaseDir;

    private Tomcat tomcat;

    TomcatLauncher(int port, String baseDir, String appBaseDir) {
        this.port = port;
        this.baseDir = baseDir;
        this.appBaseDir = appBaseDir;
    }

    synchronized void start() throws IOException, ServletException, LifecycleException {
        if (this.tomcat != null) {
            throw new IllegalStateException("Tomcat is already started.");
        }

        // initialize Tomcat.
        this.tomcat = new Tomcat();
        this.tomcat.setPort(this.port);
        this.tomcat.setBaseDir(this.baseDir);
        // Allow to use a context file inside the META-INF folder
        this.tomcat.enableNaming();

        this.configureServer(this.tomcat.getServer());
        this.configureHost((StandardHost) this.tomcat.getHost());
        this.registerWebapps(this.tomcat.getHost());

        // add JVM shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.stop();
            } catch (LifecycleException e) {
                throw new RuntimeException(e);
            }
        }));

        // trigger creating the default connectors.
        // https://stackoverflow.com/questions/48998387/
        this.tomcat.getConnector();

        // start.
        this.tomcat.start();
    }

    private void configureServer(Server server) {
        server.addLifecycleListener(new VersionLoggerListener());
    }

    private void configureHost(StandardHost host) {
        host.setDeployOnStartup(false);
        host.setAutoDeploy(false);
        host.setUnpackWARs(false);
        host.setAppBase(this.appBaseDir);
    }

    private void registerWebapps(Host host) throws IOException, ServletException {
        Path appBase = Paths.get(host.getAppBase());
        List<File> appDirs = Files.list(appBase).filter(entry -> entry.toFile().isDirectory()).map(Path::toFile)
                .collect(Collectors.toList());
        for (File appDir : appDirs) {
            String name = appDir.getName();
            String contextPath = "ROOT".equals(name) ? "" : "/" + name;
            this.tomcat.addWebapp(host, contextPath, appDir.getAbsolutePath());
        }
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
