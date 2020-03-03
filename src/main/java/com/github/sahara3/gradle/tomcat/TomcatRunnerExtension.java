package com.github.sahara3.gradle.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;

import lombok.Getter;
import lombok.Setter;

public class TomcatRunnerExtension {

    public TomcatRunnerExtension() {
        this.initDefaultJarsToSkip();
    }

    @Getter
    @Setter
    private double version = 9.0;

    @Getter
    @Setter
    private int port = 8080;

    @Getter
    private File baseDirectory;

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = new File(baseDirectory);
    }

    @Deprecated
    public void setBaseDir(String baseDir) {
        this.setBaseDirectory(baseDir);
    }

    @Getter
    private List<String> jarsToSkip;

    private void initDefaultJarsToSkip() {
        this.jarsToSkip = new ArrayList<>();

        // from conf/catalina.properties.
        this.jarsToSkip.addAll(Arrays.asList("annotations-api.jar", "ant-junit*.jar", "ant-launcher.jar", "ant.jar",
                "asm-*.jar", "aspectj*.jar", "bootstrap.jar", "catalina-ant.jar", "catalina-ha.jar", "catalina-ssi.jar",
                "catalina-storeconfig.jar", "catalina-tribes.jar", "catalina.jar", "cglib-*.jar", "cobertura-*.jar",
                "commons-beanutils*.jar", "commons-codec*.jar", "commons-collections*.jar", "commons-daemon.jar",
                "commons-dbcp*.jar", "commons-digester*.jar", "commons-fileupload*.jar", "commons-httpclient*.jar",
                "commons-io*.jar", "commons-lang*.jar", "commons-logging*.jar", "commons-math*.jar",
                "commons-pool*.jar", "dom4j-*.jar", "easymock-*.jar", "ecj-*.jar", "el-api.jar",
                "geronimo-spec-jaxrpc*.jar", "h2*.jar", "hamcrest-*.jar", "hibernate*.jar", "httpclient*.jar",
                "icu4j-*.jar", "jasper-el.jar", "jasper.jar", "jaspic-api.jar", "jaxb-*.jar", "jaxen-*.jar",
                "jdom-*.jar", "jetty-*.jar", "jmx-tools.jar", "jmx.jar", "jsp-api.jar", "jstl.jar", "jta*.jar",
                "junit-*.jar", "junit.jar", "log4j*.jar", "mail*.jar", "objenesis-*.jar", "oraclepki.jar", "oro-*.jar",
                "servlet-api-*.jar", "servlet-api.jar", "slf4j*.jar", "taglibs-standard-spec-*.jar", "tagsoup-*.jar",
                "tomcat-api.jar", "tomcat-coyote.jar", "tomcat-dbcp.jar", "tomcat-i18n-*.jar", "tomcat-jdbc.jar",
                "tomcat-jni.jar", "tomcat-juli-adapters.jar", "tomcat-juli.jar", "tomcat-util-scan.jar",
                "tomcat-util.jar", "tomcat-websocket.jar", "tools.jar", "websocket-api.jar", "wsdl4j*.jar",
                "xercesImpl.jar", "xml-apis.jar", "xmlParserAPIs-*.jar", "xmlParserAPIs.jar", "xom-*.jar"));

        // exclude tomcat-embed-*, etc. that are added by this plugin.
        this.jarsToSkip.addAll(Arrays.asList("tomcat-embed-*.jar", "tomcat-annotations-api-*.jar", "jul-to-slf4j-*.jar",
                "logback-core-*.jar", "logback-classic-*.jar", "gradle-tomcat-runner-plugin-*.jar"));
    }

    public void jarsToSkip(String... jarsToSkip) {
        this.jarsToSkip.addAll(Arrays.asList(jarsToSkip));
    }

    @Getter
    private final List<WebAppConfiguration> webapps = new ArrayList<>();

    public void webapp(File warFile, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warFile);
        if (action != null) {
            action.execute(conf);
        }
        this.webapps.add(conf);
    }

    public void webapp(File warFile) {
        this.webapp(warFile, null);
    }

    public void webapp(String warFilePath, Action<WebAppConfiguration> action) {
        this.webapp(new File(warFilePath), action);
    }

    public void webapp(String warFilePath) {
        this.webapp(new File(warFilePath), null);
    }

    public void webapp(Project warProject, Action<WebAppConfiguration> action) {
        WebAppConfiguration conf = new WebAppConfiguration(warProject);
        if (action != null) {
            action.execute(conf);
        }
        this.webapps.add(conf);
    }

    public void webapp(Project warProject) {
        this.webapp(warProject, null);
    }

    @Getter
    private final Map<String, Object> systemProperties = new HashMap<>();

    public void systemProperty(String name, Object value) {
        this.systemProperties.put(name, value);
    }

}
