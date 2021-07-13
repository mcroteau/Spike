<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Edit Song</h1>

<c:if test="${not empty message}">
    <div class="notify">${message}</div>
</c:if>

<form action="${pageContext.request.contextPath}/songs/update/${song.id}" method="post" enctype="multipart/form-dat">

    <label>Title</label>
    <input type="text" name="title" value="${song.title}">

    <label>Genre</label>
    <select name="genre">
        <c:forEach items="${genres}" var="genre">
            <c:if test="${genre.id == id}">
                <c:set var="selected" value="selected"/>
            </c:if>
            <option value="$genre.id}" ${selected}>${genre.name}</option>
        </c:forEach>
    </select>

    <input type="submit" value="Update" class="button"/>
</form>
