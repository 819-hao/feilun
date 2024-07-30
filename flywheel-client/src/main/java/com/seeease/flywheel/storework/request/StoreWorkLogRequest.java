package com.seeease.flywheel.storework.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 采购日志列表接口
 * @Date create in 2023/2/14 09:55
 */
@Data
public class StoreWorkLogRequest extends PageRequest implements Serializable {

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 操作类型
     */
    private Integer optType;

    private String startTime;

    private String endTime;

    private String stockSn;

    private List<Integer> stockIdList;

    /**
     * 来源
     */
    private Integer workSource;

    private String expressNumber;

    private String originSerialNo;

    private String createdBy;

    /**
     * 是否收发货
     */
    private boolean isReceiptOrDelivery;


    /**
     * 门店综合
     */
    private boolean storeComprehensive;

    private List<Integer> ipShopIds;

}
