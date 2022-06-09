$(function () {
    $("#uploadForm").submit(upload);
});

function upload() {
    // alert("表单提交");
    $.ajax({
        url: "https://upload.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function (data) {
            if(data && data.code == 200){
                alert("上传成功");
                //更新头像路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName": $("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if(data.code == 200){
                            location.href = CONTEXT_PATH + "/index";
                        }else{
                            alert(data.msg);
                        }
                    }
                );
            } else{
                alert("上传文件失败！")
            }

        }
    });
    //让页面的表单提交失效
    return false;

}