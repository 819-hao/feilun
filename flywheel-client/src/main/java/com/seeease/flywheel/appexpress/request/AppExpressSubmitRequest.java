package com.seeease.flywheel.appexpress.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 *
 * @author 西门 游
 * @version 1.0
 * @since 5/10/24 6:12 下午
 **/
@Data
public class AppExpressSubmitRequest implements Serializable {


    private String code;
    private String express = "顺丰";
    private Integer type;
    private String orderImage;
    private String orderVideo;
    private List<String> goodsImages;
}
