package com.seeease.flywheel.serve.base;

import java.lang.annotation.*;

/**
 * 自定义注解：校验非空字段
 * @author wbh
 * @date 2023/2/27
 */
@Documented
@Inherited
// 接口、类、枚举、注解
@Target(ElementType.FIELD)
//只是在运行时通过反射机制来获取注解，然后自己写相应逻辑（所谓注解解析器）
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidValue {
    String message();
}
