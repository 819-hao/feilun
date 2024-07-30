package com.seeease.flywheel.appexpress.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p></p>
 *
 * @author 西门 游
 * @version 1.0
 * @since 5/10/24 6:13 下午
 **/
@Data
public class AppExpressPageResult implements Serializable {
    private String code;
    private String express;
    private Integer type;
    private String orderImage;
    private String orderVideo;
    private List<String> goodsImages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
    private String createdBy;
}
