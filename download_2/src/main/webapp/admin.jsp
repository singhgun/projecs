<%@page import="com.bean.KVPair"%>
<%@page import="com.bean.MessageBean"%>
<%@page session="false" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin Page</title>
</head>
<body>
	<p>
		<a href="index.jsp">HOME</a>
	</p>
	<%
	String downPath = (String)request.getAttribute("downloadPath");
	String uploadPath = (String)request.getAttribute("uploadPath");
	%>
	<form method="post" action="admin">
		<p>Enter the path you want to EXPOSE to client: 
		C:/manish/mov<input type="text" name="downPath" value="<%=downPath%>">
		</p>
		<p>
		<p></p>
		<hr color="black" width="80%" size="3">
		<p>
			Enter path where you want the Client to upload the file:
			C:/manish/mov<input type="text" name="upPath" value="<%=uploadPath%>">
		</p>
		<p>
			Password:<input name="password" type="password" />
		</p>
		<input type="submit" name="path" value="Submit" />
		<h3>
			<%
			String msg = (String) request.getAttribute("msg");
				if (msg != null)
					out.println(msg);
			%>
		</h3>
	</form>

</body>
</html>