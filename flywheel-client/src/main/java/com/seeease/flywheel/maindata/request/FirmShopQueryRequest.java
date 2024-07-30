package com.seeease.flywheel.maindata.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirmShopQueryRequest extends PageRequest {

    /**
     * 公司名称
     */
    private String firmName;
    /**
     * 汇付商户号
     */
    private String hfMemberId;

}
