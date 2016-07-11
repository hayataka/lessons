package com.github.hayataka.lessons.action;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ActionMessagesUtil;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.github.hayataka.lessons.form.AddForm;
import com.github.hayataka.lessons.util.ResponseJsonUtil;

import net.arnx.jsonic.JSON;

public class AjaxAddAction {

    public Integer result;

    @ActionForm
    @Resource
    protected AddForm addForm;

    @Execute(validator = false)
    public String index() {
        return "index.jsp";
    }

    @Execute(input = "index.jsp")
    public String submit() {
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
        return "index.jsp";
    }
    
    @Execute(validator = false)
    public String submit1() {
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);

        ResponseUtil.write(result.toString());
        return null;
    }
    
    @Execute(validator = false)
    public String submit2() {
        Map<String, Object> responseData = new HashMap<String, Object>();

        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
        responseData.put("responseData", result);

        // JSONICライブラリ（http://jsonic.sourceforge.jp/）によりJSON形式へ変換
        String jsonText = JSON.encode(responseData);

        ResponseUtil.write(jsonText, "application/json");
        return null;
    }
    
    @Execute(input = "/ajaxValidateError")
    public String submit3() {
        Map<String, Object> responseData = new HashMap<String, Object>();
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
        responseData.put("responseData", result);
        String jsonText = JSON.encode(responseData);
        ResponseUtil.write(jsonText, "application/json");
        return null;
    }
    
    @Execute(input = "/ajaxValidateError")
    public String submit4() {
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);

        HttpServletRequest request = RequestUtil.getRequest();
        request.setAttribute("RESPONSE_DATA_KEY", result);
        // ResponseJsonInterceptorでは、request.getAttribute("RESPONSE_DATA_KEY")で
        // レスポンスデータを取り出す。
        return null;
    }
    
    @Execute(input = "/ajaxValidateError")
    public String submit5() {
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);

        ResponseJsonUtil.setResponseData(result);
        return null;
    }

    /**
     * MessageもJSON形式で送信する例
     * 
     */
    @Execute(input = "/ajaxValidateError")
    public String submit6() throws ParseException {
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);

        ActionMessages messages = new ActionMessages();
        messages.add("prop name", new ActionMessage("some message", false));
        ActionMessagesUtil.addMessages(RequestUtil.getRequest(), messages);

        ResponseJsonUtil.setResponseData(result);
        return null;
    }
}