package com.github.hayataka.lessons.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.framework.util.JSONSerializer;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

import com.github.hayataka.lessons.form.AddForm;

import net.arnx.jsonic.JSON;


public class AddAction {

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

// 基本形１： tutorialにあるレベル   
//    @Execute(validator = false)
//    public String submitAjax() {
//    	result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
//
//    	// text/plain 形式
//    	ResponseUtil.write(result.toString());
//    	return null;
//    }

    
    
    
// 基本形２：JSONICを用いる。　ただしバリデーションを個々の箇所に作りこむ必要性が
//    @Execute(validator = false)
//    public String submit2() {
//        Map<String, Object> responseData = new HashMap<String, Object>();
//
//        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
//        responseData.put("responseData", result);
//
//        // JSONICライブラリ（http://jsonic.sourceforge.jp/）によりJSON形式へ変換
//        String jsonText = JSON.encode(responseData);
//
//        ResponseUtil.write(jsonText, "application/json");
//        
//        // 例：１＋２の場合にクライアントに帰るデータ＝　　
//        //　{"responseData": 3}
//
//        
//        return null;
//    }
    @Execute(input = "/ajaxValidateError")
    public String submit3() {
        Map<String, Object> responseData = new HashMap<String, Object>();
        result = Integer.valueOf(addForm.arg1) + Integer.valueOf(addForm.arg2);
        responseData.put("responseData", result);
        String jsonText = JSON.encode(responseData);
        ResponseUtil.write(jsonText, "application/json");
        return null;
    }
    
    
}