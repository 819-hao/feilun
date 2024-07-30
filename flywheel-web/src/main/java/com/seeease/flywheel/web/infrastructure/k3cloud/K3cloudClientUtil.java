package com.seeease.flywheel.web.infrastructure.k3cloud;

import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import kingdee.bos.webapi.client.K3CloudApiClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/8/3 09:58
 */
@Slf4j
public class K3cloudClientUtil {

    public static K3CloudApiClient getInstance() {

        K3CloudApiClient k3CloudApiClient = new K3CloudApiClient("http://8.142.124.57:8090/k3cloud/");

        Boolean login = false;

        try {
            login = k3CloudApiClient.login("62c4fd89e974a4", "研发", "xiyi2021!", 2052);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (login) {
            return k3CloudApiClient;
        }
        throw new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_EXCEPTION);
    }
}
