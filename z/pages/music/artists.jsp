<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    img#profile{
        background:#ff68bb;
        padding:7px;
    }
    table{
        width:100%;
    }
    h1.meta{
        font-size: 74px;
    }
</style>

<h1 class="meta">Genre: <span style="font-style: italic">${genre.name}</span></h1>

<h1>Featured : <span style="font-style: italic">${artist.name}</span>
    <span style="font-weight: normal; font-size:12px;">Popular Track : ${loved.title}</span>
</h1>

<c:if test="${artists.size() > 0}">
    <table>
        <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Plays</th>
            <th>Likes</th>
            <th>Downloads</th>
            <th></th>
        </tr>
        <c:forEach items="${artists}" var="artist">
            <tr>
                <td>${artist.id}</td>
                <td>${artist.name}</td>
                <td>${artist.plays}</td>
                <td>${artist.likes}</td>
                <td>${artist.downloads}</td>
                <td></td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<c:if test="${artists.size() == 0}">

</c:if>