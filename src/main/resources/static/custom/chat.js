const chat_user_list = $('#chat-user-list');
let loginUserId = $('#login-user-id').val();

let toUserId;
const toUserAvatar = $('#to-user-avatar');
const toUserName = $('#to-user-name');


//创建ws对象
let ws = new WebSocket("ws://localhost:8080/wechat");
//绑定ws事件
ws.onopen = function () {
    //建立连接之后的操作
}
//接收到服务端推送的消息后触发
ws.onmessage = function (e) {
    //获取服务端推送的消息
    let dataStr = e.data;
    //将dataStr转为json对象
    let res = JSON.parse(dataStr);

    //判断是否是系统消息
    if (res.system) {
        //系统消息
        //同步在线用户列表
        let message = res.message;
        if (res.status) {
            //用户在线，拼接html
            let str = "";
            for (let user of message) {
                let userId = user.id;
                let username = user.username;
                let userAvatarUrl = user.avatarUrl;
                if (userId != loginUserId) {
                    str += "<li id='" + userId + "' class=\"clearfix\"><div class=\"d-flex\"><img class=\"rounded-circle user-image\" src='" + userAvatarUrl + "' <div class=\"flex-grow-1\">" +
                        "<div class=\"about\"><span class=\"name\" onclick=\"open_chat('" + userId+"','"+username+"','"+userAvatarUrl + "')\">" + username + "</span></div></div></div></li>";
                }
            }
            //添加html
            chat_user_list.html(str);
        } else {
            //下线用户
            for (let id of message) {
                $("#" + id).remove()
                if(toUserId==id){
                    $('.call-chat-body').addClass('fade');
                }
            }
        }
    } else {
        //不是系统消息

        let fromUserId = res.fromUserId;
        let message = res.message;

        let str = "<li><div class=\"message my-message\">"+message+"</div></li>";
        //是否正在与发消息的人聊天
        if(toUserId==fromUserId){
            //是，显示
            $('#chat-note').append(str);
        }
        //存储
        let chatData = sessionStorage.getItem(fromUserId);
        if (chatData != null) {
            chatData+=str;
        } else {
           chatData=str;
        }
        sessionStorage.setItem(fromUserId,chatData);

    }

}
ws.onclose = function () {
    //连接关闭之后的操作
}

$('#send-message').click(function () {
    //获取输入的内容
    let data = $('#message-to-send').val();
    //清空输入框内容
    $('#message-to-send').val("");

    let str = "<li class=\"clearfix\"><div class=\"message other-message\" style='float:right'>" + data + "</div></li>";
    $('#chat-note').append(str);

    //推送消息给服务器
    let json = {"toUserId": toUserId, "message": data}
    ws.send(JSON.stringify(json));

})


/*切换聊天界面*/
function open_chat(toUId,toUname,toAUrl) {

    //设置聊天区用户信息
    toUserAvatar.attr('src',toAUrl);
    toUserName.text(toUname);

    //显示聊天框
    $('.call-chat-body').removeClass('fade');
    toUserId = toUId;

    //获取历史聊天记录
    let chatData = sessionStorage.getItem(toUId);
    if (chatData != null) {
        //将聊天记录渲染到聊天区
        $('#chat-note').html(chatData);
    } else {
        $('#chat-note').html("");
    }
}
