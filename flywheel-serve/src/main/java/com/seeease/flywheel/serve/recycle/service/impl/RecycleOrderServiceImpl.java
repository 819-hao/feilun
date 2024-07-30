package com.seeease.flywheel.serve.recycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.maindata.entity.UserInfo;
import com.seeease.flywheel.recycle.entity.RecycleMessage;
import com.seeease.flywheel.recycle.request.MarketRecycleOrderRequest;
import com.seeease.flywheel.recycle.request.RecycleOrderListRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderResult;
import com.seeease.flywheel.recycle.result.RecyclingListResult;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.recycle.domain.RecycleDomain;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.flywheel.serve.recycle.mapper.RecycleOrderMapper;
import com.seeease.flywheel.serve.recycle.mq.MallLogisticsReturnProducers;
import com.seeease.flywheel.serve.recycle.mq.MallPayOrderProducers;
import com.seeease.flywheel.serve.recycle.mq.RecycleOrderProducers;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import com.seeease.springframework.Tuple2;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Auther Gilbert
 * @Date 2023/9/4 11:05
 */
@Service
public class RecycleOrderServiceImpl extends ServiceImpl<RecycleOrderMapper, MallRecyclingOrder> implements IRecycleOrderService {

    @Resource
    private RecycleDomain recycleDomain;
    @Resource
    private BrandService brandService;
    @Resource
    private RecycleOrderMapper recycleOrderMapper;
    @Resource
    private RecycleOrderProducers recycleOrderProducers;
    @Resource
    private MallLogisticsReturnProducers mallLogisticsReturnProducers;

    @Resource
    private MallPayOrderProducers mallPayOrderProducers;

    @Resource
    private BillPurchaseMapper billPurchaseMapper;
    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;

