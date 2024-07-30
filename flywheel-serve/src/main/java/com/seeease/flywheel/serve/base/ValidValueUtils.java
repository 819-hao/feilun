package com.seeease.flywheel.serve.base;

import com.seeease.springframework.exception.e.OperationRejectedException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
public class ValidValueUtils {
    /**
     * 通过反射来获取javaBean上的注解信息，判断属性值信息，然后通过注解元数据来返回
     */
    public static <T> boolean doValidator(T clas) {
        Class<?> clazz = clas.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ValidValue checkNull = field.getDeclaredAnnotation(ValidValue.class);
            if (null != checkNull) {
                Object value = getValue(clas, field.getName());
                if (!notNull(value)) {
                    throwExcpetion(checkNull.message());
                }
            }
        }
        return true;
    }

    /**
     * 获取当前fieldName对应的值
     *
     * @param clas      对应的bean对象
     * @param fieldName bean中对应的属性名称
     * @return
     */
    public static <T> Object getValue(T clas, String fieldName) {
        Object value = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clas.getClass());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : props) {
                if (fieldName.equals(property.getName())) {
                    Method method = property.getReadMethod();
                    value = method.invoke(clas, new Object[]{});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 非空校验
     *
     * @param value
     * @return
     */
    public static boolean notNull(Object value) {
        if (null == value) {
            return false;
        }
        if (value instanceof String && isEmpty((String) value)) {
            return false;
        }
        if (value instanceof List && isEmpty((List<?>) value)) {
            return false;
        }
        return null != value;
    }

    public static boolean isEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }

    public static boolean isEmpty(List<?> list) {
        return null == list || list.isEmpty();
    }

    private static void throwExcpetion(String msg) {
        if (null != msg) {
            new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }
    }
}
