package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class WuyuPricingPageRequest  extends PageRequest   {
    private Date beginCreateTime;
    private Date endCreateTime;
    private String stockSn;
}
