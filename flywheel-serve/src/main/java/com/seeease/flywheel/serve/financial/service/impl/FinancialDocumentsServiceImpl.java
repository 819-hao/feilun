package com.seeease.flywheel.serve.financial.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.seeease.flywheel.financial.request.FinancialQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialExportResult;
import com.seeease.flywheel.financial.result.FinancialPageAllResult;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.StringTools;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.mapper.CustomerContactsMapper;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsOriginEnum;
import com.seeease.flywheel.serve.financial.mapper.FinancialDocumentsDetailMapper;
import com.seeease.flywheel.serve.financial.mapper.FinancialDocumentsMapper;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.financial.template.FinancialTemplate;
import com.seeease.flywheel.serve.goods.entity.StockPo;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.purchase.entity.*;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseReturnMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderLineStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderModeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleOrderMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderLineMapper;
import com.seeease.flywheel.serve.sale.mapper.BillSaleReturnOrderMapper;
import com.seeease.springframework.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【financial_documents(财务单据)】的数据库操作Service实现
 * @createDate 2023-03-27 09:52:56
 */
@Slf4j
@Service
public class FinancialDocumentsServiceImpl extends ServiceImpl<FinancialDocumentsMapper, FinancialDocuments>
        implements FinancialDocumentsService {
    @Resource
    private FinancialTemplate financialTemplate;
    @Resource
    private BillPurchaseReturnMapper purchaseReturnMapper;
    @Resource
    private BillPurchaseReturnLineMapper purchaseReturnLineMapper;
    @Resource
    private BillPurchaseMapper purchaseMapper;
    @Resource
    private BillPurchaseLineMapper purchaseLineMapper;
    @Resource
    private BillSaleOrderMapper saleOrderMapper;
    @Resource
    private BillSaleOrderLineMapper saleOrderLineMapper;
    @Resource
    private BillSaleReturnOrderMapper saleOrderReturnMapper;
    @Resource
    private BillSaleReturnOrderLineMapper saleReturnOrderLineMapper;
    @Resource
    private FinancialDocumentsDetailMapper financialDocumentsDetailMapper;
    @Resource
    private CustomerContactsMapper customerContactsMapper;
    @Resource
    private StockMapper stockMapper;
    @Resource
    private PurchaseSubjectService purchaseSubjectService;

    //用于判断采购财务单据是否前置生成
    private static final Set<Integer> TH_CG_PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.TH_CG_PL.getValue(),
            BusinessBillTypeEnum.CG_TH.getValue(), BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(),
            BusinessBillTypeEnum.TH_JS.getValue(), BusinessBillTypeEnum.GR_HS_ZH.getValue(), BusinessBillTypeEnum.GR_HS_JHS.getValue(),
            BusinessBillTypeEnum.TH_CG_QK.getValue(),BusinessBillTypeEnum.TH_CG_DJTP.getValue());
    private static final Set<Integer> GR_HG_PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.GR_HG_JHS.getValue(),
            BusinessBillTypeEnum.GR_HG_ZH.getValue());
    private static final Set<Integer> GR_JS_PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.GR_JS.getValue());

