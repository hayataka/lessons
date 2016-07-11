package com.github.hayataka.lessons.form;

import org.seasar.struts.annotation.Arg;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.Msg;
import org.seasar.struts.annotation.Required;

public class AddForm {

    @Required(arg0=@Arg(key="arg1"))
    @IntegerType
    public String arg1;

    @Required
    @IntegerType
    public String arg2;
}
