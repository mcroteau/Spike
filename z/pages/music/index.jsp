<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld" %>

<h1>Top Music</h1>

<p>New and upcoming artists.</p>

<style>
    tr:hover{
        background:#f8f8f8;
        cursor: pointer;
        cursor: hand;
    }
</style>

<c:if test="${songs.size() > 0}">
    <table>
        <tr>
            <th></th>
            <th>Title</th>
            <th>Artist</th>
            <th>Genre</th>
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
                <td class="center play">
                    <a href="${pageContext.request.contextPath}/music/genre/${song.genre.funk}" class="href-dotted">${song.genre.name}</a>
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
    <p class="yellow">Be the first... are you an artist?</p>
</c:if>

<script>
    $(function(){

        let audio = new Audio();
        let id = {};

        if($obj.hasClass('.like')){

            let likedId = $obj.attr('data-id');
            let $likes = $obj.find('.likes');

            let url = '${pageContext.request.contextPath}/music/like/' + likedId;
            http(url, 'post').then(function(data){
                $likes.html(data.likes)
            });
        }

        $('.play').click(function(){
            let $obj = $(this);

            if(!$obj.hasClass('download')){

                audio.pause();

                let $row = $obj.parent('.data');
                let $img = $row.find('img.play')
                let $plays = $row.find('.plays');

                if(id != $row.attr('data-id')){
                    id = $row.attr('data-id');
                    audio.src = $row.attr('data-url');
                    $img.attr('src', '/assets/media/pause.png')
                    audio.play();

                    let url = '${pageContext.request.contextPath}/music/play/' + id;
                    http(url, 'post').then(function(data){
                        $plays.html(data.plays);
                    })

                }else{
                    id = {}
                    $img.attr('src', '/assets/media/play.png')
                }
            }

        })


        function http(url, method){
            return $.ajax({
                method: method,
                url: url,
                error: function(){
                    console.log("error");
                }
            })
        }
    })
</script>