//    private static final Set<Integer> BATCH_PURCHASE_RETURN_GRJS_TYPE = ImmutableSet.of(BusinessBillTypeEnum.GR_JS_TH.getValue());
//    private static final Set<Integer> BATCH_PURCHASE_RETURN_THJS_TYPE = ImmutableSet.of(BusinessBillTypeEnum.TH_JS_TH.getValue());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSale(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        //查询数据

        BillSaleOrder saleOrder = saleOrderMapper.selectById(financialGenerateDto.getId());
        List<FinancialDocumentsDetail> documentsDetails = financialDocumentsDetailMapper
                .selectListBySerialNumberAndStockIds(saleOrder.getSerialNo(), financialGenerateDto.getStockList());
        if (documentsDetails.size() > 0) {
            log.info("销售财务单已经生成过! {}", JSONObject.toJSONString(financialGenerateDto));
            return;
        }

        FinancialSalesDto dto = new FinancialSalesDto();
        dto.setSaleId(saleOrder.getId());
        dto.setSerialNumber(saleOrder.getSerialNo());
        dto.setAssocSerialNumber(saleOrder.getSerialNo());
        dto.setCreateBy(saleOrder.getCreatedBy());
        dto.setCreateTime(new Date());
        dto.setClcId(saleOrder.getSaleChannel().getValue());
        dto.setThirdNumber(saleOrder.getBizOrderCode());
        dto.setSaleLocationId(saleOrder.getShopId());
        //dto.setDivideInto((saleOrder.getDeliveryLocationId() == 1 || Objects.equals(saleOrder.getDeliveryLocationId(), saleOrder.getShopId())) ? 0 : 1);
        dto.setSaleType(saleOrder.getSaleType().getValue());
        dto.setSaleMode(SaleOrderModeEnum.convert(saleOrder.getSaleMode().getValue()).getValue());

        List<BillSaleOrderLine> lines = saleOrderLineMapper.selectList(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getSaleId, saleOrder.getId())
                .in(BillSaleOrderLine::getStockId, financialGenerateDto.getStockList())
                .in(BillSaleOrderLine::getSaleLineState, Lists.newArrayList(SaleOrderLineStateEnum.DELIVERED, SaleOrderLineStateEnum.CONSIGNMENT_SETTLED)));
        if (SaleOrderModeEnum.CONSIGN_FOR_SALE.equals(saleOrder.getSaleMode())) {
            dto.setCreateBy(lines.stream().findFirst().get().getConsignmentSettlementOperator());
        }
        dto.setDivideInto(lines.stream().allMatch(Objects::isNull) ? 0 : 1);
        //根据saleId 和 stockList 查询出销售详情数据
        Map<Integer, BillSaleOrderLine> lineMap = lines
                .stream().collect(Collectors.toMap(BillSaleOrderLine::getStockId, Function.identity(), (k1, k2) -> k2));
        dto.setLineMap(lineMap);
        //注意 这个只是销售单里的客户 id 其他的 像物鱼 门店的 customerid 需要重新查询赋值
        CustomerContacts customerContacts = customerContactsMapper.selectById(saleOrder.getCustomerContactId());
        dto.setCustomerId(customerContacts.getCustomerId());

        List<StockPo> stockList = stockMapper.selectStockListByIds(new ArrayList<>(lineMap.keySet()));
        dto.setStockList(stockList);

        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();
        // step0:生成采购主体对物鱼总部的财务单
        dataList.addAll(financialTemplate.generatePurchaseAllocation(dto));
        //step1:生成销售调拨财务单
        dataList.addAll(financialTemplate.generateSaleAllocation(dto));
        //step2:生成销售单
        dataList.addAll(financialTemplate.generateSale(dto));
        //step3:生成销售服务费相关财务单
        dataList.addAll(financialTemplate.generateSaleServiceFee(dto));
        //step4:保存单据
        this.save(dataList);

        log.info("销售财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSaleReturn(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        //查询数据
        BillSaleReturnOrder returnOrder = saleOrderReturnMapper.selectById(financialGenerateDto.getId());
        List<FinancialDocumentsDetail> documentsDetails = financialDocumentsDetailMapper
                .selectListBySerialNumberAndStockIds(returnOrder.getSerialNo(), financialGenerateDto.getStockList());
        if (documentsDetails.size() > 0) {
            log.info("销售退货财务单已经生成过! {}", JSONObject.toJSONString(financialGenerateDto));
            return;
        }
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();

        BillSaleOrder saleOrder = saleOrderMapper.selectById(returnOrder.getSaleId());

        FinancialSalesReturnDto dto = new FinancialSalesReturnDto();
        dto.setSerialNumber(returnOrder.getSerialNo());
        dto.setAssocSerialNumber(returnOrder.getSerialNo());
        dto.setCreateBy(returnOrder.getCreatedBy());
        dto.setCreateTime(new Date());
        dto.setSaleReturnType(returnOrder.getSaleReturnType().getValue());
        dto.setClcId(saleOrder.getSaleChannel().getValue());
        dto.setThirdNumber(returnOrder.getBizOrderCode());
        dto.setSaleLocationId(returnOrder.getShopId());
        dto.setCustomerId(returnOrder.getCustomerId());
        dto.setSaleReturnType(returnOrder.getSaleReturnType().getValue());
        dto.setSaleMode(SaleOrderModeEnum.convert(saleOrder.getSaleMode().getValue()).getValue());

        List<BillSaleOrderLine> lines = saleOrderLineMapper.selectList(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getSaleId, returnOrder.getSaleId())
                .in(BillSaleOrderLine::getStockId, financialGenerateDto.getStockList()));
        Map<Integer, BillSaleOrderLine> lineMap = lines
                .stream().collect(Collectors.toMap(BillSaleOrderLine::getStockId,
                        Function.identity(), (k1, k2) -> k2));
        dto.setLineMap(lineMap);


        List<BillSaleReturnOrderLine> returnOrderLines = saleReturnOrderLineMapper.selectList(new LambdaQueryWrapper<BillSaleReturnOrderLine>()
                .eq(BillSaleReturnOrderLine::getSaleReturnId, returnOrder.getId())
                .in(BillSaleReturnOrderLine::getStockId, financialGenerateDto.getStockList()));
        Map<Integer, BillSaleReturnOrderLine> returnOrderLineMap = returnOrderLines
                .stream().collect(Collectors.toMap(BillSaleReturnOrderLine::getStockId,
                        Function.identity(), (k1, k2) -> k2));
        dto.setReturnLineMap(returnOrderLineMap);

        //根据 采购主体分组
        List<StockPo> stockList = stockMapper.selectStockListByIds(new ArrayList<>(lineMap.keySet()))
                .stream().filter(stockPo -> StockStatusEnum.SOLD_OUT.equals(stockPo.getStockStatus())).collect(Collectors.toList());
        dto.setStockList(stockList);

        // 物鱼 和 采购主体 调入调出
        dataList.addAll(financialTemplate.generatePurchaseReturnAllocation(dto));
        //step2:生成销售退货单
        dataList.addAll(financialTemplate.generateSaleReturn(dto));
        //step3:生成销售退货服务费相关财务单
        dataList.addAll(financialTemplate.generateSaleReturnServiceFee(dto));

        //查询商品是否是个人寄售商品
        List<StockPo> list = stockList.stream().filter(stockPo -> GR_JS_PURCHASE_TYPE.contains(stockPo.getStockSrc())).collect(Collectors.toList());

        //step1:生成销售退货调拨财务单
        dataList.addAll(financialTemplate.generateSaleReturnAllocation(dto));
        //个人寄售商品 特殊逻辑
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(stockPo -> {
                //当前是销售单的采购退货 没有生成采购退货的业务单 取原采购单
                FinancialPurchaseReturnDto fprd = new FinancialPurchaseReturnDto();
                BillPurchaseLine billPurchaseLine = purchaseLineMapper.selectOne(new LambdaQueryWrapper<BillPurchaseLine>()
                        .eq(BillPurchaseLine::getStockId, stockPo.getId()));
                BillPurchase purchase = purchaseMapper.selectById(billPurchaseLine.getPurchaseId());
                fprd.setSerialNumber(SerialNoGenerator.generatePurchaseReturnSerialNo());
                fprd.setAssocSerialNumber(fprd.getSerialNumber());
                fprd.setCreateBy(purchase.getCreatedBy());
                fprd.setCreateTime(new Date());
                fprd.setSaleMode(BusinessBillTypeEnum.convertMode(stockPo.getStockSrc()).getValue());
                fprd.setCustomerId(purchase.getCustomerId());
                fprd.setStockList(Lists.newArrayList(stockPo));
                //对个人客户进行个人采购退货
                dataList.addAll(financialTemplate.generatePurchaseReturn(fprd));
            });
        }

        //step4:保存单据
        this.save(dataList);

        log.info("销售退货财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatePurchase(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        //查询数据
        BillPurchase purchase = purchaseMapper.selectById(financialGenerateDto.getId());
        List<FinancialDocumentsDetail> documentsDetails = financialDocumentsDetailMapper
                .selectListBySerialNumberAndStockIds(purchase.getSerialNo(), financialGenerateDto.getStockList());
        if (documentsDetails.size() > 0) {
            log.info("采购财务单已经生成过! {}", JSONObject.toJSONString(financialGenerateDto));
            return;
        }
        AtomicInteger num = new AtomicInteger(1);
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();
        List<StockPo> stockList = stockMapper.selectStockListByIds(financialGenerateDto.getStockList());
        Map<Integer, List<StockPo>> stockMap = stockList.stream().collect(Collectors.groupingBy(StockPo::getStockSrc));
        stockMap.forEach((stockSrc, list) -> {
            FinancialPurchaseDto dto = new FinancialPurchaseDto();
            dto.setSerialNumber(StringTools.dataSplicing(purchase.getSerialNo(), num.getAndIncrement()) + "-" + list.get(0).getId());
            dto.setAssocSerialNumber(dto.getSerialNumber());
            dto.setCreateBy(purchase.getCreatedBy());
            dto.setCreateTime(new Date());
            dto.setSaleMode(PurchaseModeEnum.convert(purchase.getPurchaseMode().getValue()).getValue());
            dto.setCustomerId(purchase.getCustomerId());
            dto.setDemandId(purchase.getDemanderStoreId());
            dto.setOrderOrigin(BusinessBillTypeEnum.convertOrigin(purchase.getPurchaseSource().getValue()).getValue());

            dto.setStockList(list);

            // step1:生成采购财务单
            dataList.addAll(financialTemplate.generatePurchase(dto));
            // step2:如果是回购回收的采购 需要生成回购服务费单子
            if (GR_HG_PURCHASE_TYPE.contains(purchase.getPurchaseSource().getValue())) {
                List<BillPurchaseLine> lines = purchaseLineMapper.selectList(new LambdaQueryWrapper<BillPurchaseLine>()
                        .eq(BillPurchaseLine::getPurchaseId, purchase.getId())
                        .in(BillPurchaseLine::getStockId, financialGenerateDto.getStockList()));
                Map<Integer, BigDecimal> map = lines.stream().collect(Collectors.toMap(BillPurchaseLine::getStockId, BillPurchaseLine::getRecycleServePrice));
                dto.setServiceFeeMap(map);
                dto.setSerialNumber(SerialNoGenerator.generateFWSRSerialNo());
                dataList.addAll(financialTemplate.generatePurchaseServiceFee(dto));
            }
        });
        this.save(dataList);
        log.info("采购财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }

    @Override
    public void generatePurchaseQtReturn(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        if (!(BusinessBillTypeEnum.TH_CG_PL.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_DJ.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.GR_HS_ZH.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.GR_HS_JHS.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_BH.getValue().equals(financialGenerateDto.getType())) ||
                BusinessBillTypeEnum.TH_CG_QK.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_DJTP.getValue().equals(financialGenerateDto.getType()))
            return;
        //确定只有一个表 的情况下这样做 多个表 需要改
        BillPurchaseLine purchaseLine = purchaseLineMapper.selectOne(new LambdaQueryWrapper<BillPurchaseLine>()
                .eq(BillPurchaseLine::getPurchaseId, financialGenerateDto.getId())
                .in(BillPurchaseLine::getStockId, financialGenerateDto.getStockList()));
        if (!PurchaseLineStateEnum.IN_RETURN.equals(purchaseLine.getPurchaseLineState()))
            return;
        BillPurchase purchase = purchaseMapper.selectById(financialGenerateDto.getId());
        FinancialPurchaseReturnDto dto = new FinancialPurchaseReturnDto();
        dto.setSerialNumber(SerialNoGenerator.generatePurchaseReturnSerialNo());
        dto.setAssocSerialNumber(dto.getSerialNumber());
        dto.setCreateBy(purchase.getCreatedBy());
        dto.setCreateTime(new Date());
        dto.setSaleMode(PurchaseModeEnum.convert(purchase.getPurchaseMode().getValue()).getValue());
        dto.setCustomerId(purchase.getCustomerId());
        dto.setOrderOrigin(BusinessBillTypeEnum.convertOrigin(purchase.getPurchaseSource().getValue()).getValue());
        List<StockPo> stockList = stockMapper.selectStockListByIds(financialGenerateDto.getStockList());
        dto.setStockList(stockList);

        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();
        // step1:生成采购退货财务单
        dataList.addAll(financialTemplate.generatePurchaseReturn(dto));
        //step1:保存单据
        this.save(dataList);

        log.info("采购质检-采购退货财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }

    @Override
    public void generatePurchaseMarginCover(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        if (!(BusinessBillTypeEnum.TH_CG_PL.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_DJ.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_BH.getValue().equals(financialGenerateDto.getType())) ||
                BusinessBillTypeEnum.TH_CG_QK.getValue().equals(financialGenerateDto.getType()) ||
                BusinessBillTypeEnum.TH_CG_DJTP.getValue().equals(financialGenerateDto.getType()))
            return;
        //确定只有一个表 的情况下这样做 多个表 需要改
//        BillPurchaseLine purchaseLine = purchaseLineMapper.selectOne(new LambdaQueryWrapper<BillPurchaseLine>()
//                .eq(BillPurchaseLine::getPurchaseId, financialGenerateDto.getId())
//                .in(BillPurchaseLine::getStockId, financialGenerateDto.getStockList()));
//        if (!PurchaseLineStateEnum.WAREHOUSED.equals(purchaseLine.getPurchaseLineState()))
//            return;
        BillPurchase purchase = purchaseMapper.selectById(financialGenerateDto.getId());
        FinancialPurchaseDto dto = new FinancialPurchaseDto();
        dto.setSerialNumber(SerialNoGenerator.generatePurchaseSerialNo());
        dto.setAssocSerialNumber(dto.getSerialNumber());
        dto.setCreateBy(purchase.getCreatedBy());
        dto.setCreateTime(new Date());
        dto.setSaleMode(FinancialDocumentsModeEnum.PURCHASE_MARGIN_COVER.getValue());
        dto.setCustomerId(purchase.getCustomerId());
        dto.setOrderOrigin(FinancialDocumentsOriginEnum.CG_ZR.getValue());
        List<StockPo> stockList = stockMapper.selectStockListByIds(financialGenerateDto.getStockList());
        dto.setStockList(stockList);
        dto.setStockMap(purchaseLineMapper.selectByPurchaseId(purchase.getId()).stream().filter(a -> Objects.nonNull(a.getOldPurchasePrice()))
                .collect(Collectors.toMap(BillPurchaseLineDetailsVO::getStockId,BillPurchaseLineDetailsVO::getOldPurchasePrice)));
        // step1:生成采购退货财务单
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>(financialTemplate.generatePurchaseMarginCover(dto));
        //step1:保存单据
        this.save(dataList);

        log.info("采购质检-采购退货财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generatePurchaseReturn(FinancialGenerateDto financialGenerateDto) {
        if (ObjectUtils.isEmpty(financialGenerateDto.getStockList()) || ObjectUtils.isEmpty(financialGenerateDto.getId()))
            return;
        if (!TH_CG_PURCHASE_TYPE.contains(financialGenerateDto.getType()))
            return;
        //查询数据
        BillPurchaseReturn purchaseReturn = purchaseReturnMapper.selectById(financialGenerateDto.getId());

        List<FinancialDocumentsDetail> documentsDetails = financialDocumentsDetailMapper
                .selectListBySerialNumberAndStockIds(purchaseReturn.getSerialNo(), financialGenerateDto.getStockList());
        if (documentsDetails.size() > 0) {
            log.info("采购退货财务单已经生成过! {}", JSONObject.toJSONString(financialGenerateDto));
            return;
        }
        AtomicInteger num = new AtomicInteger(1);
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();

        //根据销售单的不同类型做分组
        List<BillPurchaseReturnLine> returnLines = purchaseReturnLineMapper.selectList(new LambdaQueryWrapper<BillPurchaseReturnLine>()
                .eq(BillPurchaseReturnLine::getPurchaseReturnId, purchaseReturn.getId())
                .in(BillPurchaseReturnLine::getStockId, financialGenerateDto.getStockList()));
        for (BillPurchaseReturnLine billPurchaseReturnLine : returnLines) {
            if (BusinessBillTypeEnum.TH_JS.getValue().equals(billPurchaseReturnLine.getPurchaseReturnType().getValue())) {
                Long count = financialDocumentsDetailMapper.selectCount(new LambdaQueryWrapper<FinancialDocumentsDetail>()
                        .eq(FinancialDocumentsDetail::getStockId, billPurchaseReturnLine.getStockId()));
                if (count == 0L)
                    continue;
            }
            FinancialPurchaseReturnDto dto = new FinancialPurchaseReturnDto();
            dto.setSerialNumber(StringTools.dataSplicing(purchaseReturn.getSerialNo(), num.getAndIncrement()) + "-" + billPurchaseReturnLine.getStockId());
            dto.setAssocSerialNumber(purchaseReturn.getSerialNo());
            dto.setCreateBy(purchaseReturn.getCreatedBy());
            dto.setCreateTime(new Date());
            dto.setSaleMode(BusinessBillTypeEnum.convertMode(billPurchaseReturnLine.getPurchaseReturnType().getValue()).getValue());
            dto.setCustomerId(purchaseReturn.getCustomerId());
            dto.setOrderOrigin(BusinessBillTypeEnum.convertOrigin(billPurchaseReturnLine.getPurchaseReturnType().getValue()).getValue());

            List<StockPo> stockList = stockMapper.selectStockListByIds(Lists.newArrayList(billPurchaseReturnLine.getStockId()));
            dto.setStockList(stockList);

            // step1:生成采购退货财务单
            dataList.addAll(financialTemplate.generatePurchaseReturn(dto));
        }

        //step5:保存单据
        this.save(dataList);

        log.info("采购退货财务单新增成功! {}", JSONObject.toJSONString(financialGenerateDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSaleBalance(FinancialGenerateDto financialGenerateDto) {
        BillSaleOrder saleOrder = saleOrderMapper.selectById(financialGenerateDto.getId());
        List<BillSaleOrderLine> lines = saleOrderLineMapper.selectList(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getSaleId, financialGenerateDto.getId())
                .in(BillSaleOrderLine::getStockId, financialGenerateDto.getStockList()));
        Map<Integer, BillSaleOrderLine> map = lines
                .stream().collect(Collectors.toMap(BillSaleOrderLine::getStockId,
                        Function.identity(), (k1, k2) -> k2));
        //根据 采购主体分组
        List<StockPo> stockList = stockMapper.selectStockListByIds(financialGenerateDto.getStockList());

        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();

        //天猫结算首先创建本身销售退货
        FinancialSalesReturnDto fsrd = new FinancialSalesReturnDto();
        String returnOrderSerialNo = SerialNoGenerator.generateToCSaleReturnOrderSerialNo();
        fsrd.setSerialNumber(returnOrderSerialNo);
        fsrd.setAssocSerialNumber(returnOrderSerialNo);
        fsrd.setCreateBy(saleOrder.getCreatedBy());
        fsrd.setCreateTime(new Date());
        fsrd.setClcId(saleOrder.getSaleChannel().getValue());
        fsrd.setThirdNumber(saleOrder.getBizOrderCode());
        fsrd.setSaleLocationId(saleOrder.getShopId());
        fsrd.setCustomerId(saleOrder.getCustomerId());
        fsrd.setSaleMode(SaleOrderModeEnum.convert(saleOrder.getSaleMode().getValue()).getValue());
        fsrd.setSaleReturnType(SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue());
        //fsrd.setDivideInto((saleOrder.getDeliveryLocationId() == 1 || Objects.equals(saleOrder.getDeliveryLocationId(), saleOrder.getShopId())) ? 0 : 1);
        fsrd.setLineMap(map);
        fsrd.setStockList(stockList);
        fsrd.setDivideInto(lines.stream().allMatch(Objects::isNull)  ? 0 : 1);
        //step1:生成销售退货调拨财务单
        dataList.addAll(financialTemplate.generateSaleReturnAllocation(fsrd));
        //step2:生成销售退货单
        dataList.addAll(financialTemplate.generateSaleReturn(fsrd));
        //step3:生成销售退货服务费相关财务单
        dataList.addAll(financialTemplate.generateSaleReturnServiceFee(fsrd));

        //天猫结算 对流转人员 的销售单
        FinancialSalesDto fsd = new FinancialSalesDto();
        fsd.setSerialNumber(SerialNoGenerator.generateToCSaleOrderSerialNo());
        fsd.setAssocSerialNumber(fsd.getSerialNumber());
        fsd.setCreateBy(saleOrder.getCreatedBy());
        fsd.setCreateTime(new Date());
        fsd.setThirdNumber(saleOrder.getBizOrderCode());
        fsd.setSaleLocationId(saleOrder.getShopId());
        fsd.setSaleType(saleOrder.getSaleType().getValue());
        fsd.setBelongId(stockList.get(0).getSourceSubjectId());
        fsd.setSaleMode(SaleOrderModeEnum.convert(saleOrder.getSaleMode().getValue()).getValue());
//        fsd.setDivideInto((saleOrder.getDeliveryLocationId() == 1 || Objects.equals(saleOrder.getDeliveryLocationId(), saleOrder.getShopId())) ? 0 : 1);
        fsd.setDivideInto(lines.stream().allMatch(Objects::isNull)  ? 0 : 1);
        //注意 这个只是销售单里的客户 id 其他的 像物鱼 门店的 customerid 需要重新查询赋值
        fsd.setCustomerId(saleOrder.getTransferCustomerId());
        fsd.setLineMap(map);
        //根据 采购主体分组
        fsd.setStockList(stockList);
        dataList.addAll(financialTemplate.generateSale(fsd));

        PurchaseSubject purchaseSubject = purchaseSubjectService.selectPurchaseSubjectByName(SeeeaseConstant.TJ_XXY);

        //接下来的都是以天津稀小蜴为主体
        stockList.forEach(stockPo -> stockPo.setSourceSubjectId(purchaseSubject.getId()));
        //天猫结算 个人回收
        FinancialPurchaseDto fpd = new FinancialPurchaseDto();
        fpd.setSerialNumber(SerialNoGenerator.generatePurchaseSerialNo());
        fpd.setAssocSerialNumber(fpd.getSerialNumber());
        fpd.setCreateBy(saleOrder.getCreatedBy());
        fpd.setClcId(saleOrder.getSaleChannel().getValue());
        fpd.setCreateTime(new Date());
        fpd.setCustomerId(saleOrder.getTransferCustomerId());
        fpd.setDemandId(SeeeaseConstant._ZB_ID);
        //根据 采购主体分组
        fpd.setStockList(stockList);
        fpd.setOrderOrigin(BusinessBillTypeEnum.convertOrigin(BusinessBillTypeEnum.GR_HS_JHS.getValue()).getValue());
        // step1:生成采购财务单
        dataList.addAll(financialTemplate.generatePurchase(fpd));


        //天猫结算 天津稀小蜴 对客户销售
        //查询数据
        FinancialSalesDto dto = new FinancialSalesDto();
        dto.setSerialNumber(SerialNoGenerator.generateToCSaleOrderSerialNo());
        dto.setAssocSerialNumber(dto.getSerialNumber());
        dto.setCreateBy(saleOrder.getCreatedBy());
        dto.setCreateTime(new Date());
        dto.setClcId(saleOrder.getSaleChannel().getValue());
        dto.setThirdNumber(saleOrder.getBizOrderCode());
        dto.setSaleLocationId(saleOrder.getShopId());
        dto.setSaleType(saleOrder.getSaleType().getValue());
        fsd.setSaleMode(SaleOrderModeEnum.convert(saleOrder.getSaleMode().getValue()).getValue());
//        dto.setDivideInto((saleOrder.getDeliveryLocationId() == FlywheelConstant._ZB_ID || Objects.equals(saleOrder.getDeliveryLocationId(), saleOrder.getShopId())) ? 0 : 1);
        dto.setDivideInto(lines.stream().allMatch(Objects::isNull)  ? 0 : 1);
        //注意 这个只是销售单里的客户 id 其他的 像物鱼 门店的 customerid 需要重新查询赋值
        dto.setCustomerId(saleOrder.getCustomerId());
        dto.setLineMap(map);
        //根据 采购主体分组
        dto.setStockList(stockList);

        // step2:生成采购主体对物鱼总部的财务单
        dataList.addAll(financialTemplate.generatePurchaseAllocation(dto));
        //step1:生成销售调拨财务单
        dataList.addAll(financialTemplate.generateSaleAllocation(dto));
        //step2:生成销售单
        dataList.addAll(financialTemplate.generateSale(dto));
        //step3:生成销售服务费相关财务单
        dataList.addAll(financialTemplate.generateSaleServiceFee(dto));

        //step5:保存单据
        this.save(dataList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateSaleReturnBalance(FinancialGenerateDto financialGenerateDto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList = new LinkedList<>();
        BillSaleOrder saleOrder = saleOrderMapper.selectById(financialGenerateDto.getId());
        //根据 采购主体分组
        List<StockPo> stockList = stockMapper.selectStockListByIds(financialGenerateDto.getStockList());
        List<BillSaleOrderLine> lines = saleOrderLineMapper.selectList(new LambdaQueryWrapper<BillSaleOrderLine>()
                .eq(BillSaleOrderLine::getSaleId, financialGenerateDto.getId())
                .in(BillSaleOrderLine::getStockId, financialGenerateDto.getStockList()));
        Map<Integer, BillSaleOrderLine> map = lines
                .stream().collect(Collectors.toMap(BillSaleOrderLine::getStockId,
                        Function.identity(), (k1, k2) -> k2));
        PurchaseSubject purchaseSubject = purchaseSubjectService.selectPurchaseSubjectByName(SeeeaseConstant.TJ_XXY);

        //天猫结算 采购主体对 流转人员的销售退货
        FinancialSalesReturnDto fsrd = new FinancialSalesReturnDto();
        fsrd.setSerialNumber(SerialNoGenerator.generateToCSaleReturnOrderSerialNo());
        fsrd.setAssocSerialNumber(fsrd.getSerialNumber());
        fsrd.setCreateBy(saleOrder.getCreatedBy());
        fsrd.setCreateTime(new Date());
        fsrd.setClcId(saleOrder.getSaleChannel().getValue());
        fsrd.setThirdNumber(saleOrder.getBizOrderCode());
        fsrd.setSaleLocationId(saleOrder.getShopId());
        fsrd.setCustomerId(saleOrder.getTransferCustomerId());
        fsrd.setLineMap(map);
        fsrd.setStockList(stockList);
        fsrd.setBelongId(stockList.get(0).getSourceSubjectId());
        fsrd.setSaleReturnType(SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue());

        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> saleReturnList = financialTemplate.generateSaleReturn(fsrd);

        // 对天猫结算后的单子 个人销售进行退货
        stockList.forEach(stockPo -> stockPo.setSourceSubjectId(purchaseSubject.getId()));

        FinancialSalesReturnDto fsr = new FinancialSalesReturnDto();
        fsr.setSerialNumber(SerialNoGenerator.generateToCSaleReturnOrderSerialNo());
        fsr.setAssocSerialNumber(fsr.getSerialNumber());
        fsr.setCreateBy(saleOrder.getCreatedBy());
        fsr.setCreateTime(new Date());
        fsr.setClcId(saleOrder.getSaleChannel().getValue());
        fsr.setThirdNumber(saleOrder.getBizOrderCode());
        fsr.setSaleLocationId(saleOrder.getShopId());
        fsr.setCustomerId(saleOrder.getCustomerId());
        fsr.setLineMap(map);
        fsr.setStockList(stockList);
        fsr.setBelongId(stockList.get(0).getSourceSubjectId());
        fsr.setSaleReturnType(SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue());

//        // step0:生成供应商对采购主体的财务单子 同行寄售不需要生成 个人寄售要生成
//        dataList.addAll(financialTemplate.generatePurchaseReturn(fsr));
//        // step1:生成销售退货采购财务单
//        dataList.addAll(financialTemplate.generateSaleReturnPurchase(fsr));
        dataList.addAll(financialTemplate.generatePurchaseReturnAllocation(fsr));
        //step2:生成销售退货调拨财务单
        dataList.addAll(financialTemplate.generateSaleReturnAllocation(fsr));
        //step3:生成销售退货单
        dataList.addAll(financialTemplate.generateSaleReturn(fsr));
        //step4:生成销售退货服务费相关财务单
        dataList.addAll(financialTemplate.generateSaleReturnServiceFee(fsr));

        //天猫结算 个人回收 退货
        FinancialPurchaseReturnDto fprd = new FinancialPurchaseReturnDto();
        fprd.setSerialNumber(SerialNoGenerator.generatePurchaseReturnSerialNo());
        fprd.setAssocSerialNumber(fprd.getSerialNumber());
        fprd.setCreateBy(saleOrder.getCreatedBy());
        fprd.setCreateTime(new Date());
        fprd.setClcId(saleOrder.getSaleChannel().getValue());
        fprd.setCustomerId(saleOrder.getCustomerId());
        fprd.setStockList(stockList);
        fprd.setOrderOrigin(BusinessBillTypeEnum.convertOrigin(BusinessBillTypeEnum.GR_HS_JHS.getValue()).getValue());
        // step1:生成采购财务单
        dataList.addAll(financialTemplate.generatePurchaseReturn(fprd));
        dataList.addAll(saleReturnList);

        //step5:保存单据
        this.save(dataList);
    }

    @Override
    public Page<FinancialPageAllResult> selectByFinancialQueryAllRequest(FinancialQueryAllRequest request) {
        return this.baseMapper.selectByFinancialQueryAllRequest(new Page(request.getPage(), request.getLimit()), request);
    }

    @Override
    public List<FinancialExportResult> selectExcelByFinancialDocumentsQueryDto(FinancialQueryAllRequest request) {
        return this.baseMapper.selectExcelByFinancialDocumentsQueryDto(request);
    }

    /**
     * 保存单据
     *
     * @param dataList
     */
    private void save(LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            log.error("财务单据为空");
            return;
//            throw new IllegalArgumentException("财务单据不能为空");
        }
        for (Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> t : dataList) {
            FinancialDocuments documents = t.getV1();
            if (1 != baseMapper.insert(documents)) {
                throw new RuntimeException("财务单新增失败");
            }
            for (FinancialDocumentsDetail details : t.getV2()) {
                //关联主健
                details.setFinancialDocumentsId(documents.getId());
                //新增
                if (1 != financialDocumentsDetailMapper.insert(details)) {
                    throw new RuntimeException("财务单详情新增失败");
                }
            }
        }
    }
}




