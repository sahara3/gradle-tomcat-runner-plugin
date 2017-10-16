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
buildscript {
  repositories {
    maven {
      url 'https://plugins.gradle.org/m2/'
    }
  }
  dependencies {
    classpath 'com.github.sahara3:tomcat-runner-plugin:0.1'
  }
}

apply plugin: 'tomcat-runner-plugin:0.1'

dependencies {
  webapp project(':myapp1')
  webapp project(':myapp2')
}

tomcat {
  port = 8080
  systemProperty 'your.custom.property', 'property-value'
}
```

This plugin adds a configuration named 'webapp'. You can specify Web
application projects in this 'webapp' configuraion dependency.

War plugin should be applied to Web application projects.
