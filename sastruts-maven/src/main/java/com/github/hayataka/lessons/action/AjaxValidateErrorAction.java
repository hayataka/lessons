package com.github.hayataka.lessons.action;

import org.seasar.struts.annotation.Execute;

public class AjaxValidateErrorAction {

    @Execute(validator = false)
    public String index() {
        return null;
    }
}
