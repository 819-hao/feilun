package com.seeease.flywheel.web.common.work.pti;

import com.alibaba.cola.extension.ExtensionPointI;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.common.work.cmd.BaseCmd;

import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/1/17
 */
public interface WorkExtPtI<T, R extends BaseCmd> extends ExtensionPointI {
    /**
     * 获取入参class
     *
     * @return
     */
    Class<T> getRequestClass();

    /**
     * 参数校验
     *
     * @param cmd
     */
    void validate(R cmd);


    /**
     * 参数转换
     *
     * @param cmd
     */
    default void convert(R cmd) {
        if (Objects.isNull(cmd.getRequest())) {
            return;
        }
        T request = JSONObject.parseObject(JSONObject.toJSONString(cmd.getRequest()), getRequestClass());

        cmd.setRequest(request);
    }


}
