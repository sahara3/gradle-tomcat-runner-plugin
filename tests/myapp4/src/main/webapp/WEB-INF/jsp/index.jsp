<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Hello, World!</title>
  <style>
    table,
    th,
    td {
      border-style: solid;
      border-width: 1px;
    }

  </style>
</head>
<body>
  <h1>Hello from myapp4</h1>
  <p>Context path: [
    <c:out value="${request.contextPath}" />].</p>

  <h2>System Properties</h2>
  <table>
    <tr>
      <th>Key</th>
      <th>Value</th>
    </tr>
    <c:forEach var="entry" items="${systemProperties}">
      <tr>
        <td>
          <c:out value="${entry.key}" />
        </td>
        <td>
          <c:out value="${entry.value}" />
        </td>
      </tr>
    </c:forEach>
  </table>
</body>
</html>
