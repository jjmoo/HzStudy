var url_head;
var cur_minute;
var length;
var minutes;

var getSendProcessUrlHead = function(id, student, course) {
    var str = "http://study.huizhou.gov.cn/play/MmsProgress.ashx?";
    str += "id=" + id;
    str += "&student_id=" + student;
    str += "&course_id=" + course;
    str += "&uidcount=1";
    return str;
};

var sendProcess = function() {
    var time;
    if (60 > cur_minute) {
        time = cur_minute + ":00";
    } else if (120 > cur_minute){
        time = "01:" + (cur_minute - 60) + ":00";
    } else {
        time = "02:" + (cur_minute - 120) + ":00";
    }
    if (cur_minute <= minutes) {

        if (location.hash.indexOf("debug") > -1) {
            var p = document.createElement("p");
            p.innerHTML = url_head + "&timems=" + time + "&length=" + length;
            document.getElementsByTagName("body")[0].appendChild(p);
        }

        document.getElementById("frame_send").contentWindow.location.href = url_head + "&timems=" + time + "&length=" + length;
//                document.getElementById("frame").contentWindow.location.href = "http://www.baidu.com/s?w=" + cur_minute;
//                document.getElementById("frame").contentWindow.open(url_head + "&timems=" + time + "&length=");
//                window.open(url_head + "&timems=" + time + "&length=", last_win);
    } else {
        alert("学习完成！");
        document.getElementById("frame_play").style.display = "none";
        document.getElementById("frame_play").contentWindow.location.href = "http://www.baidu.com";
        document.getElementById("frame_check").style.display = "inline-block";
    }
};

var sendData = function (form) {
    length = form.length.value;
    minutes = form.minutes.value;
    cur_minute = 1;
    sendProcess();
};

var analyzeData = function(form) {
    var re = /right.aspx?.*?id=(.*?)&.*?student_id=(.*?)&.*?course_id=(.*?)&.*$/ig;
    var r = re.exec(form.content.value);
    var id = r[1];
    var student = r[2];
    var course = r[3];
    url_head = getSendProcessUrlHead(id, student, course);

    document.getElementById("frame_play").contentWindow.location.href = "http://study.huizhou.gov.cn/play/" + r[0];
    document.getElementById("frame_play").style.display = "inline-block";
    document.getElementById("frame_check").style.display = "none";
    document.getElementById("form").style.marginTop = "20px";
};

var toTrueAddress = function (form) {
    var relative = form.address.value;
    window.open("http://study.huizhou.gov.cn/play/" + relative);
};

window.onload = function() {
    var iframe = document.getElementById("frame_send");
    iframe.onload = function () {
        cur_minute++;
        //alert(cur_minute);
        sendProcess();
    };
};