package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class FinancialQueryAllRequest extends PageRequest implements Serializable {

    /**
     * 创建开始时间
     */
    private String beginCreateTime;

    /**
     * 创建结束时间
     */
    private String endCreateTime;

    /**
     * 订单分类：采购单、采购退货单等
     */
    private Integer orderType;
    private Integer saleMode;

    /**
     * 订单来源
     */
    private Integer orderOrigin;

    /**
     * 是否分成
     */
    private Integer divideInto;

    /**
     * 订单来源筛选项
     */
    private List<Integer> belongSubject;

    /**
     * 门店只能看自己的财务单
     */
    private Integer belongId;

    /**
     * 单据号
     */
    private String serialNumber;

    /**
     * 关联单号
     */
    private String assocSerialNumber;

    /**
     * 销售渠道
     */
    private Integer clcId;

    /**
     * 用于表示 是否从老财务 变更过来  1是 0否
     */
    private Integer oldToNew;

    /**
     * 客户名
     */
    private String customerName;
    /**
     * 客户类型
     */
    private Integer customerType;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;

    /**
     * 详情扩展条件
     */
    private FinancialDocumentsQueryExtDto ext;

    @Data
    public static class FinancialDocumentsQueryExtDto implements Serializable {
        /**
         * 寄售成交开始时间
         */
        private String beginSaleTime;
        /**
         * 寄售成交结束时间
         */
        private String endSaleTime;
        /**
         * 表身号
         */
        private String stockSn;
        /**
         * 型号
         */
        private String modelName;

        private String wno;
    }
}
