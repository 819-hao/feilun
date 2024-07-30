package com.seeease.flywheel.recycle.request;

import com.seeease.flywheel.recycle.entity.MallAgreement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 打款单信息
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkektRecycleUserBankRequest implements Serializable {

    /**
     * 回收单主键
     */
    private Integer recycleId;
    /**
     * 商城估价单主键
     */
    private String assessId;
    /**
     * 三方关联单号
     */
    private String bizOrderCode;
    /**
     * 账号名 -- 兆言等
     */
    private String accountName;

    /**
     * 银行 - 银行名称 and 开户行
     */
    private String bankName;

    /**
     * 账号-- 银行账户名 621767789007900
     */
    private String account;

    /**
     * 手机号 -- 17890909899
     */
    private String phone;

    /**
     * 身份证号 -- 42222566
     */
    private String idCard;

    /**
     * 身份证正面 --身份证正反面
     */
    private String frontImg;

    /**
     * 身份证背面 -- 身份证背面
     */
    private String backImg;
    /**
     * 协议内容 --回收协议图片
     */
    private MallAgreement agreement;
}
