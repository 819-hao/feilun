package com.seeease.flywheel.account.request;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/20 15:46
 */

public interface AccountImportRequest extends Serializable {

    /**

     * 删除范围
     *
     * @return
     */
    Integer getPageType();

}
