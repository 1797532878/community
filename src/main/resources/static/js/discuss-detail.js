//点赞
function like(btn,entityType,entityId,entityUserId,postId){
    $.post(
      CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data){
          data = $.parseJSON(data);
          if (data.code == 0){
              $(btn).children("i").text(data.likeCount);
              $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
          }else{
              alert(data.msg);
          }
        }
    );
}

// $(function(){
//     $("#submit_button_11").click(submit_11);
// });
//
// function submit_11(){
//     var content = $("#content_11").val();
//     var entityType = $("#entityType_11").val();
//     var entityId =$("#entityId_11").val();
//     var post_id = $("#post_id_11").val();
//     var targetId = $("#targetId_11").val();
//     $.post(
//         CONTEXT_PATH + "/comment/add/" + post_id,
//         {"content":content,"entityType":entityType,"entityId":entityId,"targetId":targetId,"post_id":post_id},
//         function (data){
//             data = $.parseJSON(data);
//             if (data.code == 0){
//                 window.location.reload();
//             }
//         }
//     );
// }

function comment(btn,entityType,entityId,post_id,targetId){
    var content = $(btn).parent().prev().children().val();
    $.post(
      CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"targetId":targetId,"post_id":post_id},
        function (data){
          data = $.parseJSON(data);
          if (data.code == 0){
              window.location.reload();
          }
        }
    );
}
