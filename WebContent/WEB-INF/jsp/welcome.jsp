<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Spring MVC Tutorial</title>
	<link href="<c:url value="/resources/css/jquery-ui.structure.min.css" />" rel="stylesheet">
	<link href="<c:url value="/resources/css/opusmagus.css" />" rel="stylesheet">
	
    <script src="<c:url value="/resources/js/jquery-3.1.1.min.js" />"></script>
    <script src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>
    <script src="<c:url value="/resources/js/sample.js" />"></script>
</head>
<body>
	<input id="hfAppRoot" type="hidden" value="<%=request.getContextPath()%>">
 	<h1>Spring MVC Starter Project</h1>
	
	<div id="btnGetCustomer" style="cursor:pointer; text-decoration:underline;">Test AJAX post back</div>
</body>
</html>