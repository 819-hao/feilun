package com.seeease.flywheel.web.common.express.client;

/**
 * @author Tiro
 * @date 2023/9/19
 */
public class SfExpressException extends RuntimeException {

    public SfExpressException(Exception e) {
        super(e);
    }

    public SfExpressException(String msg) {
        super(msg);
    }
}
