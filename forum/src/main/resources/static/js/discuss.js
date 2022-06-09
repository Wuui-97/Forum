$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
})

//点赞
function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId" : entityId, "entityUserId" : entityUserId, "postId" : postId},
        function (result) {
            result = $.parseJSON(result);
            if(result.code == 200){
                $(btn).children("i").text(result.likeCount);
                $(btn).find("b").text(result.likeStatus == 1 ? "已赞" : "赞");
            }else{
                alert(result.msg);
            }
        }
    )
}

//置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"postId" : $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 200){
                $("#topBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg)
            }
        }
    )
}

//加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"postId" : $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 200){
                $("#wonderfulBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg)
            }
        }
    )
}

//删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"postId" : $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 200){
                location.href = CONTEXT_PATH + "/index";
            }else{
                alert(data.msg)
            }
        }
    )
}