<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>The Best Task Manager</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.0/themes/eggplant/jquery-ui.css" />

<c:url value="/resources/taskstyle.css" var="cssUrl" />
<link rel="stylesheet" href="${cssUrl}" />

<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.8.3.js"></script>

<script type="text/javascript"
	src="http://code.jquery.com/ui/1.10.0/jquery-ui.js"></script>

<c:url value="/resources/validate.js" var="jsUrl" />
<script type="text/javascript" src="${jsUrl}"></script>

</head>

<body>

	<div id="header1">
		<b>Welcome</b>
	</div>
	<div id="main">
		<h1 class="taskManager">Task Manager</h1>
		<spring:htmlEscape defaultHtmlEscape="true" />
		<c:url value="errors" var="errorsURL" />
		<form:form action="${addURL}" commandName="task" method="post"
			id="form1">
			<form:errors path="*" cssClass="errorBlock" element="div"></form:errors>
			<table id="table1">

				<tr>
					<th class="col1"><label>Task Name:</label></th>
					<td class="col2"><form:input class="clearme" id="taskName"
							path="taskName" maxlength="30" /> <form:errors path="taskName"
							cssClass="error" method="post" /></td>


				</tr>
				<tr>
					<th class="col1"><label>Assignee:</label></th>
					<td class="col2"><form:input class="clearme" id="assignee"
							path="assignee" maxlength="30" /> <form:errors path="assignee"
							cssClass="error" /></td>
				</tr>
				<tr>
					<th class="col1"><label>Description:</label></th>
					<td class="col2"><form:input class="clearme"
							id="taskDescription" path="description" maxlength="150" /> <form:label
							path="description" cssClass="error">Invalid Inputs for description, containing harmful character sequence</form:label>

					</td>
				</tr>
				<tr>
					<th class="col1"><label>Date:</label></th>
					<td class="col2"><form:input class="clearme" id="datepicker"
							path="date" htmlEscape="true" /> <form:errors id="datepicker"
							path="date" cssClass="error" /></td>
				</tr>
				<tr>
					<th colspan="2" id="add"><input id="submit" type="submit"
						value="ADD" /></th>

				</tr>

			</table>
		</form:form>
	</div>
	<div id="second">
		<h1 id="task">Task List</h1>
		<c:if test="${!empty taskList}">
			<table id="table2">
				<tr>
					<th>Task Name</th>
					<th>Assignee</th>
					<th>Description</th>
					<th>Dates</th>
					<th>&nbsp;</th>
				</tr>
				<tbody>
					<c:forEach items="${taskList}" var="list">
						<tr>

							<td>${list.taskName}</td>
							<td>${list.assignee}</td>
							<td>${list.description}</td>
							<td><fmt:formatDate pattern="MMM-dd-yyyy"
									value="${list.date}" /></td>
							<td><a href="delete/${list.taskID}">delete</a></td>

						</tr>
					</c:forEach>

				</tbody>
			</table>
		</c:if>
	</div>
</body>
</html>