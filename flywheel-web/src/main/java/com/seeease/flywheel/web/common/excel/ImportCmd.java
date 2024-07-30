package com.seeease.flywheel.web.common.excel;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/1/16
 */
@Data
public class ImportCmd<T> implements Serializable {

    /**
     * 业务id(扩展点使用)
     */
    private String bizCode;

    /**
     * 用例(扩展点使用)
     */
    private String useCase;

    /**
     * 传递入参
     */
    @NotNull(message = "request不能为空")
    private T request;
}
