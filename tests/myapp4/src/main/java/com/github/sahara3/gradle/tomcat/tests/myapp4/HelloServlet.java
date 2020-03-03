package com.github.sahara3.gradle.tomcat.tests.myapp4;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class HelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Entry<String, String>> systemProperties = System.getProperties().stringPropertyNames().stream().sorted()
                .map(key -> new SimpleEntry<>(key, System.getProperty(key))).collect(Collectors.toList());
        request.setAttribute("systemProperties", systemProperties);
        this.getServletContext().getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
    }
}