$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName, "content" : content},
		function (result) {

			result = $.parseJSON(result);
			$("#hintBody").text(result.msg);

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
			}, 2000);

			location.reload();
		}
	)


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}