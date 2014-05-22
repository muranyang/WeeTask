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

<!-- stylesheet for jquery "datepicker" epplant theme   -->
<link rel="stylesheet"
    href="http://code.jquery.com/ui/1.10.0/themes/eggplant/jquery-ui.css" />

<!-- stylesheet for jquery  "tooltip"
<link rel="stylesheet"
    href="http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css" />
-->
<!-- stylesheet for the webpage -->
<c:url value="/resources/taskstyle.css" var="cssUrl" />
<link rel="stylesheet" href="${cssUrl}" />

<!-- scripts for date picker 
<script type="text/javascript"
    src="http://code.jquery.com/jquery-1.8.3.js"></script>
<script type="text/javascript"
    src="http://code.jquery.com/ui/1.10.0/jquery-ui.js"></script>
-->
<!-- jquery -->
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.1/jquery-ui.js"></script>



<c:url value="/resources/validate.js" var="jsUrl" />
<script type="text/javascript" src="${jsUrl}"></script>



</head>

<body>

    <%-- <div id="header1">
        <b>Welcome</b>
    </div>
    <div id="main">
        <h1 class="taskManager">Task Manager</h1>
        <spring:htmlEscape defaultHtmlEscape="true" />
        <c:url value="/update/$(taskID)" var="update" />
        <form:form action="${update}" commandName="task" method="post"
            id="form1">
            <form:errors path="*" cssClass="errorBlock" element="div"></form:errors>
          
            <table id="table1">
                <tr>
                    <th class="col1"><label for="taskName">Task Name:</label></th>
                    <td class="col2"><form:input class="txtbox" id="taskName"
                            path="taskName" title ="Must provide Task Name" value=${update.taskName } maxlength="30" /> 
                            <form:errors path="taskName" cssClass="error" method="post" /></td>

                </tr>

                <tr>
                    <th class="col1"><label for="assignee">Assignee:</label></th>
                    <td class="col2"><form:input class="txtbox" id="assignee"
                            path="assignee" title="Must provide Assignee" value=${update.assignee } maxlength="30" /> 
                            <form:errors path="assignee" cssClass="error" /></td>
                </tr>
                <tr>
                    <th class="col1"><label for="taskDescription">Description:</label></th>
                    <td class="col2"><form:input class="txtbox"
                            id="taskDescription" path="description" value="${update.description }" ${update.description }maxlength="150" /> <form:errors
                            id="description" path="description" cssClass="error" /></td>
                </tr>
                <tr>
                    <th class="col1"><label for="datepicker">Start Date:</label></th>
                    <td class="col2"><form:input class="txtbox" id="datepicker"
                            path="date" title="Date in format mm/dd/yyyy" value="${date }" /> <form:errors id="datepicker" path="date"
                            cssClass="error" /></td>
                </tr>
                <tr>
                    <th colspan="2" id="add"><input id="submit" type="submit"
                        value="ADD" /></th>

                </tr>

            </table>

        </form:form>
    </div> --%>
    
   
     <div id="second">
        <h1 id="task">Task List</h1>
       
            <table id="table2">
                <tr class="tlcol">
                    <th>Task Name</th>
                    <th>Assignee</th>
                    <th>Description</th>
                    <th>Dates</th>
                   
                </tr>
                <tbody>
                    
                        <tr class="tlcol">

                            <td><spring:escapeBody>
                            ${update.taskName}
                            </spring:escapeBody></td>
                            <td><spring:escapeBody> 
                            ${update.assignee}
                            </spring:escapeBody></td>
                            <td><spring:escapeBody> 
                            ${update.description}
                            </spring:escapeBody></td>
                            <td><fmt:formatDate pattern="MMM-dd-yyyy"
                                    value="${update.date}" /></td>
                        </tr>
                    

                </tbody>
            </table>
      
    </div>
    
</body>
</html>