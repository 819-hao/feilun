package com.seeease.flywheel.serve.base.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class BillHandlerEventContext implements BeanPostProcessor {

    private static Map<Type, List<BillHandlerEventListener>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof BillHandlerEventListener)) {
            return bean;
        }
        this.addListener((BillHandlerEventListener) bean);
        return bean;
    }

    /**
     * @param bean
     */
    private synchronized void addListener(BillHandlerEventListener bean) {

        ParameterizedType parameterizedType = (ParameterizedType) bean.getClass().getGenericInterfaces()[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        Optional.ofNullable(handlerMap.get(type))
                .orElseGet(() -> {
                    List<BillHandlerEventListener> v = new Vector();
                    handlerMap.put(type, v);
                    return v;
                })
                .add(bean);

    }

    public List<BillHandlerEventListener> getHandlerList(Type type) {
        return handlerMap.get(type);
    }

}
