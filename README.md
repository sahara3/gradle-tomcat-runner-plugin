Gradle plugin for running Apache Tomcat
=======================================

This is a Gradle plugin that runs web applications on Apache Tomcat.
I am aware that there are plugins that claim to do that, however, not
to my satisfication.

This plugin can run multiple web application on the single Tomcat
instance. That is the main feature of this plugin.

This plugin is designed to use under a multi-project layout.

Usage
-----

```gradle
plugin {
    id 'com.github.sahara3.tomcat-runner' version '0.2.1'
}

tomcat {
    version = 9.0
    port = 8080
    systemProperty 'your.custom.property', 'property-value'

    webapp(project(':myapp1')) {
        contextPath = '' // root context.
    }

    webapp project(':myapp2')

    webapp 'myapp3/build/libs/myapp3.war'

    webapp file('myapp4/build/libs/myapp4.war')
}
```

To add a Web application project as webapp, you should apply War
plugin to the Web application project.
