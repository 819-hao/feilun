package com.seeease.flywheel.web.infrastructure.notify;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.flywheel.helper.notice.BusinessCustomerAuditNotice;
import com.seeease.flywheel.notify.entity.*;
import com.seeease.flywheel.web.entity.User;
import com.seeease.flywheel.web.infrastructure.mapper.UserSyncMapper;
import com.seeease.flywheel.web.infrastructure.notify.message.MiniProgramNoticeMessage;
import com.seeease.flywheel.web.infrastructure.notify.message.WxCpMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Component
public class MessageConvert {
    /**
     * 小程序消息类型
     */
    private final static String MINI_PROGRAM_NOTICE_MSG_TYPE = "miniprogram_notice";

    @Resource
    private UserSyncMapper userSyncMapper;

    @NacosValue(value = "${wx.cp.appid:}", autoRefreshed = true)
    private String appid;

    /**
     * @param notice
     * @return
     */
    public WxCpMessage convert(BaseNotice notice) {
        Assert.isTrue(CollectionUtils.isNotEmpty(notice.getToUserIdList())
                        || CollectionUtils.isNotEmpty(notice.getToUserRoleKey())
                , "通知的用户不能为空");

        List<String> userList = userSyncMapper.selectUserByRoleKey(notice.getToUserRoleKey(), notice.getShopId(), notice.getToUserIdList())
                .stream()
                .map(User::getUserid)
                .collect(Collectors.toList());

        Assert.isTrue(CollectionUtils.isNotEmpty(userList), "通知的用户不能为空");

        if (notice instanceof ApplyFinancialPaymentNotice) {
            return convertApplyFinancialPaymentNotice((ApplyFinancialPaymentNotice) notice, userList);
        } else if (notice instanceof StockTocWarnNotice) {
            return convertStockTocWarnNotice((StockTocWarnNotice) notice, userList);
        } else if (notice instanceof ShippingReminderNotice) {
            return convertShippingReminderNotice((ShippingReminderNotice) notice, userList);
        } else if (notice instanceof BusinessCustomerAuditNotice) {
            return convertBusinessCustomerAuditNotice((BusinessCustomerAuditNotice) notice, userList);
        } else if (notice instanceof FixReceiveNotice) {
            return convertFixReceiveNotice((FixReceiveNotice) notice, userList);
        } else if (notice instanceof FinancialInvoiceNotice) {
            return convertFinancialInvoiceNotice((FinancialInvoiceNotice) notice, userList);
        } else if (notice instanceof AccountReceiptConfirmNotice) {
            return convertAccountReceiptConfirmNotice((AccountReceiptConfirmNotice) notice, userList);
        }

        throw new IllegalArgumentException("消息转换失败，消息无法识别");
    }

    /**
     * 确认收款通知
     *
     * @param notice
     * @param userList
     * @return
     */
    private MiniProgramNoticeMessage convertAccountReceiptConfirmNotice(AccountReceiptConfirmNotice notice, List<String> userList) {

        return MiniProgramNoticeMessage.builder()
                .toUser(String.join("|", userList))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .page("/pages/finance/confirmPaymentDetail?map=" + notice.getScene() + "&id=" + notice.getId())
                        .title("收款确认单--" + notice.getState() + "，请及时处理")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(true)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .value(notice.getState())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("订单编号")
                                        .value(notice.getSerialNo())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }

    /**
     * 企业客户审核
     *
     * @param notice
     * @return
     */
    private MiniProgramNoticeMessage convertBusinessCustomerAuditNotice(BusinessCustomerAuditNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .page("/pages/firmClient/firmClientDetail?id=" + notice.getId())
                        .title("有新的企业客户信息待审核")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date())).build()
                )
                .build();
    }

    /**
     * 申请开票
     *
     * @param notice
     * @return
     */
    private MiniProgramNoticeMessage convertFinancialInvoiceNotice(FinancialInvoiceNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .page("/pages/finance/invoiceDetail?id=" + notice.getId())
                        .title("开票待办，请及时处理")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(true)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .value(notice.getState())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("订单编号")
                                        .value(notice.getSerialNo())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }

    /**
     * 申请打款
     *
     * @param notice
     * @return
     */
    private MiniProgramNoticeMessage convertApplyFinancialPaymentNotice(ApplyFinancialPaymentNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
//                        .page("/finance/paymentApplyDetail?id=" + notice.getId())
                        .page(notice.getScene() + "&id=" + notice.getId())
                        .title("申请打款待办，请及时处理")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(true)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .value(notice.getState())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("订单编号")
                                        .value(notice.getSerialNo())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }

    private MiniProgramNoticeMessage convertFixReceiveNotice(FixReceiveNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .page("/pages/repair/repairDetail?id=" + notice.getId() + "&serialNo=" + notice.getSerialNo())
                        .title("总部维修通知提示")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(true)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .value(notice.getState())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("订单编号")
                                        .value(notice.getSerialNo())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }

    private MiniProgramNoticeMessage convertStockTocWarnNotice(StockTocWarnNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .title("价格预警通知提示")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(true)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .value(notice.getState())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("表身号")
                                        .value(notice.getStockSn())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }

    /**
     * 发货提醒
     *
     * @param notice
     * @param userList
     * @return
     */
    private MiniProgramNoticeMessage convertShippingReminderNotice(ShippingReminderNotice notice, List<String> userList) {
        return MiniProgramNoticeMessage.builder()
                .toUser(userList.stream().collect(Collectors.joining("|")))
                .msgType(MINI_PROGRAM_NOTICE_MSG_TYPE)
                .miniProgramNotice(MiniProgramNoticeMessage.MiniProgramNotice.builder()
                        .appid(appid)
                        .title("订单来了，请及时发货")
                        .description(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()))
                        .emphasisFirstItem(false)
                        .contentItems(Arrays.asList(MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("订单编号")
                                        .value(notice.getSerialNo())
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("商品数量")
                                        .value(notice.getCount() + "")
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建时间")
                                        .value(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getCreatedTime()))
                                        .build()
                                , MiniProgramNoticeMessage.ContentItem.builder()
                                        .key("创建人")
                                        .value(notice.getCreatedBy())
                                        .build()

                        )).build())
                .build();
    }
}
