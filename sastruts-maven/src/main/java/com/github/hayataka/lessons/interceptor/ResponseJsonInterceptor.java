package com.github.hayataka.lessons.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.github.hayataka.lessons.util.ResponseJsonUtil;

import net.arnx.jsonic.JSON;

/**
 * <p>JSON形式でレスポンスを返すためのインターセプタです。</p>
 * <p>
 * Actionの実行メソッドにインターセプトします。
 * 実行メソッドがnullを返す場合のみ、必要な情報をJSON形式に変換してレスポンスに書き込み、コミットします。
 * </p>
 * <p>
 * レスポンスとして返されるデータは以下の形式になります。
 * <pre>
 * {
 *     errors:   {errorProp1: [errorStr1-1, errorStr1-2, ...],
 *                errorProp2: [errorStr2-1, errorStr2-2, ...],
 *                ...},
 *     messages: {messagesProp1: [messagesStr1-1, messagesStr1-2, ...],
 *                messagesProp2: [messagesStr2-1, messagesStr2-2, ...],
 *                ...},
 *     
 *     responseData: object
 * }
 * </pre>
 * <dl>
 * <dt>errors
 * <dd>キー値{@link Globals.ERROR_KEY}で設定されたActionMessages。
 * <dt>messages
 * <dd>キー値{@link Globals.MESSAGE_KEY}で設定されたActionMessages。
 * <dt>responseData
 * <dd>任意のオブジェクト。（requestに"responseData"という名前で設定された値。）
 * </dl>
 * 
 * ActionMessagesは更に、名前がpropertyのメッセージ（ActionMessage）配列で構成されるオブジェクトへ展開されます.
 * </p>
 * @author TIS
 */
public class ResponseJsonInterceptor extends AbstractInterceptor{
    
    private static final long serialVersionUID = 1L;
    
    Log logger = LogFactory.getLog(this.getClass());
    
    public static final String ERRORS_KEY = "errors";
    
    public static final String MESSAGES_KEY = "messages";
    
    public static final String RESDATA_KEY = "responseData";
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
        Object ret = invocation.proceed();
        
        HttpServletRequest request = RequestUtil.getRequest();
        HttpServletResponse response = ResponseUtil.getResponse();
        
        // retはフォワード先を表す。
        // フォワード先がなく、レスポンスの送信がコミットされていなければ
        // ここでレスポンスを返送する。
        if (ret == null && !response.isCommitted()) { 

            Map<String, Object> jsonMap = new HashMap<String, Object>();
            
            //エラー内容を取り出す
            Map<String, List<String>> errors = ResponseJsonUtil.getMessages(request, Globals.ERROR_KEY);
            jsonMap.put(ERRORS_KEY, errors);
            if (errors != null) {
                //エラーがあった場合、200以外のステータスコードを返送する場合はここで指定
                //response.setStatus(400);
            }
            
            //メッセージ内容を取り出す
            Map<String, List<String>> messages = ResponseJsonUtil.getMessages(request, Globals.MESSAGE_KEY);
            jsonMap.put(MESSAGES_KEY, messages);
            
            //responseDataを取り出す
            Object responseData = ResponseJsonUtil.getResponseData();
            jsonMap.put(RESDATA_KEY, responseData);
            
            String text = JSON.encode(jsonMap);
            ResponseUtil.write(text, "application/json");
        }

        return ret;
    }
}