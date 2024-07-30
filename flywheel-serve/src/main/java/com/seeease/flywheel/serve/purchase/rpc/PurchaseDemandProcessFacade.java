package com.seeease.flywheel.serve.purchase.rpc;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.IPurchaseDemandFacade;
import com.seeease.flywheel.purchase.dto.PurchaseDemandConfirmMqPushDto;
import com.seeease.flywheel.purchase.request.PurchaseDemandCancelRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandConfirmRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandPageRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.enums.CustomerTypeEnum;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.Series;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.purchase.convert.PurchaseDemandConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import com.seeease.flywheel.serve.purchase.enums.PurchaseDemandStatusEnum;
import com.seeease.flywheel.serve.purchase.mq.PurchaseDemandConfirmMqProducer;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseDemandService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderStateEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Optional;

@DubboService(version = "1.0.0")
public class PurchaseDemandProcessFacade implements IPurchaseDemandFacade {
    @Resource
    private BillPurchaseDemandService purchaseDemandService;
    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BrandService brandService;
    @Resource
    private SeriesService seriesService;
    @Resource
    private PurchaseDemandConfirmMqProducer producer;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private CustomerContactsService customerContactsService;
    @Resource
    private CustomerService customerService;

    @Resource
    private BillSaleOrderService billSaleOrderService;


    @Override
    public void create(PurchaseDemandCreateRequest request) {
        transactionTemplate.executeWithoutResult(state->{

            //尝试创建联系人
            CustomerContacts exist = customerContactsService.queryCustomerContactsByNameAndPhone(null, request.getContactPhone());
            if (exist == null){
                Customer customer = new Customer();
                customer.setCustomerName(request.getContactName());
                customer.setType(CustomerTypeEnum.INDIVIDUAL);
                customer.setProp("下游");

                customerService.save(customer);

                exist = new CustomerContacts();
                exist.setCustomerId(customer.getId());
                exist.setName(request.getContactName());
                exist.setPhone(request.getContactPhone());
                exist.setAddress(request.getContactAddress());
                customerContactsService.save(exist);
            }




            BillPurchaseDemand entity = PurchaseDemandConverter.INSTANCE.to(request);
            entity.setStatus(PurchaseDemandStatusEnum.WAIT);
            entity.setContactId(exist.getId());

            purchaseDemandService.save(entity);


        });
    }

    @Override
    public PageResult<PurchaseDemandPageResult> page(PurchaseDemandPageRequest request) {

        Page<PurchaseDemandPageResult> ret  = purchaseDemandService.pageOf(request);

        return PageResult.<PurchaseDemandPageResult>builder()
                .totalPage(ret.getPages())
                .totalCount(ret.getTotal())
                .result(ret.getRecords())
                .build();
    }

    @Override
    public void confirm(PurchaseDemandConfirmRequest request) {
        BillPurchaseDemand entity = purchaseDemandService.getById(request.getId());
        Assert.notNull(entity,"id错误查找不到对应数据");
        Assert.isTrue(entity.getStatus() == PurchaseDemandStatusEnum.WAIT,"状态无法变更");
        transactionTemplate.executeWithoutResult(t->{
            BillPurchaseDemand e = PurchaseDemandConverter.INSTANCE.to(request);
            e.setStatus(PurchaseDemandStatusEnum.OK);
            boolean ret = purchaseDemandService.updateById(e);
            Assert.isTrue(ret,"确认失败");


            // 发送mq消息
            GoodsWatch goodsWatch = goodsWatchService.getById(request.getGoodsWatchId());
            String brandName = Optional.ofNullable(brandService.getById(goodsWatch.getBrandId())).map(Brand::getName).orElse("");
            String seriesName = Optional.ofNullable(seriesService.getById(goodsWatch.getSeriesId())).map(Series::getName).orElse("");

            producer.sendMsg(PurchaseDemandConfirmMqPushDto.builder()
                            .brandName(brandName)
                            .serial(seriesName)
                            .model(goodsWatch.getModel())
                            .serial(entity.getSerial())
                    .build());
        });

    }

    @Override
    public void cancelHeadOrder(PurchaseDemandCancelRequest request) {
        BillPurchaseDemand one;
        if (request.getId() != null){
            one = purchaseDemandService.getById(request.getId());
        }else {
            LambdaQueryWrapper<BillPurchaseDemand> wq = Wrappers.<BillPurchaseDemand>lambdaQuery()
                    .eq(BillPurchaseDemand::getSerial, request.getSerial())
                    .last("limit 1");
            one = purchaseDemandService.getOne(wq);
        }
        Assert.notNull(one,"查询不到对应订购需求 request:" + JSONObject.toJSONString(request));


        if (one.getStatus() == PurchaseDemandStatusEnum.CANCEL){
            return;
        }else if (one.getStatus() == PurchaseDemandStatusEnum.OK){
            LambdaQueryWrapper<BillSaleOrder> wq = Wrappers.<BillSaleOrder>lambdaQuery()
                    .eq(BillSaleOrder::getBizOrderCode, one.getSerial())
                    .eq(BillSaleOrder::getSaleMode, SaleOrderModeEnum.DEPOSIT)
                    .eq(BillSaleOrder::getSaleChannel, SaleOrderChannelEnum.XI_YI_SHOP)
                    .ne(BillSaleOrder::getSaleState, SaleOrderStateEnum.CANCEL_WHOLE)
                    .eq(BillSaleOrder::getDeleted,0)
                    .last("limit 1");

            BillSaleOrder saleOrder = billSaleOrderService.getOne(wq);

            Assert.isTrue(saleOrder == null,"需求单对应的销售单取消后才可取消需求单");


        }
        one.setStatus(PurchaseDemandStatusEnum.CANCEL);
        purchaseDemandService.updateById(one);

    }



    @Override
    public void mallDepositSalesPayed(String seriesNo) {
        BillSaleOrder billSaleOrder = billSaleOrderService.selectBySerialNo(seriesNo);


        Assert.notNull(billSaleOrder,"订单编码：" + seriesNo + " 在商场内查找不到对应订单");
        if (billSaleOrder.getMallPayed()){
            return;
        }
        billSaleOrder.setMallPayed(true);
        billSaleOrderService.updateById(billSaleOrder);
    }



}
