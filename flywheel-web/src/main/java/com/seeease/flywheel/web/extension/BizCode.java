package com.seeease.flywheel.web.extension;

/**
 * @author Tiro
 * @date 2023/1/16
 */
public interface BizCode {

    /**
     * 采购业务
     */
    String PURCHASE = "purchase";
    /**
     * 采购业务
     */
    String PURCHASE_RETURN = "purchaseReturn";
    /**
     * 销售业务
     */
    String SALE = "sale";

    String TO_C_SALE_RETURN = "toCSaleReturn";
    String TO_B_SALE_RETURN = "toBSaleReturn";

    /**
     * 质检
     */
    String QT = "qt";

    /**
     * 维修
     */
    String FIX = "fix";

    /**
     * 仓库
     */
    String STORAGE = "storage";

    /**
     * 门店
     */
    String SHOP = "shop";
    /**
     * 3号楼
     */
    String SHOP_B3 = "shopB3";

    /**
     * 调拨
     */
    String ALLOCATE = "allocate";

    String PRICING = "pricing";

    String IMPORT = "import";

    String STOCK = "stock";

    /**
     * 集单
     */
    String WMS_COLLECT = "wmsCollect";

    /**
     * 商城操作
     */
    String MALL = "mall";

    String PURCHASE_TASK = "purchaseTask";
}
