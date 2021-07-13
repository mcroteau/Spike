<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Add Song</h1>

<c:if test="${not empty message}">
    <div class="notify">${message}</div>
</c:if>

<form action="${pageContext.request.contextPath}/songs/upload" method="post" enctype="multipart/form-data">

    <label>Title</label>
    <input type="text" name="title" placeholder="Is This Love">

    <lable>Audio Track</lable>
    <input type="file" name="song"/>

    <input type="submit" value="Add Song" class="button"/>
</form>
