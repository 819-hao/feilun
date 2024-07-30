package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.base.WNOUtil;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStock;
import com.seeease.flywheel.serve.goods.entity.ExtAttachmentStockRecord;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.BillLifeCycleService;
import com.seeease.flywheel.serve.purchase.convert.AttachmentStockRecordConverter;
import com.seeease.flywheel.serve.purchase.entity.*;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.SalesPriorityEnum;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseLineMapper;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseMapper;
import com.seeease.flywheel.serve.goods.mapper.ExtAttachmentStockMapper;
import com.seeease.flywheel.serve.goods.mapper.ExtAttachmentStockRecordMapper;
import com.seeease.flywheel.serve.goods.service.ExtAttachmentStockRecordService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @description 针对表【ext_attachment_stock_record(附件库存导入记录)】的数据库操作Service实现
 * @createDate 2023-09-25 09:54:52
 */
@Service
public class ExtAttachmentStockRecordServiceImpl extends ServiceImpl<ExtAttachmentStockRecordMapper, ExtAttachmentStockRecord>
        implements ExtAttachmentStockRecordService {
    @Resource
    private StockMapper stockMapper;
    @Resource
    private ExtAttachmentStockMapper extAttachmentStockMapper;
    @Resource
    private BillPurchaseMapper billPurchaseMapper;
    @Resource
    private BillPurchaseLineMapper billPurchaseLineMapper;
    @Resource
    private BillLifeCycleService billLifeCycleService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ExtAttachmentStockRecord> importCreate(List<AttachmentStockImportDto> dtoList) {
        List<ExtAttachmentStockRecord> recordList = new ArrayList<>();
        //分组新增
        dtoList.stream()
                .collect(Collectors.groupingBy(AttachmentStockImportDto::getCustomerId))
                .values()
                .forEach(lis -> {
                    Integer customerId = lis.get(0).getCustomerId();
                    Integer customerContactsId = lis.get(0).getCustomerContactsId();

                    BillPurchase billPurchase = new BillPurchase();
                    billPurchase.setSerialNo(SerialNoGenerator.generatePurchaseSerialNo());
                    billPurchase.setStoreId(UserContext.getUser().getStore().getId());
                    billPurchase.setPurchaseType(PurchaseTypeEnum.TH_CG);
                    billPurchase.setPurchaseMode(PurchaseModeEnum.BATCH);
                    billPurchase.setPurchaseSource(BusinessBillTypeEnum.TH_CG_PL);
                    billPurchase.setCustomerId(customerId);
                    billPurchase.setCustomerContactId(customerContactsId);
                    billPurchase.setPurchaseSubjectId(60); //指导杭州维修主体
                    billPurchase.setTotalPurchasePrice(lis.stream()
                            .map(AttachmentStockImportDto::getPurchasePrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    //采购状态，完成
                    billPurchase.setPurchaseState(BusinessBillStateEnum.COMPLETE);
                    //采购数量
                    billPurchase.setPurchaseNumber(lis.size());
                    billPurchase.setStoreTag(billPurchase.getStoreId().intValue() == FlywheelConstant._ZB_ID ? WhetherEnum.NO : WhetherEnum.YES);
                    //新增采购单
                    billPurchaseMapper.insert(billPurchase);


                    List<Stock> stockList = lis.stream()
                            .map(t -> {
                                String wno = WNOUtil.generateWNO();
                                Stock stock = AttachmentStockRecordConverter.INSTANCE.convertStock(t);
                                stock.setId(null);
                                stock.setStockStatus(StockStatusEnum.MARKETABLE);
                                stock.setSourceSubjectId(billPurchase.getPurchaseSubjectId());
                                stock.setBelongId(billPurchase.getPurchaseSubjectId());
                                stock.setStockSrc(billPurchase.getPurchaseSource().getValue());
                                //客户id
                                stock.setCcId(billPurchase.getCustomerId());
                                //是否允许低于b价销售
                                stock.setIsUnderselling(StockUndersellingEnum.ALLOW);
                                stock.setTotalPrice(t.getPurchasePrice()); //初始总成本为采购成本
                                stock.setUseConfig(0);
                                stock.setLockDemand(0);
                                stock.setIsRecycling(0);
                                stock.setDefectOrNot(0);
                                stock.setTagPrice(t.getConsignmentPrice());
                                stock.setTobPrice(t.getConsignmentPrice());
                                stock.setTocPrice(t.getConsignmentPrice());
                                stock.setLevel("压货");
                                stock.setStrapMaterial("其他");
                                stock.setSalesPriority(SalesPriorityEnum.TOC.getValue());
                                stock.setRightOfManagement(FlywheelConstant._ZB_RIGHT_OF_MANAGEMENT);
                                stock.setWno(wno);
                                stock.setAttachment(StringUtils.EMPTY);
                                stock.setFixPrice(BigDecimal.ZERO);
                                stock.setLocationId(FlywheelConstant._ZB_ID); //改变商品位置
                                stock.setRkTime(new Date());
                                stock.setStoreId(FlywheelConstant._ZB_ID); //改变仓库位置
                                stock.setRemark(t.getRemarks());

                                return stock;
                            })
                            .collect(Collectors.toList());

                    //新增库存信息
                    stockMapper.insertBatchSomeColumn(stockList);

                    //新增生命周期
                    billLifeCycleService.insertBatchSomeColumn(stockList.stream()
                            .map(t -> {
                                BillLifeCycle billLifeCycle = new BillLifeCycle();
                                billLifeCycle.setWno(t.getWno());
                                billLifeCycle.setStockId(t.getId());
                                billLifeCycle.setOriginSerialNo(billPurchase.getSerialNo());
                                billLifeCycle.setOperationDesc("配件采购入库");
                                billLifeCycle.setStoreId(billPurchase.getStoreId());
                                billLifeCycle.setOperationTime(System.currentTimeMillis());
                                return billLifeCycle;
                            })
                            .collect(Collectors.toList()));

                    //新增库存参数
                    Map<String, Stock> stockMap = stockList.stream()
                            .collect(Collectors.toMap(Stock::getSn, Function.identity()));

                    List<ExtAttachmentStock> extAttachmentStockList = lis.stream()
                            .filter(t -> StringUtils.isNotBlank(t.getColour())
                                    || StringUtils.isNotBlank(t.getSize())
                                    || StringUtils.isNotBlank(t.getGwModel())
                                    || StringUtils.isNotBlank(t.getMaterial()))
                            .map(t -> {
                                ExtAttachmentStock extAttachmentStock = AttachmentStockRecordConverter.INSTANCE.convertExtAttachmentStock(t);
                                Stock stock = stockMap.get(t.getStockSn());
                                extAttachmentStock.setStockId(stock.getId());
                                extAttachmentStock.setGoodsId(stock.getGoodsId());
                                return extAttachmentStock;
                            })
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(extAttachmentStockList)) {
                        //新增库存参数
                        extAttachmentStockMapper.insertBatchSomeColumn(extAttachmentStockList);
                    }
                    List<BillPurchaseLine> billPurchaseLines = stockList.stream().map(t -> {
                                BillPurchaseLine billPurchaseLine = AttachmentStockRecordConverter.INSTANCE.convertBillPurchaseLine(t);
                                billPurchaseLine.setIsCard(NumberUtils.INTEGER_ZERO);
                                billPurchaseLine.setPurchaseId(billPurchase.getId());//采购单id
                                billPurchaseLine.setPurchaseLineState(PurchaseLineStateEnum.WAREHOUSED);//采购行状态
                                return billPurchaseLine;
                            })
                            .collect(Collectors.toList());
                    //新增采购详情
                    billPurchaseLineMapper.insertBatchSomeColumn(billPurchaseLines);

                    //导入记录
                    ExtAttachmentStockRecord stockRecord = AttachmentStockRecordConverter.INSTANCE.convertRecord(billPurchase);
                    stockRecord.setCountNumber(billPurchaseLines.size());
                    recordList.add(stockRecord);
                });

        //新增导入记录
        baseMapper.insertBatchSomeColumn(recordList);

        return recordList;
    }

}




