package com.seeease.flywheel.web.extension;

/**
 * @author Tiro
 * @date 2023/1/16
 */
public interface UseCase {

    /**
     * 查询列表
     */
    String QUERY_LIST = "list";

    /**
     * 查询详情
     */
    String QUERY_DETAILS = "details";

    /**
     * 待出库详情
     */
    String OUT_STORAGE_DETAILS = "outStorageDetails";

    /**
     * RFID待出库详情
     */
    String OUT_STORAGE_DETAILS_RFID = "outStorageDetailsRfid";

    /**
     * 待发货详情
     */
    String LOGISTICS_DELIVERY_DETAILS = "logisticsDeliveryDetails";

    /**
     * 流程创建
     */
    String PROCESS_CREATE = "create";

    /**
     * 取消
     */
    String CANCEL = "cancel";

    /**
     * 收货列表
     */
    String RECEIVING_LIST = "receivingList";

    /**
     * 发货列表
     */
    String DELIVERY_LIST = "deliveryList";

    /**
     * 出库列表
     */
    String OUT_STORAGE_LIST = "outStorageList";

    /**
     * 入库列表
     */
    String IN_STORAGE_LIST = "inStorageList";

    /**
     * 上传快递单号
     */
    String UPLOAD_EXPRESS_NUMBER = "uploadExpressNumber";

    /**
     * 门店收货
     */
    String SHOP_RECEIVING = "shopReceiving";
    /**
     * 归还客户
     */
    String CONFIRM_RETURN = "confirmReturn";

    String ACCEPT_REPAIR = "acceptRepair";

    String ACCEPT_REPAIR_FIX = "acceptRepairByFix";

    String ACCEPT_REPAIR_RETURN = "acceptRepairByReturn";

    String WAIT_DELIVER = "waitDeliver";

    /**
     * 物流收货
     */
    String LOGISTICS_RECEIVING = "logisticsReceiving";

    /**
     * 物流发货
     */
    String LOGISTICS_DELIVERY = "logisticsDelivery";

    /**
     * 质检转交
     */
    String QT_DELIVERY = "qtDelivery";

    /**
     * 质检判定
     */
    String QT_DETERMINE = "qtDetermine";

    String BATCH_PASS = "batchPass";

    /**
     * 仓管入库
     */
    String IN_STORAGE = "inStorage";

    /**
     * 仓库出库
     */
    String OUT_STORAGE = "outStorage";

    /**
     * 维修员接修
     */
    String REPAIR_RECEIVING = "repairReceiving";

    /**
     * 维修完成
     */
    String REPAIR_COMPLETED = "repairCompleted";

    /**
     * 销售确认
     */
    String SALE_ORDER_CONFIRM = "saleConfirm";

    /**
     * 调拨导入
     */
    String ALLOCATE_CREATE = "allocateCreate";
    /**
     * 型号直播话术
     */
    String MODEL_SCRIPT = "modelLiveScript";

    /**
     * 采购流水导入
     */
    String TX_HISTORY = "purchaseVouchers";

    /**
     * 门店配额
     */
    String STORE_QUOTA = "storeQuota";

    /**
     * 销售导入
     */
    String SALE_CREATE = "saleCreate";
    /**
     * 销售退货导入
     */
    String SALE_RETURN_CREATE = "saleReturnCreate";
    /**
     * 销售寄售导入
     */
    String SALE_SETTLE = "saleSettle";
    /**
     * 同行寄售结算导入
     */
    String PURCHASE_BATCH_SETTLE = "purchaseBatchSettle";
    /**
     * 申请开票导入
     */
    String FINANCIAL_INVOICE_STOCK = "financialInvoiceStock";
    /**
     * 活动商品导入
     */
    String STOCK_PROMOTION = "stockPromotion";

    /**
     * 活动商品下架导入
     */
    String STOCK_PROMOTION_TAKE_DOWN = "stockPromotionTakeDown";
    /**
     * 采购价修改导入
     */
    String STOCK_PURCHASE_UPDATE = "stockPurchaseUpdate";
    /**
     * 财务流水导入
     */
    String FINANCIAL_STATEMENT = "financialStatement";
    /**
     * 采购导入
     */
    String PURCHASE_CREATE = "purchaseCreate";

    /**
     * 型号修改
     */
    String UPDATE_GOODS_WATCH = "updateGoodsWatch";

    /**
     * 采购计划
     */
    String PURCHASE_PLAN = "purchasePlan";


    /**
     * 采购退货导入
     */
    String PURCHASE_RETURN_CREATE = "purchaseReturnCreate";

    /**
     * 定价新建
     */
    String PRICING_CREATE = "pricingCreate";

    String PRICING_PASS = "pricingPass";

    /**
     * 型号价格变更
     */
    String MODEL_PRICE_CHANGE = "modelPriceChange";


    /**
     * 销售优先等级修改
     */
    String SALES_PRIORITY_MODIFY = "salesPriorityModify";

    /**
     * 调拨保卡管理
     */
    String STOCK_GUARANTEE_CARD_MANAGE = "stockGuaranteeCardManage";

    /**
     * 出库保存表身号
     */
    String OUT_STORAGE_SUPPLY_STOCK = "outStorageSupplyStock";

    /**
     * 采购需求列表
     */
    String APPLY_PURCHASE_LIST = "applyPurchaseList";

    /**
     * 批量定价
     */
    String BATCH_PRICING = "batchPricing";

    /**
     * 抖音商品映射
     */
    String DOU_YIN_PRODUCT_MAPPING = "douYinProductMapping";

    String FINANCE_CREATE = "financeCreate";
    String PEOPLE_CREATE = "peopleCreate";
    String MANPOWER_CREATE = "manpowerCreate";

    String STOCK_MANAGE_INFO = "stockManageInfo";
    String STOCK_MANAGE_SHELVES_INFO = "stockManageShelvesInfo";

    /**
     * 加载
     */
    String PROCESS_LOAD = "load";

    /**
     * 确认
     */
    String FIRST_VERIFY = "firstVerify";

    String SECOND_VERIFY = "secondVerify";

    /**
     * 上传打款信息
     */
    String UPLOAD_REMIT = "uploadRemit";

    /**
     * 请求付款信息
     */
    String CLIENT_PAY = "clientPay";

    /**
     * 客户经理后台建单
     */
    String LOAD_CREATE = "loadCreate";

    String REFUSE_RETURN = "refuseReturn";

    /**
     * 首次报价
     */
    String FIRST_OFFER = "firstOffer";
    /**
     * 二次报价
     */
    String SECOND_OFFER = "secondOffer";

    /**
     * 同意收货
     */
    String AGREE_GOODS = "agreeGoods";

    String RETURN_GOODS = "returnGoods";

    String ADMIN_CANCEL = "adminCancel";

    /**
     * 附近库存导入
     */
    String ATTACHMENT_STOCK_CREATE = "attachmentStock";
    /**
     * 调拨商品导入
     */
    String ALLOCATE_STOCK = "allocateStock";
    /**
     * 借货导入
     */
    String BORROW_STOCK = "borrowStock";

    /**
     * 维修员接修
     */
    String ALLOT_RECEIVING = "allot";
    String FOREIGN_RECEIVING = "foreign";
    /**
     * 物鱼定价
     */
    String WUYU_PRICING = "wuyuPrice";

    /**
     * 同行集采结算导入
     */
    String PURCHASE_GROUP_SETTLE = "purchaseGroupSettle";
}
