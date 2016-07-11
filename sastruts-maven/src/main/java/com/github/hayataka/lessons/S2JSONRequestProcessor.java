package com.github.hayataka.lessons;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.ReaderUtil;
import org.seasar.struts.action.S2RequestProcessor;
import org.seasar.struts.util.S2ActionMappingUtil;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class S2JSONRequestProcessor extends S2RequestProcessor {

    @Override
    protected void processPopulate(HttpServletRequest request,
            HttpServletResponse response, ActionForm form, ActionMapping mapping)
            throws ServletException {

        if (form == null) {
            return;
        }

        String contentType = request.getContentType();
//        String method = request.getMethod();
// POSTだけでなくても対応する
        if (contentType != null && contentType.startsWith("application/json")) {
            form.setServlet(servlet);
            form.setMultipartRequestHandler(null);
            processExecuteConfig(request, response, mapping);
            form.reset(mapping, request);

            Object actionForm = S2ActionMappingUtil.getActionMapping()
                    .getActionForm();
            try {
                ObjectMapper  mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                String postBody = ReaderUtil.readText(request.getReader());
                Object value = mapper.readValue(postBody, actionForm.getClass());

                Beans.copy(value, actionForm).execute();

            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }

        } else {

            super.processPopulate(request, response, form, mapping);

        }
    }

}