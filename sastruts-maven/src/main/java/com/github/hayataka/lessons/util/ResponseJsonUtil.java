package com.github.hayataka.lessons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

/**
 * JSON形式レスポンス用ユーティリティクラス
 *
 * @author TIS
 */
public final class ResponseJsonUtil {
    
    private static Log logger = LogFactory.getLog(ResponseJsonUtil.class);
    
    /**
     * requestにレスポンスデータを設定する際のキー値
     */
    public static final String RESPONSE_KEY_ON_SERVER = "tutorial.util.ResJsonUtil.RESPONSE_KEY_ON_SERVER";
    
    /**
     * 指定された名前のActionMessagesを、JSON形式展開向けのオブジェクトに変換して返す。
     * 
     * @param request 現在処理中の{@link HttpServletRequest}インスタンス
     * @param name 対象となるActionMessagesの名前
     * @return ActionMessagesをJSON形式展開向けに変換したオブジェクト（Map<String, List<String>>型）
     * @throws ServletException ServletException
     */
    public static Map<String, List<String>> getMessages(final HttpServletRequest request, final String name)
        throws ServletException {

        ActionMessages actionMessages = null;
        
        //nameに紐付くActionMessagesを取得
        try {
            actionMessages = getActionMessages(request, name);
        } catch (ServletException e) {
            throw e;
        }

        if ((actionMessages == null) || actionMessages.isEmpty()) {
            return null;
        }

        String message = null;
        Iterator<?> properties = actionMessages.properties();
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        
        while(properties.hasNext()) {
            String property = (String)properties.next();
            
            //propertyに紐付くActionMessageの処理
            Iterator<?> reports = actionMessages.get(property);
            while (reports.hasNext()) {

                ActionMessage report = (ActionMessage) reports.next();

                String key = report.getKey();
                if (key == null || key.length() == 0) {
                    continue;
                }

                List<String> tempValues = resultMap.get(property);
                if (tempValues == null) {
                    tempValues = new ArrayList<String>();
                    resultMap.put(property, tempValues);
                }

                if (report.isResource()) {
                	MessageResources res = MessageResourcesUtil.getMessageResources();
                	message = res.getMessage(report.getKey(), report.getValues());
                } else {
                    message = report.getKey();
                }

                if (message != null) {
                    tempValues.add(message);
                }
            }
        }
        
        return resultMap.size() > 0 ? resultMap : null;
    }
    
    /**
     * requestまたはsessionからActionMessagesを取得します。
     * 値がActionMessagesでない場合、型変換して返します。
     * 
     * @param request 現在処理中の{@link HttpServletRequest}インスタンス
     * @param paramName 取得する{@link ActionMessages}のキー値
     * @return ActionMessages {@link ActionMessages}
     * @throws ServletException ServletException
     */
    private static ActionMessages getActionMessages(final HttpServletRequest request, final String paramName)
        throws ServletException {

        ActionMessages am = new ActionMessages();

        Object value = findAttribute(request, paramName);
        if (value != null) {
            try {
                if (value instanceof String) {
                    am.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage((String) value));
                } else if (value instanceof String[]) {
                    String keys[] = (String[]) value;
                    for (int i = 0; i < keys.length; i++) {
                        am.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(keys[i]));
                    }
                } else if (value instanceof ActionErrors) {
                    ActionMessages m = (ActionMessages) value;
                    am.add(m);
                } else if (value instanceof ActionMessages) {
                    am = (ActionMessages) value;
                } else {
                    throw new ServletException("Cannot process ActionMessages instance of class "
                        + value.getClass().getName());
                }
            } catch (ServletException e) {
                throw e;
            } catch (Exception e) {
                synchronized(logger) {
                    logger.warn("Unable to retieve ActionMessage for paramName : " + paramName
                    + "[" + e.getMessage() + "]");
                }
            }
        }
        return am;
    }
    
    /**
     * request→sessionの順番で、指定されたnameの属性値を探して返します。
     * 存在しない場合、nullを返します。
     * 
     * @param request 現在処理中の{@link HttpServletRequest}インスタンス
     * @param name 属性名
     * @return requestまたはsessionに設定された、name属性値
     */
    private static Object findAttribute(final HttpServletRequest request, final String name) {
        if (name == null) {
            throw new NullPointerException("name should not be null.");
        }

        return doFindAttribute(request, name);
    }
    
    /**
     * request→sessionの順番で、指定されたnameの属性値を探して返します。
     * 存在しない場合、nullを返します。
     * 
     * @param request 現在処理中の{@link HttpServletRequest}インスタンス
     * @param name 属性名
     * @return requestまたはsessionに設定された、name属性値
     */
    private static Object doFindAttribute(final HttpServletRequest request, final String name) {

        Object o = request.getAttribute(name);
        if (o != null) {
            return o;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                o = session.getAttribute(name);
            } catch(IllegalStateException ise) { // CHECKSTYLE IGNORE THIS LINE
                // Checkstyleで「少なくとも1文はあるはずです。」と警告が出ますが、
                // ここでは例外を握りつぶしたいので、警告を抑制します。
                // Session has been invalidated.
                // Ignore.
            }
            if (o != null) {
                return o;
            }
        }
        
        return null;
    }
    
    /**
     * クライアントに返すレスポンスデータを設定します。
     * 
     * @param data レスポンスデータ
     */
    public static void setResponseData(Object data) {
        RequestUtil.getRequest().setAttribute(RESPONSE_KEY_ON_SERVER, data);
    }
    
    /**
     * クライアントに返すレスポンスデータを取得します。
     * 
     * @return レスポンスデータ
     */
    public static Object getResponseData() {
        return RequestUtil.getRequest().getAttribute(RESPONSE_KEY_ON_SERVER);
    }
}