    @Resource
    private BillSaleOrderMapper billSaleOrderMapper;
    @Resource
    private TagService tagService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecycleOrderResult create(MarketRecycleOrderRequest request) {
        //查询客户经理信息
        Map<String, UserInfo> userMap = recycleDomain.queryUser(request.getEmployeeId());
        //查询客户信息和联系人信息
        Tuple2<Integer, Integer> tuple2Customer = recycleDomain.customerCreateOrQuery(null, request.getPhone());
        //查询品牌
        Brand brand = brandService.getOne(new LambdaQueryWrapper<Brand>().eq(Brand::getName, request.getBrandName()));
        //转换一层url,商城存的相对路径，飞轮要求绝对路径
        String collect = null;
        if (CollectionUtil.isNotEmpty(request.getAssessPictureVOS())) {
            collect = request.getAssessPictureVOS().stream().map(v -> "https://seeease.oss-cn-hangzhou.aliyuncs.com/mall/upload/" + v).collect(Collectors.joining(","));
        }
        String serial = RecycleOrderTypeEnum.fromCode(request.getRecycleType()) == RecycleOrderTypeEnum.RECYCLE ? SerialNoGenerator.generateRecycleSerialNo() : SerialNoGenerator.generateBuyBackSerialNo();

        //参数赋值
        MallRecyclingOrder mallRecyclingOrder = new MallRecyclingOrder()
                .setSerial(serial)
                .setCustomerId(tuple2Customer.getV1())
                .setCustomerContactId(tuple2Customer.getV2())
                .setShopImage(collect)
                .setBrandId(Objects.nonNull(brand) ? brand.getId() : null)
                .setAssessId(request.getAssessId())
                .setDemandId(request.getStoreId())
                .setSaleSerialNo(request.getSerialNo())//如果是回购。需要有飞轮的单号
                .setStockId(request.getStockId())//回收是没有商品id的
                .setEmployeeId(userMap.get(request.getEmployeeId()).getId())
                .setBizOrderCode(request.getBizOrderCode())
                .setRecycleType(RecycleOrderTypeEnum.fromCode(request.getRecycleType()))
                .setSymbol(0)
                .setBalance(BigDecimal.ZERO)
                .setState(RecycleOrderTypeEnum.RECYCLE == RecycleOrderTypeEnum.fromCode(request.getRecycleType()) ? RecycleStateEnum.UN_CONFIRMED : RecycleStateEnum.MAKE_ORDER);
        //进行保存回收、回购单
        recycleOrderMapper.insert(mallRecyclingOrder);
        return new RecycleOrderResult()
                .setSerialNo(serial)
                .setRecycleId(mallRecyclingOrder.getId())
                .setUserId(request.getEmployeeId())
                .setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes())
                .setStatus(mallRecyclingOrder.getState().getValue());
    }

    @Override
    public Page<RecyclingListResult> listByRequest(RecycleOrderListRequest request) {
        return baseMapper.listByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<RecyclingListResult> listByRequestByRecycle(RecycleOrderListRequest request) {
        return baseMapper.listByRequestByRecycle(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public void updateRecycleStatus(MallRecyclingOrder mallRecyclingOrder) {
        UpdateByIdCheckState.update(baseMapper, mallRecyclingOrder);
        //发送给商城发送mq
        sendMq(mallRecyclingOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRecycleById(MallRecyclingOrder mallRecyclingOrder) {
        if (Objects.nonNull(mallRecyclingOrder)) {
            return baseMapper.updateById(mallRecyclingOrder);
        }
        return 0;
    }

    @Override
    public MallRecyclingOrder queryBySaleSerialNo(@NonNull String saleSerialNo) {
        LambdaQueryWrapper<MallRecyclingOrder> mallRecyclingOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        mallRecyclingOrderLambdaQueryWrapper.eq(MallRecyclingOrder::getSaleSerialNo, saleSerialNo)
                .ne(MallRecyclingOrder::getState,RecycleStateEnum.CANCEL_WHOLE)
                .eq(MallRecyclingOrder::getDeleted, Boolean.FALSE)
                .last("limit 1");
        return this.baseMapper.selectOne(mallRecyclingOrderLambdaQueryWrapper);
    }


    @Override
    public void checkIntercept(List<String> originSerialNoList) {

        if (CollectionUtils.isEmpty(originSerialNoList)) {
            return;
        }

        List<Integer> collect = billPurchaseMapper.selectList(Wrappers.<BillPurchase>lambdaQuery().in(BillPurchase::getSerialNo, originSerialNoList)).stream().map(BillPurchase::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            List<MallRecyclingOrder> mallRecyclingOrderList = baseMapper.selectList(Wrappers.<MallRecyclingOrder>lambdaQuery().in(MallRecyclingOrder::getPurchaseId, collect));
            if (CollectionUtils.isEmpty(mallRecyclingOrderList)) {
                return;
            }
            if (CollectionUtils.isNotEmpty(mallRecyclingOrderList)) {

                for (MallRecyclingOrder mallRecyclingOrder : mallRecyclingOrderList) {
                    mallLogisticsReturnProducers.sendMsg(new RecycleMessage()
                            .setRecycleType(mallRecyclingOrder.getRecycleType().getValue())
                            .setType(mallRecyclingOrder.getType().getValue())
                            .setAssessId(mallRecyclingOrder.getAssessId())
                            .setBizOrderCode(mallRecyclingOrder.getBizOrderCode())
                            .setRecycleId(mallRecyclingOrder.getId())
                    );
                }
            }
        }


    }

    @Override
    public void checkIntercept(String originSerialNo) {
        if (StringUtils.isEmpty(originSerialNo)) {
            return;
        }

        List<Integer> collect = billPurchaseMapper.selectList(Wrappers.<BillPurchase>lambdaQuery().in(BillPurchase::getSerialNo, Arrays.asList(originSerialNo))).stream().map(BillPurchase::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            List<MallRecyclingOrder> mallRecyclingOrderList = baseMapper.selectList(Wrappers.<MallRecyclingOrder>lambdaQuery().in(MallRecyclingOrder::getPurchaseId, collect));
            if (CollectionUtils.isEmpty(mallRecyclingOrderList)) {
                return;
            }
            if (CollectionUtils.isNotEmpty(mallRecyclingOrderList)) {

                for (MallRecyclingOrder mallRecyclingOrder : mallRecyclingOrderList) {
                    recycleOrderProducers.sendMsg(new RecycleMessage()
                            .setRecycleType(mallRecyclingOrder.getRecycleType().getValue())
                            .setType(mallRecyclingOrder.getType().getValue())
                            .setAssessId(mallRecyclingOrder.getAssessId())
                            .setBizOrderCode(mallRecyclingOrder.getBizOrderCode())
                            .setRecycleId(mallRecyclingOrder.getId())
                    );
                }
            }
        }
    }

    @Override
    public List<Integer> intercept(Integer purchaseId) {

        if (ObjectUtils.isEmpty(purchaseId)) {
            return Arrays.asList();
        }

        List<Integer> list = new ArrayList<>();

        List<BillPurchase> billPurchaseList = billPurchaseMapper.selectList(Wrappers.<BillPurchase>lambdaQuery().in(BillPurchase::getId, Arrays.asList(purchaseId)));

        if (CollectionUtil.isNotEmpty(billPurchaseList)) {

            for (BillPurchase billPurchase : billPurchaseList) {

                List<MallRecyclingOrder> mallRecyclingOrderList = baseMapper.selectList(Wrappers.<MallRecyclingOrder>lambdaQuery().in(MallRecyclingOrder::getPurchaseId, Arrays.asList(billPurchase.getId())));
                if (CollectionUtils.isEmpty(mallRecyclingOrderList)) {
                    continue;
                }

                BillPurchaseLine billPurchaseLine = billPurchaseLineMapper.selectOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, billPurchase.getId()));

                List<MallRecyclingOrder> collect = mallRecyclingOrderList.stream().filter(mallRecyclingOrder -> Objects.nonNull(mallRecyclingOrder.getSaleId())).collect(Collectors.toList());

                for (MallRecyclingOrder recyclingOrder : collect) {
                    MallRecyclingOrder r = new MallRecyclingOrder();
                    r.setId(recyclingOrder.getId());
                    //采购接受维修才走这一步
                    //销售单如何查询
                    BillSaleOrder saleOrder = billSaleOrderMapper.selectById(recyclingOrder.getSaleId());

                    BigDecimal balance = saleOrder.getTotalSalePrice().subtract(billPurchaseLine.getBuyBackPrice());

                    //修改值
                    r.setBalance(balance);
                    r.setSymbol(balance.signum());
                    if (balance.signum() > 0) {
                        //不开启销售 不改状态 直接完成
                        r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_COMPLETE);
                        updateRecycleStatus(r);
                    } else if (balance.signum() <= 0) {
                        if (balance.signum() < 0) {
                            //开启销售 并发货 //待打款
                            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_WAIT_UPLOAD_BANK);
                            updateRecycleStatus(r);
                        } else {
                            //开启销售
                            r.setTransitionStateEnum(RecycleStateEnum.TransitionEnum.MAKE_ORDER_COMPLETE);
                            updateRecycleStatus(r);
                        }
                        list.add(recyclingOrder.getId());
                    }
                }
            }
        }

        return list;
    }

    public void sendMq(@NonNull MallRecyclingOrder request) {
        MallRecyclingOrder mallRecyclingOrder = baseMapper.selectById(request.getId());
        Assert.isTrue(Objects.nonNull(mallRecyclingOrder), "查询不到订单");
        RecycleMessage recycleMessage = new RecycleMessage()
                .setRecycleId(request.getId())
                .setStatus(((RecycleStateEnum) request.getTransitionStateEnum().getToState()).getValue())
                .setAssessId(mallRecyclingOrder.getAssessId())
                .setRecycleType(mallRecyclingOrder.getRecycleType().getValue())
                .setBizOrderCode(mallRecyclingOrder.getBizOrderCode());
        //当时回购的时候，发送mq数据
        if (Arrays.asList(RecycleStateEnum.WAIT_UPLOAD_CUSTOMER).contains(((RecycleStateEnum) request.getTransitionStateEnum().getToState()))
                && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.BUY_BACK) {
//            recycleOrderProducers.sendMsg(recycleMessage);
        } else if (Arrays.asList(RecycleStateEnum.COMPLETE).contains(((RecycleStateEnum) request.getTransitionStateEnum().getToState()))
                && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE
                && mallRecyclingOrder.getPurchaseId() != null && mallRecyclingOrder.getSaleId() != null) {//回收是已完成才发送mq信息
            recycleOrderProducers.sendMsg(recycleMessage);
        } else if (Arrays.asList(RecycleStateEnum.CANCEL_WHOLE).contains(((RecycleStateEnum) request.getTransitionStateEnum().getToState()))
                && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.RECYCLE
                && ObjectUtils.isEmpty(mallRecyclingOrder.getPurchaseId())
                && ObjectUtils.isNotEmpty(mallRecyclingOrder.getExpressNumber())
        ) {
            //二次确认退回
            mallLogisticsReturnProducers.sendMsg(recycleMessage);
        } else if (Arrays.asList(RecycleStateEnum.COMPLETE).contains(((RecycleStateEnum) request.getTransitionStateEnum().getToState()))
                && mallRecyclingOrder.getRecycleType() == RecycleOrderTypeEnum.BUY_BACK
                && ObjectUtils.isNotEmpty(mallRecyclingOrder.getPurchaseId())
        ) {
            mallPayOrderProducers.sendMsg(new RecycleMessage());
        }
    }
}
