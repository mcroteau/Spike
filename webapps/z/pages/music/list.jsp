<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach items="${songs}" var="song">
    <p>${song.title}</p>
</c:forEach>