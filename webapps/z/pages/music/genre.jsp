<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>

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

<c:if test="${songs.size() > 0}">
    <table>
    <tr>
        <th></th>
        <th>Title</th>
        <th>Artist</th>
        <th>Duration<br/>(Seconds)</th>
        <th>Plays</th>
        <th>Likes</th>
    </tr>
    <c:forEach items="${songs}" var="song">
        <tr data-url="${song.url}" class="data" data-id="${song.id}">
            <td class="play"><img src="/assets/media/play.png" style="width:15px;" class="play"/></td>
            <td class="play">${song.title}</td>
            <td class="center play">
                <a href="${pageContext.request.contextPath}/artist/${song.artist.id}" class="href-dotted">${song.artist.name}</a>
            </td>
            <td class="center play">${song.duration}</td>
            <td class="center play plays">${song.plays}</td>
            <td class="center play"><span class="likes">${song.likes}</span>
                <parakeet:isAuthenticated>
                    <c:if test="${!song.liked}">
                        <img src="${pageContext.request.contextPath}/assets/media/like.png" style="width:30px;" class="like"/>
                    </c:if>
                </parakeet:isAuthenticated>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/music/download/${song.id}" class="download">&#8623;</a>
            </td>
        </tr>
    </c:forEach>
    </table>
</c:if>
<c:if test="${songs.size() == 0}">
    <p>Sorry, no songs for this Genre yet...</p>
</c:if>

