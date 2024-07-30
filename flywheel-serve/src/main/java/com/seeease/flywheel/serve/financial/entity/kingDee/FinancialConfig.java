package com.seeease.flywheel.serve.financial.entity.kingDee;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsTypeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wbh
 * @date 2023/4/4
 */
public class FinancialConfig {

    public static String account ="62c4fd89e974a4" ;
    public static String userName ="研发" ;
    public static String password ="xiyi2021!" ;
    public static int lcId = 2052;

    //单据分类
    public static final Set<Integer> ORDER_TYPE = ImmutableSet.of(
            FinancialDocumentsTypeEnum.XS.getValue(),//销售出库
            FinancialDocumentsTypeEnum.XS_TH.getValue()//销售退货

    );

    //往来单位 销售类型和销售渠道
    public static final Map<Integer, String> DEALING_UNIT = MapUtil.
            builder(SaleOrderChannelEnum.SI_YU.getValue(), "md001")
            .put(SaleOrderChannelEnum.STORE.getValue(), "md001")
            .put(SaleOrderChannelEnum.T_MALL.getValue(), "md001").
            put(SaleOrderChannelEnum.DOU_YIN.getValue(), "dy001").
            put(SaleOrderChannelEnum.JD.getValue(), "jd001").
            put(SaleOrderChannelEnum.XI_YI_SHOP.getValue(), "md001").
            put(SaleOrderChannelEnum.XIAO_HONG_SHU.getValue(), "md001").
            put(SaleOrderChannelEnum.OTHER.getValue(), "CUST0189").//商家组
            put(SaleOrderChannelEnum.PEER.getValue(), "CUST0189").//商家组
            put(SaleOrderChannelEnum.XIAN_YU.getValue(), "CUST0189").//商家组
            put(SaleOrderChannelEnum.KUAI_SHOU.getValue(), "CUST0189").//商家组
            put(SaleOrderChannelEnum.ALIPAY.getValue(), "CUST0189").//商家组
            put(SaleOrderChannelEnum.TAO_BAO.getValue(), "CUST0189").//商家组
                    build();

    //PVOJ-1088任务 根据飞轮的商品归属
    public static final Map<Integer, String> GOODS_BELONG = MapUtil.
            builder(1, "GYSLB001_SYS").//小蜴
            put(22, "GYSLB001_SYS").//小蜴科技
            put(41, "GYSLB002_SYS").//天津古稀科技
            put(18, "GYSLB003_SYS").//天津稀小蜴
            put(21, "GYSLB004_SYS").//景德镇稀小蜴
            build();

    //结算组织/收款组织 订单来源
    public static final Map<String, String> CLEARING_ORGANIZATION = MapUtil.
            builder("南京天街", "017").
            put("温州印象", "011").
            put("商家组", "005").
            put("天猫国际", "018").
            put("稀蜴商城", "016").
            put("奥秘(A组)", "023").
            put("钢铁侠B1", "006").
            put("融易(C1)", "009").
            put("融易(C2)", "036").
            put("库元(E组)", "028").
            put("恒越F1", "027").
            put("杭州稀小蜴(S组)", "005").
            put("京东", "016").
            put("杭州星光", "008").
            put("重庆吾悦", "004").
            put("杭州天街", "002").
            put("绍兴百盛", "015").
            put("台州宝龙", "001").
            put("宁波天街", "026").
            put("南宁亨得利", "999").
            put("沈阳稀蜴", "019").
            put("南宁二店", "999").
            put("嘉兴自营店", "022").
            put("幂次F2", "024").
            put("奥特G", "030").
            put("奥秘A2", "040").
            put("支付宝", "012").
            put("大麦B2", "034").
            put("努力奋斗H", "035").
            build();
//    public static final Map<String, String> DOUYIN_CLEARING_ORGANIZATION = MapUtil.
//            builder("3695355", "016").//稀蜴真品
//            put("11732799", "015").//稀蜴真品绍兴店
//            put("13786541", "999").//南宁稀蜴
//            put("18701995", "011").//稀蜴温州
//            put("20085729", "016").//
//            put("35683061", "023").//
//            put("40846569", "006").//
//            put("47209030", "009").//
//            put("51934986", "036").//
//            put("52648017", "028").//
//            put("57110976", "027").//
//            put("65711065", "005").//
//            put("77875865", "016").//
//            put("83384044", "008").//
//            put("83740589", "004").//
//            put("83756903", "002").//
//            put("93132946", "015").//
//            put("105407995", "001").//
//            put("105753478", "026").//
//            put("112327074", "999").//
//            put("65685463", "019").//
//            put("31853473", "999").//
//            put("111290674", "022").//
//            put("111867538", "024").//
//            put("113190435", "030").//
//            put("113190462", "040").//
//            put("128205444", "012").//
//            put("大麦B2", "034").//
//            put("努力奋斗H", "035").//
//            build();
    //客户类别 销售类型和销售渠道
//    public static final Map<Integer, String> CLIENT_TYPE = MapUtil.
//            builder(SaleOrderChannelEnum.STORE.getValue(), "KHLB001_SYS").
//            put(SaleOrderChannelEnum.T_MALL.getValue(), "KHLB001_SYS").
//            put(SaleOrderChannelEnum.DOU_YIN.getValue(), "KHLB002_SYS").
//            put(SaleOrderChannelEnum.SI_YU.getValue(), "KHLB003_SYS").
//            put(SaleOrderChannelEnum.JD.getValue(), "KHLB007_SYS").
//            put(SaleOrderChannelEnum.OTHER.getValue(), "KHLB004_SYS").
//            put(SaleOrderChannelEnum.XIAO_HONG_SHU.getValue(), "KHLB003_SYS").
//            put(SaleOrderChannelEnum.XI_YI_SHOP.getValue(), "KHLB003_SYS").
//            build();

    //费用承担部门 订单来源
    public static final Map<String, String> EXPENSE_BEARING_DEPARTMENT = MapUtil.
            builder("南京天街", "BM000003").
            put("温州印象", "BM000003").
            put("商家组", "BM000003").
            put("天猫国际", "BM000007").
            put("稀蜴商城", "BM000009").
            put("奥秘(A组)", "BM000003").
            put("钢铁侠B1", "BM000003").
            put("融易(C1)", "BM000003").
            put("融易(C2)", "BM000003").
            put("库元(E组)", "BM000003").
            put("恒越F1", "BM000003").
            put("杭州稀小蜴(S组)", "BM000003").
            put("京东", "BM000010").
            put("杭州星光", "BM000003").
            put("重庆吾悦", "BM000003").
            put("杭州天街", "BM000003").
            put("绍兴百盛", "BM000003").
            put("台州宝龙", "BM000003").
            put("宁波天街", "BM000003").
            put("南宁亨得利", "BM000001").
            put("沈阳稀蜴", "BM000003").
            put("南宁二店", "BM000002").
            put("嘉兴自营店", "BM000003").
            put("幂次F2", "BM000003").
            put("奥特G", "BM000003").
            put("奥秘A2", "BM000003").
            put("支付宝", "BM000003").
            put("大麦B2", "BM000003").
            put("努力奋斗H", "BM000003").
            build();

    public static Map<String, String> USER_MAP = new HashMap<>();


    //退货
    public static final Set<Integer> RETURN_GOODS = ImmutableSet.of(FinancialDocumentsTypeEnum.XS_TH.getValue());

    //个人销售

    public static final Set<Integer> GOODS = ImmutableSet.of(FinancialDocumentsTypeEnum.XS.getValue());

    public static Boolean login = false;

}
