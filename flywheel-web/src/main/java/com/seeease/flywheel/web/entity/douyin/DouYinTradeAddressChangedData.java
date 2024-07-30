package com.seeease.flywheel.web.entity.douyin;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 抖音订单买家收货信息变更消息数据
 *
 * @author Tiro
 * @date 2023/4/25
 */
@Data
public class DouYinTradeAddressChangedData implements Serializable {
    /**
     * 店铺ID
     */
    @JsonAlias("shop_id")
    private Long shopId;

    /**
     * 父订单ID
     */
    @JsonAlias("p_id")
    private Long pId;

    /**
     * 子订单ID列表
     */
    @JsonAlias("s_ids")
    private List<Long> sIds;

    /**
     * 父订单状态，买家收货信息变更消息的status值为"2"
     */
    @JsonAlias("order_status")
    private Long orderStatus;

    /**
     * 订单类型： 0: 实物 2: 普通虚拟 4: poi核销 5: 三方核销 6: 服务市场
     */
    @JsonAlias("order_type")
    private Long order_type;

    /**
     * 订单取消时间
     */
    @JsonAlias("update_time")
    private Long update_time;

    /**
     * 收货人详细信息
     */
    @JsonAlias("receiver_msg")
    private ReceiverMsg receiverMsg;


    @Data
    public static class ReceiverMsg implements Serializable {
        /**
         * 收货地址，包含： province:省 city:市 town:区 detail:详细地址
         */
        private String addr;
        /**
         * 收货人姓名
         */
        private String encrypt_name;
        /**
         * 收货人手机号
         */
        private String encrypt_tel;
    }

    @Data
    public static class Addr {
        private AdministrativeArea province;
        private AdministrativeArea city;
        private AdministrativeArea town;
        private AdministrativeArea street;
        private String encrypt_detail;
    }

    @Data
    public static class AdministrativeArea {
        private String id;
        private String name;
    }


    public static void main(String[] args) {
        String s = "{\"province\\\":{\\\"name\\\":\\\"陕西省\\\",\\\"id\\\":\\\"61\\\"},\\\"city\\\":{\\\"name\\\":\\\"西安市\\\",\\\"id\\\":\\\"610100\\\"},\\\"town\\\":{\\\"name\\\":\\\"莲湖区\\\",\\\"id\\\":\\\"610104\\\"},\\\"street\\\":{\\\"name\\\":\\\"红庙坡街道\\\",\\\"id\\\":\\\"610104004\\\"},\\\"detail\\\":\\\"\\\",\\\"encrypt_detail\\\":\\\"#PzYfEZpxDmaYNP/W6TC+kuI8y6sW+Nrr#BYAANzPnUKdAq+IBJXJABgyEVAU4Lb5EBuNw45UotcuNVxMJhmCtCtj8cmSK8kBBMJp5unWZJ/jLNq9ZsJdF3aiIj43ynrOAlGTB/6T3M1QzExcJDQ/tfHSpVlWVAA==*CgwIARCtHBiqICABKAESPgo8x1+raNEh52E6ABE+lRLt/v10RRls7mFwHSKsGDHBKQiTVe8oC6EspmSe+o8avz09ZlbdbzCFbeZnE3SNGgA=#1##\\\"}";
        Addr a = JSONObject.parseObject(s.replace("\\\"", "\""), Addr.class);
        System.out.println(a.encrypt_detail);
    }
}