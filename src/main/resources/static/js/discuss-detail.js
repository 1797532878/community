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


$(function(){
    $("#submit_button1").click(submit1);
});

function submit1(){
    var content = $("#content1").val();
    var entityType = $("#entityType1").val();
    var entityId =$("#entityId1").val();
    var post_id = $("#post_id1").val();
    $.post(
        CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"post_id":post_id},
        function (data){
            data = $.parseJSON(data);
            if (data.code == 0){
                window.location.reload();
            }
        }
    );
}

$(function(){
    $("#submit_button2").click(submit2);
});

function submit2(){
    var content = $("#content2").val();
    var entityType = $("#entityType2").val();
    var entityId =$("#entityId2").val();
    var post_id = $("#post_id2").val();
    $.post(
        CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"post_id":post_id},
        function (data){
            data = $.parseJSON(data);
            if (data.code == 0){
                window.location.reload();
            }
        }
    );
}

$(function(){
    $("#submit_button3").click(submit3);
});

function submit3(){
    var content = $("#content3").val();
    var entityType = $("#entityType3").val();
    var entityId =$("#entityId3").val();
    var post_id = $("#post_id3").val();
    $.post(
        CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"post_id":post_id},
        function (data){
            data = $.parseJSON(data);
            if (data.code == 0){
                window.location.reload();
            }
        }
    );
}

$(function(){
    $("#submit_button4").click(submit4);
});

function submit4(){
    var content = $("#content4").val();
    var entityType = $("#entityType4").val();
    var entityId =$("#entityId4").val();
    var post_id = $("#post_id4").val();
    $.post(
        CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"post_id":post_id},
        function (data){
            data = $.parseJSON(data);
            if (data.code == 0){
                window.location.reload();
            }
        }
    );
}
$(function(){
    $("#submit_button5").click(submit5);
});

function submit5(){
    var content = $("#content5").val();
    var entityType = $("#entityType5").val();
    var entityId =$("#entityId5").val();
    var post_id = $("#post_id5").val();
    $.post(
        CONTEXT_PATH + "/comment/add/" + post_id,
        {"content":content,"entityType":entityType,"entityId":entityId,"post_id":post_id},
        function (data){
            data = $.parseJSON(data);
            if (data.code == 0){
                window.location.reload();
            }
        }
    );
}