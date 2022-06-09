//点击发布触发点击事件，通过ajax发送异步请求
$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 发送AJAX请求之前,将CSRF令牌设置到请求的消息头中.
   // var token = $("meta[name='_csrf']").attr("content");
   // var header = $("meta[name='_csrf_header']").attr("content");
   // $(document).ajaxSend(function(e, xhr, options){
   //     xhr.setRequestHeader(header, token);
   // });

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		//url:
		CONTEXT_PATH + "/discuss/add",
		//data:
		"title=" + title + "&content=" + content,
		// {"title" : title, "content" : content},
		//success:
		function (data) {

			//将服务端返回的JSON格式的字符串转为js对象
			data = $.parseJSON(data);
			// alert(typeof(data));

			//在提示框内显示提示信息
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");

			//2s后提示框消失
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//当更新成功后，刷新页面
				if(data.code == 200){
					window.location.reload();
				}
			}, 2000);
		}
	)




}