<%@page pageEncoding="UTF-8"%>
<html>
<head>
<title>Tutorial: Add</title>
<link rel="stylesheet" type="text/css" href="${f:url('/css/sa.css')}" />
</head>
<body>

<h1>Tutorial: Add</h1>

<div id="errors"></div>
<div id="messages"></div>
<s:form>
<html:text property="arg1"/> +
<html:text property="arg2"/>
= <span id="result"></span><br />
<input type="button" name="submit" value="サブミット" id="mysubmit"/>
</s:form>


検証エラーが発生した場合： <br/>
{"errors": {"プロパティ名１": ["エラーメッセージ１","エラーメッセージ１´",...],  <br/>
            "プロパティ名２": ["エラーメッセージ２","エラーメッセージ２´",...]},<br/>
 "responseData": null}<br/>
<br/>

正常時  <br/>
{"errors": null,　　　<br/>
 "responseData": 3}　　<br/>
 ３の箇所が、jsonicを用いて、Spring等でのajaxと同じになるようにしている　（この画面ではInteger１つ）
 
 また、info系のメッセージがある場合には
 messagesというオブジェクトに、errorsと同様の考え方で設定
 

</body>
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
;
var App = {
	util: {}
};

App.util = (function() {

    var

    _setHtml = function(type, html) {
        var id = "#" + type;
        var el = $(id);
        if (!el.length) {
            alert("id[" + id + "] does not exist. ");
        }

        if (html) {
            el.html(html);
        } else {
            el.html("");
        }
    },

    _arrayToUl = function(arrayText) {

        if (!(arrayText instanceof Array)) {
            alert(arrayText + " is not Array!");
            return "";
        }

        if (arrayText.length == 0) {
            return "";
        }

        var tmpHtml = '<ul>';
        for(var i = 0; i < arrayText.length; i++) {
            tmpHtml += '<li>'+ arrayText[i] +'</li>';
        }
        tmpHtml += '</ul>';

        return tmpHtml;
    },

    _setPageMessages = function(html) {
        _setHtml("messages", html);
    },

    _setPageErrors = function(html) {
        _setHtml("errors", html);
    },

    //エラー表示
    showErrors = function (errors) {

        var tmpErrors = errors ? $.extend(true,{},errors) : {};
        var otherErrors = [];

        $(":input").each(function() {
            var inputName = $(this).attr("name");

            var messageEl = $(".errors ." + inputName);
            if (messageEl.length) {
                if ((errors !== null) && errors[inputName]) {
                    messageEl.html( _arrayToUl(errors[inputName]) );

                    delete tmpErrors[inputName];
                } else {
                    messageEl.html("");
                }
            }
        });

        //上で取り出せる以外のエラーの表示
        jQuery.each(tmpErrors, function(key, valArray) {
            otherErrors = otherErrors.concat(valArray);
        });
        _setPageErrors(_arrayToUl(otherErrors));
    },

    //メッセージ表示
    showMessages = function (messages) {

        var tmpMessages = messages ? $.extend(true,{},messages) : {};
        var otherMessages = [];

        //メッセージの設定
        jQuery.each(tmpMessages, function(key, valArray) {
            otherMessages = otherMessages.concat(valArray);
        });
        _setPageMessages(_arrayToUl(otherMessages));
    },

    //レスポンスデータ表示
    showResult = function(result) {
    	_setHtml("result", result);
    },

    clearErrors = function() {
        showErrors(null);
    },

    clearMessages = function() {
        showMessages(null);
    };

    return {
        clearErrors: clearErrors,
        clearMessages: clearMessages,
        showErrors: showErrors,
        showMessages: showMessages,
        showResult: showResult
    };

}());

(function() {

    $("#mysubmit").on("click", function() {
        var fields = $(":input").serializeArray();

        $.ajax({
            type: "post",
            url: "submit5",
            data: fields,
            dataType: "json",
            cache: false,
            success: function(resData) {
                console.log("errors:" + resData.errors);
                console.log("messages:" + resData.messages);
                console.log("responseData:" + resData.responseData);

                App.util.showErrors(resData.errors);

                App.util.showMessages(resData.messages);

                App.util.showResult(resData.responseData);
            }
        });
    });
})();
</script>
</html>