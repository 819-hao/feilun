package com.seeease.flywheel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/3/7
 */
public interface FlywheelConstant {


    /**
     * 总部id
     */
    int _ZB_ID = 1;

    /**
     * 直播发货3号楼门店id
     */
    int _DF3_SHOP_ID = 42;

    /**
     * 直播发货3号楼
     * 品牌运营
     * 嘉兴共享
     * 嘉兴南湖
     */
    List<Integer> EXCLUDE_SUBJECT_ID = Arrays.asList(42, 44, 52, 55);

    /**
     * 南宁一店
     */
    int SUBJECT_NN_YD = 5;
    int SHOP_NN_YD = 2;
    /**
     * 南宁二店
     */
    int SUBJECT_NN_ED = 9;
    int SHOP_NN_ED = 9;

    /**
     * 商家组
     */
    int _SJZ = 12;
    String _SJZ_SUBJECT = "15";

    int _ZB_RIGHT_OF_MANAGEMENT = 20;

    /**
     * 个人寄售 系数
     */
    double COEFFICIENT = 0.9D;

    /**
     * 角标第一个
     */
    int INDEX = 0;

    /**
     * 单个一个
     */
    int ONE = 1;

    /**
     * 倍率100
     */
    long MULTIPLIER_100 = 100L;

    /**
     * 倍率10
     */
    long MULTIPLIER_10 = 10L;

    int THREE = 3;

    String PRICING_RESTART_STATE = "重启";

    String PRICING_NEW_STATE = "新建";

    String SETTLEMENT_AUDIT = "结算核销(自动)";
    String DELIVERY_AUDIT = "发货核销(自动)";
    String IN_STORE_AUDIT = "入库核销(自动)";
    String CANCEL_ORDER_AUDIT = "取消核销(自动)";
    String REJECTION_AUDIT = "拒收核销(自动)";
    String C_AUDIT = "寄售退回自动核销";
    String AUTOMATIC_SYSTEM = "系统自动";
    String PAYMENT_AUDIT = "打款核销(自动)";
    String RETURN_AUDIT = "退货核销(自动)";
    String WRONG_AIRWAY_BILL_AUDIT = "错单核销(自动)";

    String RETURN_EXCHANGE_AUDIT = "返修核销(自动)";
    String EXCHANGE_AUDIT = "换货核销(自动)";
    String EXCHANGE_R_AUDIT = "转回收核销(自动)";

    /**
     * role 表中 总部财务 role_key
     */
    String HQ_FINANCE = "hq_finance";
    String PENDING_REVIEW = "待审核";
    String PENDING_INVOICE = "待开票";
    String COMPLETE_INVOICE = "已开票";

    String LANGUAGE = "zh-CN";

    /**
     * 需要前置的打款的采购业务类型
     */
    List<Integer> FORCE_PRE_PAYMENTS = Arrays.asList(101, 102, 106);

    String FINESS_S_99_NEW = "S级/99新";

    Integer newWatch = 7;
    Integer oldWatch = 8;

    int two = 2;

    String FIX_RECEIVE = "已接修";
    String FIX_FINISH = "已完成";
    String FIX = "维修中";
    String FIX_TIMEOUT = "已超时";

    String SHOP_MANAGER_ROLE = "店长";

    int INTEGER_DAFULT_VALUE = 0;

    String STRING_DAFULT_VALUE = "";

    String CUSTOMER_CONTACTNAME_VALUE = "无名";

    /**
     * 申请开票 主体 的图片
     * 18 天津稀小蜴科技有限公司  天津稀小蜴
     * 41 天津古稀科技有限公司  天津古稀科技
     * 南宁市稀蜴商务服务有限公司 对应的是飞轮中南宁卖出的商品归属为杭州小蜴或者小蜴的
     * 1 杭州小蜴网络技术有限公司 小蜴 小蜴科技
     * 21 景德镇稀小蜴技术有限公司 景德镇稀小蜴
     */
    Map<Integer, String> SUBJECT_URL_MAP = new HashMap<Integer, String>() {{
        put(18, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/bBv3Gh8ZjmmGm5tYtB9wdPm8ZcHsLahA.png");
        put(41, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/gVMhUsOPzVjnuHhos8T8QC9hnibRzhbA.png");
        put(5, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/CBjGIjOejyBmXiIsAGWN5YaQaIqVeIe5.png");
        put(9, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/CBjGIjOejyBmXiIsAGWN5YaQaIqVeIe5.png");
        put(1, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/sk5sqU2XT66su4k1ZlbpSfHOVqj6xN1C.png");
        put(22, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/sk5sqU2XT66su4k1ZlbpSfHOVqj6xN1C.png");
        put(21, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/XcO1KgBEXNCLCJtONRncp8hepdDVlsrR.png");
        put(3, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/gVMhUsOPzVjnuHhos8T8QC9hnibRzhbA.png");
        put(4, "https://seeease.oss-cn-hangzhou.aliyuncs.com/seeease-system/img/goods/gVMhUsOPzVjnuHhos8T8QC9hnibRzhbA.png");
    }};
    Map<Integer, String> SUBJECT_NAME_MAP = new HashMap<Integer, String>() {{
        put(18, "天津稀小蜴科技有限公司");
        put(41, "天津古稀科技有限公司");
        put(5, "南宁市稀蜴商务服务有限公司");
        put(9, "南宁市稀蜴商务服务有限公司");
        put(1, "杭州小蜴网络技术有限公司");
        put(22, "杭州小蜴网络技术有限公司");
        put(21, "景德镇稀小蜴技术有限公司");
        put(3, "天津古稀科技有限公司");
        put(4, "天津古稀科技有限公司");
    }};
    Map<Integer, String> SUBJECT_CODE_MAP = new HashMap<Integer, String>() {{
        put(18, "018");
        put(41, "033");
        put(5, "999");
        put(9, "999");
        put(1, "012");
        put(22, "012");
        put(21, "021");
        put(3, "033");
        put(4, "033");
    }};

    int _XY = 1;
    int _XY_KJ = 22;

    String MQ_NAME_SERVE = "mq.local.com:9876";
    String REGEX = "[\n`~!@#$%^&*()\\-+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";

    Integer WEEK = 7;
}
