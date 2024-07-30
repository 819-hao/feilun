package com.seeease.flywheel.serve.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.purchase.dto.PurchaseDemandSaleOrderMqPushDto;
import com.seeease.flywheel.purchase.request.PurchaseDemandPageRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;
import com.seeease.flywheel.serve.base.DateUtils;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseDemandStatusEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseDemandMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchasePlanLineMapper;
import com.seeease.flywheel.serve.purchase.mq.PurchaseDemandConfirmMqProducer;
import com.seeease.flywheel.serve.purchase.mq.PurchaseDemandSaleOrderPushMqProducer;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseDemandService;
import com.seeease.flywheel.serve.purchase.service.BillPurchasePlanLineService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderDTO;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Slf4j
public class BillPurchaseDemandServiceImpl extends ServiceImpl<BillPurchaseDemandMapper, BillPurchaseDemand>
    implements BillPurchaseDemandService {

    @Resource
    private PurchaseDemandSaleOrderPushMqProducer producer;
    @Resource
    private UserService userService;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private BillSaleOrderService saleOrderService;
    @Override
    public Page<PurchaseDemandPageResult> pageOf(PurchaseDemandPageRequest request) {

        return getBaseMapper().page(Page.of(request.getPage(),request.getLimit()),request);
    }

    @Override
    public void pushMallRealOrder(List<BillSaleOrderDTO> saleOrderDTOList,Integer ccId) {

        BillSaleOrderDTO saleOrder = saleOrderDTOList.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("推送订单时订单参数不存在"));

        Assert.isTrue(saleOrder.getLines().size() == 1,"商城定金销售的商品只能有一个");

        //断言商城的销售单号
        String bizOrderCode = saleOrder.getOrder().getBizOrderCode();
       Assert.isTrue(StringUtils.isNotEmpty(bizOrderCode),"商城定金销售 bizCode不能为空");
        //断言定金需求订单
        LambdaQueryWrapper<BillPurchaseDemand> wq = Wrappers.<BillPurchaseDemand>lambdaQuery()
                .eq(BillPurchaseDemand::getSerial, bizOrderCode);
        BillPurchaseDemand demand = getOne(wq);
        Assert.notNull(demand,"bizCode 查找不到对应商品销售单");
        Assert.isTrue(demand.getStatus() == PurchaseDemandStatusEnum.OK,"定金销售需求状态非成功无法创建尾款单");
        //断言销售单是否存在
        LambdaQueryWrapper<BillSaleOrder> wq1 = Wrappers.<BillSaleOrder>lambdaQuery()
                .eq(BillSaleOrder::getBizOrderCode, bizOrderCode)
                .eq(BillSaleOrder::getSaleMode, SaleOrderModeEnum.DEPOSIT)
                .eq(BillSaleOrder::getSaleChannel, SaleOrderChannelEnum.XI_YI_SHOP)
                .ne(BillSaleOrder::getSaleState, SaleOrderStateEnum.CANCEL_WHOLE)
                .eq(BillSaleOrder::getDeleted,0);
        Assert.isTrue(saleOrderService.list(wq1).size() == 1,"已存在对应销售单无法继续创建");


        PurchaseDemandSaleOrderMqPushDto dto = new PurchaseDemandSaleOrderMqPushDto();
        dto.setOrderCode(bizOrderCode);


        BillSaleOrderLine stock = saleOrder.getLines()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("定金销售的商品不能为空"));

        dto.setSalePrice(stock.getClinchPrice().subtract(demand.getDeposit()).setScale(2,RoundingMode.HALF_DOWN));
        dto.setStockId(stock.getStockId());
        dto.setFlSaleOrderCode(saleOrder.getOrder().getSerialNo());


        Integer fiUserId;
        if ((fiUserId = saleOrder.getOrder().getFirstSalesman()) != null){
            Optional.ofNullable(userService.getById(fiUserId)).ifPresent(v-> dto.setFirstSalesman(v.getUserid()));
        }

        Integer seUserId;
        if ((seUserId = saleOrder.getOrder().getSecondSalesman()) != null){
            Optional.ofNullable(userService.getById(seUserId)).ifPresent(v-> dto.setSecondSalesman(v.getUserid()));
        }
        Integer thUserId;
        if ((thUserId = saleOrder.getOrder().getThirdSalesman()) != null){
            Optional.ofNullable(userService.getById(thUserId)).ifPresent(v-> dto.setThirdSalesman(v.getUserid()));
        }

        Optional.ofNullable(customerContactsService.getById(ccId))
                .ifPresent(v->{
                    dto.setContactName(v.getName());
                    dto.setContactAddress(v.getAddress());
                    dto.setContactPhone(v.getPhone());
                });

        //mq推送
        producer.sendMsg(dto);
    }
}




