package com.seeease.flywheel.serve.financial.template;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.mapper.CustomerMapper;
import com.seeease.flywheel.serve.financial.convert.FinancialDocumentsConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsOriginEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsTypeEnum;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockPo;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.mapper.PurchaseSubjectMapper;
import com.seeease.flywheel.serve.maindata.mapper.StoreRelationshipSubjectMapper;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderTypeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.springframework.Tuple2;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author edy
 * @date 2022/9/22
 */
@Component
public class FinancialTemplateImpl implements FinancialTemplate {

    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private PurchaseSubjectMapper subjectMapper;
    @Resource
    private StoreRelationshipSubjectMapper storeRelationshipSubjectMapper;
    @Resource
    private StoreRelationshipSubjectMapper relationshipSubjectMapper;


    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleAllocation(FinancialSalesDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> rofMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        for (List<StockPo> stockList : rofMap.values()) {
            //查询销售方的采购主体
            StoreRelationshipSubject customerSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                    .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
            if (Objects.isNull(customerSubject))
                throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
            String customerSubjectName = subjectMapper.selectNameById(customerSubject.getSubjectId());
            Customer customer = customerMapper.queryByName(customerSubjectName);
            if (Objects.isNull(customer))
                throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_NOT_EXIT);
            //查询出小蜴总部(物鱼)的采购主体
            PurchaseSubject supplierSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
            Customer supplier = customerMapper.queryByName(supplierSubject.getName());

            Map<Integer, List<StockPo>> stockMap = stockList.stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
            for (List<StockPo> list : stockMap.values()) {

                //物鱼对销售方的寄售调出单
                FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DC.getValue());
                fdOut.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
                fdOut.setSaleMode(null);
                fdOut.setCustomerId(customer.getId());
                fdOut.setBelongId(supplierSubject.getId());
                fdOut.setSerialNumber(SerialNoGenerator.generateJSDCSerialNo());

                List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(list);
                fdOut.setOrderNumber(fddOutList.size());
                fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
                linkedList.add(tuple2);

                //销售方对物鱼的寄售调入单
                FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DR.getValue());
                fdIn.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
                fdIn.setSaleMode(null);
                fdIn.setCustomerId(supplier.getId());
                fdIn.setBelongId(customerSubject.getSubjectId());
                fdIn.setSerialNumber(SerialNoGenerator.generateJSDRSerialNo());

                List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(list);
                fdIn.setOrderNumber(fddInList.size());
                fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
                linkedList.add(tuple2Out);
            }
        }
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSale(FinancialSalesDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, BillSaleOrderLine> lineMap = dto.getLineMap();
        Map<Integer, List<StockPo>> map = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
        StoreRelationshipSubject saleSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
        if (Objects.isNull(saleSubject))
            throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
        for (List<StockPo> list : map.values()) {
            boolean whetherGR = Objects.equals(dto.getSaleType(), SaleOrderTypeEnum.TO_C_XS.getValue());
            //门店对客户的销售单
            FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fd.setOrderOrigin(whetherGR ?
                    FinancialDocumentsOriginEnum.GR_XS.getValue() : FinancialDocumentsOriginEnum.TH_XS.getValue());
            fd.setOrderType(FinancialDocumentsTypeEnum.XS.getValue());
            fd.setBelongId(saleSubject.getSubjectId());
            fd.setThirdNumber(dto.getThirdNumber());
            fd.setSerialNumber(dto.getSerialNumber() + "-" + list.get(0).getId() + "-" +
                    list.stream().map(Stock::getRightOfManagement).filter(Objects::nonNull).findFirst().orElse(0));

            List<FinancialDocumentsDetail> fddList = getFinancialDocumentsDetails(list);

            fddList.forEach(detail -> {
                BillSaleOrderLine orderLine = lineMap.getOrDefault(detail.getStockId(), new BillSaleOrderLine());
                detail.setPurchasePrice(null);
                detail.setClinchPrice(orderLine.getClinchPrice());
                detail.setJsClinchPrice(orderLine.getPreClinchPrice());
                detail.setMarginPrice(orderLine.getMarginPrice());
                detail.setDivideInto(fd.getDivideInto());
                detail.setPromotionConsignmentPrice(orderLine.getPromotionConsignmentPrice());
                if (whetherGR) {
                    detail.setFinancialPerformance(detail.getClinchPrice().compareTo(detail.getTocPrice()) >= 0 ?
                            detail.getClinchPrice() : detail.getTocPrice());
                    detail.setBrandMarketingExpenses(detail.getTocPrice().compareTo(detail.getClinchPrice()) > 0 ?
                            detail.getTocPrice().subtract(detail.getClinchPrice()) : null);
                }
            });
            fd.setOrderNumber(fddList.size());
            fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getClinchPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fd, fddList);
            linkedList.add(tuple2);
        }
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleServiceFee(FinancialSalesDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, BillSaleOrderLine> lineMap = dto.getLineMap();

        //查询出小蜴总部(物鱼)的采购主体 经营权值
        PurchaseSubject supplierSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
        //查出销售方的经营权值
        //查询销售方的采购主体
        StoreRelationshipSubject saleSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
        if (Objects.isNull(saleSubject))
            throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
        String customerSubjectName = subjectMapper.selectNameById(saleSubject.getSubjectId());
        Customer saller = customerMapper.queryByName(customerSubjectName);
        if (Objects.isNull(saller))
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_NOT_EXIT);
        //TODO 对 经营权 是物鱼的 并且 经营权是自己门店的进行过滤 并且 添加特殊 判断 南宁一二店不需要分成
        List<StockPo> stockList = dto.getStockList().stream().filter(stock ->
                checkDivideInto(stock, supplierSubject, saleSubject)).collect(Collectors.toList());

        //有可能一个订单里有不同经营权的商品 所以需要对 商品进行经营权分组
        Map<Integer, List<StockPo>> map = stockList.stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));

        for (List<StockPo> list : map.values()) {
            Map<Integer, List<StockPo>> stockMap = list.stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
            stockMap.forEach((rightOfManagement, stockPoList) -> {

                //查出来经营权的名字
                String rightOrManagementName = subjectMapper.selectNameById(rightOfManagement);
                Customer customer = customerMapper.queryByName(rightOrManagementName);

                //物鱼对销售方的寄售调出单
                FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.FW_SR.getValue());
                fdOut.setOrderType(FinancialDocumentsTypeEnum.FWF.getValue());
                fdOut.setSaleMode(null);
                fdOut.setBelongId(rightOfManagement);
                fdOut.setSerialNumber(SerialNoGenerator.generateFWSRSerialNo());
                fdOut.setCustomerId(saller.getId());

                List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(stockPoList);
                fddOutList.forEach(detail -> {
                    BillSaleOrderLine orderLine = lineMap.get(detail.getStockId());
                    detail.setPurchasePrice(null);
                    detail.setDivideInto(fdOut.getDivideInto());
                    detail.setServiceFee(orderLine.getClinchPrice()
                            .subtract(Objects.nonNull(orderLine.getPromotionConsignmentPrice()) ?
                                    orderLine.getPromotionConsignmentPrice() : detail.getConsignSalePrice())
                            .multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP));
                });
                fdOut.setOrderNumber(fddOutList.size());
                fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getServiceFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
                linkedList.add(tuple2);

                //销售方对物鱼的寄售调入单
                FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdIn.setOrderType(FinancialDocumentsTypeEnum.FWF.getValue());
                fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.FW_ZC.getValue());
                fdIn.setBelongId(saleSubject.getSubjectId());
                fdIn.setSaleMode(null);
                fdIn.setSerialNumber(SerialNoGenerator.generateFWZCSerialNo());
                fdIn.setCustomerId(customer.getId());

                List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(stockPoList);
                fddInList.forEach(detail -> {
                    BillSaleOrderLine orderLine = lineMap.get(detail.getStockId());
                    detail.setPurchasePrice(null);
                    detail.setDivideInto(fdIn.getDivideInto());
                    detail.setServiceFee(lineMap.get(detail.getStockId()).getClinchPrice()
                            .subtract(Objects.nonNull(orderLine.getPromotionConsignmentPrice()) ?
                                    orderLine.getPromotionConsignmentPrice() : detail.getConsignSalePrice())
                            .multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP));
                });
                fdIn.setOrderNumber(fddInList.size());
                fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getServiceFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
                linkedList.add(tuple2Out);
            });
        }

        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseReturnAllocation(FinancialSalesReturnDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> rofMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        //因为是采购主体的调出单 所以客户id 是物鱼平台
        PurchaseSubject customerSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
        Customer customer = customerMapper.queryByName(customerSubject.getName());
        rofMap.forEach((sourceSubjectId, list) -> {

            String supplierSubjectName = subjectMapper.selectNameById(sourceSubjectId);
            //根据采购主体 查出来customerId
            Customer supplier = customerMapper.queryByName(supplierSubjectName);

            //采购主体对物鱼的寄售调入单
            FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DR.getValue());
            fdOut.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
            fdOut.setCustomerId(customer.getId());
            fdOut.setBelongId(sourceSubjectId);
            fdOut.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
            fdOut.setSerialNumber(SerialNoGenerator.generateJSDCSerialNo());
            List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(list);

            fdOut.setOrderNumber(fddOutList.size());
            fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
            linkedList.add(tuple2);

            //物鱼对采购主体的寄售调出单
            FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fdIn.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
            fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DC.getValue());
            fdIn.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
            fdIn.setCustomerId(supplier.getId());
            fdIn.setBelongId(customerSubject.getId());
            fdIn.setSerialNumber(SerialNoGenerator.generateJSDRSerialNo());
            List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(list);
            fdIn.setOrderNumber(fddInList.size());
            fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
            linkedList.add(tuple2Out);
        });
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseAllocation(FinancialSalesDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> rofMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        //因为是采购主体的调出单 所以客户id 是物鱼平台
        PurchaseSubject customerSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
        Customer customer = customerMapper.queryByName(customerSubject.getName());


        for (Map.Entry<Integer,List<StockPo>> entry : rofMap.entrySet()){
            Integer sourceSubjectId = entry.getKey();
            List<StockPo> list = entry.getValue();

            String supplierSubjectName = subjectMapper.selectNameById(sourceSubjectId);
            //根据采购主体 查出来customerId
            Customer supplier = customerMapper.queryByName(supplierSubjectName);

            //采购主体对物鱼的寄售调出单
            FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DC.getValue());
            fdOut.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
            fdOut.setSaleMode(null);
            fdOut.setCustomerId(customer.getId());
            fdOut.setBelongId(sourceSubjectId);
            fdOut.setSerialNumber(SerialNoGenerator.generateJSDCSerialNo());
            List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(list);

            fdOut.setOrderNumber(fddOutList.size());
            fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
            linkedList.add(tuple2);

            //物鱼对采购主体的寄售调入单
            FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DR.getValue());
            fdIn.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
            fdIn.setSaleMode(null);
            fdIn.setCustomerId(supplier.getId());
            fdIn.setBelongId(customerSubject.getId());
            fdIn.setSerialNumber(SerialNoGenerator.generateJSDRSerialNo());
            List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(list);
            fdIn.setOrderNumber(fddInList.size());
            fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
            linkedList.add(tuple2Out);
        }


        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturnAllocation(FinancialSalesReturnDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> rofMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        for (List<StockPo> stockList : rofMap.values()) {
            //查询销售方的采购主体
            StoreRelationshipSubject customerSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                    .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
            if (Objects.isNull(customerSubject))
                throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
            String customerSubjectName = subjectMapper.selectNameById(customerSubject.getSubjectId());
            Customer customer = customerMapper.queryByName(customerSubjectName);
            if (Objects.isNull(customer))
                throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_NOT_EXIT);
            //查询出小蜴总部(物鱼)的采购主体
            PurchaseSubject supplierSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
            Customer supplier = customerMapper.queryByName(supplierSubject.getName());
            if (Objects.isNull(supplier))
                throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_NOT_EXIT);
            Map<Integer, List<StockPo>> stockMap = stockList.stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
            for (List<StockPo> list : stockMap.values()) {

                //物鱼对销售方的寄售调出单
                FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DR.getValue());
                fdOut.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
                fdOut.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
                fdOut.setCustomerId(customer.getId());
                fdOut.setBelongId(supplierSubject.getId());
                fdOut.setSerialNumber(SerialNoGenerator.generateJSDRSerialNo());

                List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(list);
                fdOut.setOrderNumber(fddOutList.size());
                fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
                linkedList.add(tuple2);

                //销售方对物鱼的寄售调入单
                FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.JS_DC.getValue());
                fdIn.setOrderType(FinancialDocumentsTypeEnum.SP_DB.getValue());
                fdIn.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
                fdIn.setCustomerId(supplier.getId());
                fdIn.setBelongId(customerSubject.getSubjectId());
                fdIn.setSerialNumber(SerialNoGenerator.generateJSDCSerialNo());

                List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(list);
                fdIn.setOrderNumber(fddInList.size());
                fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getConsignSalePrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
                linkedList.add(tuple2Out);
            }
        }
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturn(FinancialSalesReturnDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, BillSaleOrderLine> lineMap = dto.getLineMap();
        Map<Integer, BillSaleReturnOrderLine> returnLineMap = dto.getReturnLineMap();
        Map<Integer, List<StockPo>> map = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
        StoreRelationshipSubject saleSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
        if (Objects.isNull(saleSubject))
            throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
        for (List<StockPo> list : map.values()) {
            //门店对客户的销售单
            FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fd.setOrderOrigin(Objects.equals(dto.getSaleReturnType(), SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue()) ?
                    FinancialDocumentsOriginEnum.GR_XS.getValue() : FinancialDocumentsOriginEnum.TH_XS.getValue());
            fd.setOrderType(FinancialDocumentsTypeEnum.XS_TH.getValue());
            fd.setBelongId(saleSubject.getSubjectId());
            fd.setThirdNumber(dto.getThirdNumber());
            fd.setSerialNumber(dto.getSerialNumber() + "-" + list.get(0).getId() + "-" +
                    list.stream().map(Stock::getRightOfManagement).filter(Objects::nonNull).findFirst().orElse(0));

            List<FinancialDocumentsDetail> fddList = getFinancialDocumentsDetails(list);

            fddList.forEach(detail -> {
                BillSaleOrderLine orderLine = lineMap.getOrDefault(detail.getStockId(), new BillSaleOrderLine());
                BillSaleReturnOrderLine returnOrderLine = returnLineMap.getOrDefault(detail.getStockId(), new BillSaleReturnOrderLine());
                detail.setPurchasePrice(null);
                detail.setClinchPrice(orderLine.getClinchPrice());
                detail.setReturnPrice(Optional.ofNullable(returnOrderLine.getReturnPrice())
                                .orElse(orderLine.getClinchPrice()));
                detail.setJsClinchPrice(orderLine.getPreClinchPrice());
                detail.setMarginPrice(orderLine.getMarginPrice());
                detail.setDivideInto(fd.getDivideInto());
                detail.setPromotionConsignmentPrice(orderLine.getPromotionConsignmentPrice());
                if (Objects.equals(dto.getSaleReturnType(), SaleReturnOrderTypeEnum.TO_C_XS_TH.getValue())) {
                    detail.setBrandMarketingExpenses(detail.getTocPrice().compareTo(detail.getClinchPrice()) > 0 ?
                            detail.getTocPrice().subtract(detail.getClinchPrice()) : null);
                }
            });
            fd.setOrderNumber(fddList.size());
            fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getClinchPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fd, fddList);
            linkedList.add(tuple2);
        }
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generateSaleReturnServiceFee(FinancialSalesReturnDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, BillSaleOrderLine> lineMap = dto.getLineMap();

        //查询出小蜴总部(物鱼)的采购主体 经营权值
        PurchaseSubject supplierSubject = subjectMapper.selectPurchaseSubjectByName(SeeeaseConstant.XY_WY_ZB);
        //查出销售方的经营权值
        //查询销售方的采购主体
        StoreRelationshipSubject saleSubject = relationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, dto.getSaleLocationId()));
        if (Objects.isNull(saleSubject))
            throw new OperationRejectedException(OperationExceptionCode.SALE_PURCHASE_NOT_EXIT);
        String customerSubjectName = subjectMapper.selectNameById(saleSubject.getSubjectId());
        Customer saller = customerMapper.queryByName(customerSubjectName);
        if (Objects.isNull(saller))
            throw new OperationRejectedException(OperationExceptionCode.CUSTOMER_NOT_EXIT);

        //TODO 对 经营权 是物鱼的 并且 经营权是自己门店的进行过滤 并且 添加特殊 判断 南宁一二店不需要分成
        List<StockPo> stockList = dto.getStockList().stream().filter(stock ->
                checkDivideInto(stock, supplierSubject, saleSubject)).collect(Collectors.toList());

        //有可能一个订单里有不同经营权的商品 所以需要对 商品进行经营权分组
        Map<Integer, List<StockPo>> map = stockList.stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));

        for (List<StockPo> list : map.values()) {
            Map<Integer, List<StockPo>> stockMap = list.stream().collect(Collectors.groupingBy(StockPo::getRightOfManagement));
            stockMap.forEach((rightOfManagement, stockPoList) -> {

                //查出来经营权的名字
                String rightOrManagementName = subjectMapper.selectNameById(rightOfManagement);
                Customer customer = customerMapper.queryByName(rightOrManagementName);

                //物鱼对销售方的寄售调出单
                FinancialDocuments fdOut = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdOut.setOrderOrigin(FinancialDocumentsOriginEnum.FW_SR.getValue());
                fdOut.setOrderType(FinancialDocumentsTypeEnum.FWF.getValue());
                fdOut.setBelongId(rightOfManagement);
                fdOut.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
                fdOut.setSerialNumber(SerialNoGenerator.generateFWSRSerialNo());
                fdOut.setCustomerId(saller.getId());

                List<FinancialDocumentsDetail> fddOutList = getFinancialDocumentsDetails(stockPoList);
                fddOutList.forEach(detail -> {
                    BillSaleOrderLine orderLine = lineMap.get(detail.getStockId());
                    detail.setPurchasePrice(null);
                    BigDecimal clinchPrice = orderLine.getClinchPrice();
                    BigDecimal finalPrice = clinchPrice
                            .subtract(Objects.nonNull(orderLine.getPromotionConsignmentPrice()) ?
                                    orderLine.getPromotionConsignmentPrice() : detail.getConsignSalePrice())
                            .multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP);
                    detail.setDivideInto(fdOut.getDivideInto());
                    detail.setServiceFee(finalPrice);
                });
                fdOut.setOrderNumber(fddOutList.size());
                fdOut.setOrderMoney(fddOutList.stream().map(FinancialDocumentsDetail::getServiceFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fdOut, fddOutList);
                linkedList.add(tuple2);

                //销售方对物鱼的寄售调入单
                FinancialDocuments fdIn = FinancialDocumentsConvert.INSTANCE.convert(dto);
                fdIn.setOrderType(FinancialDocumentsTypeEnum.FWF.getValue());
                fdIn.setOrderOrigin(FinancialDocumentsOriginEnum.FW_ZC.getValue());
                fdIn.setBelongId(saleSubject.getSubjectId());
                fdIn.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
                fdIn.setSerialNumber(SerialNoGenerator.generateFWZCSerialNo());
                fdIn.setCustomerId(customer.getId());
                List<FinancialDocumentsDetail> fddInList = getFinancialDocumentsDetails(stockPoList);
                fddInList.forEach(detail -> {
                    BillSaleOrderLine orderLine = lineMap.get(detail.getStockId());
                    detail.setPurchasePrice(null);
                    detail.setDivideInto(fdIn.getDivideInto());
                    detail.setServiceFee(orderLine.getClinchPrice()
                            .subtract(Objects.nonNull(orderLine.getPromotionConsignmentPrice()) ?
                                    orderLine.getPromotionConsignmentPrice() : detail.getConsignSalePrice())
                            .multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP));
                });
                fdIn.setOrderNumber(fddInList.size());
                fdIn.setOrderMoney(fddInList.stream().map(FinancialDocumentsDetail::getServiceFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));

                Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fdIn, fddInList);
                linkedList.add(tuple2Out);
            });
        }

        return linkedList;
    }

    private List<FinancialDocumentsDetail> getFinancialDocumentsDetails(List<StockPo> stockPoList) {
        List<FinancialDocumentsDetail> fddList = stockPoList.stream().map(stock -> {
            FinancialDocumentsDetail detail = FinancialDocumentsConvert.INSTANCE.convert(stock);
            detail.setId(null);
            return detail;
        }).collect(Collectors.toList());
        return fddList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchase(FinancialPurchaseDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();

        FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
        fd.setOrderType(FinancialDocumentsTypeEnum.CG.getValue());
        List<StockPo> list = dto.getStockList();
        fd.setBelongId(list.get(0).getSourceSubjectId());
        List<FinancialDocumentsDetail> fddList = getFinancialDocumentsDetails(list);
        fddList.forEach(detail -> {
            if (Objects.equals(fd.getOrderOrigin(), FinancialDocumentsOriginEnum.GR_HG.getValue()))
                detail.setConsignSalePrice(null);
        });
        fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        fd.setOrderNumber(fddList.size());
        Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fd, fddList);
        linkedList.add(tuple2);
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseReturn(FinancialPurchaseReturnDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> stockMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        stockMap.forEach((sourceSubjectId, list) -> {
            FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fd.setOrderType(FinancialDocumentsTypeEnum.CG_TH.getValue());
//            fd.setOrderOrigin(FinancialDocumentsOriginEnum.TH_CG.getValue());
            fd.setSaleMode(FinancialDocumentsModeEnum.REFUND.getValue());
            fd.setBelongId(sourceSubjectId);
            List<FinancialDocumentsDetail> fddList = getFinancialDocumentsDetails(list);
            fddList.forEach(detail -> detail.setReturnPrice(detail.getPurchasePrice()));
            fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getPurchasePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            fd.setOrderNumber(fddList.size());
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fd, fddList);
            linkedList.add(tuple2);
        });
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseServiceFee(FinancialPurchaseDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();

        FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
        fd.setOrderType(FinancialDocumentsTypeEnum.FWF.getValue());
        fd.setOrderOrigin(FinancialDocumentsOriginEnum.HG_FW.getValue());

        List<StockPo> list = dto.getStockList();

        fd.setBelongId(storeRelationshipSubjectMapper.selectOne(Wrappers.<StoreRelationshipSubject>lambdaQuery()
                .eq(StoreRelationshipSubject::getStoreManagementId, dto.getDemandId())).getSubjectId());
        List<FinancialDocumentsDetail> fddList = list.stream()
                .filter(po -> Optional.ofNullable(dto.getServiceFeeMap().get(po.getId())).isPresent())
                .map(stock -> {
                    FinancialDocumentsDetail detail = FinancialDocumentsConvert.INSTANCE.convert(stock);
                    detail.setDemandId(dto.getDemandId());
                    detail.setBuyBackServiceFee(dto.getServiceFeeMap().getOrDefault(stock.getId(), new BigDecimal(0)));
                    detail.setServiceFee(dto.getServiceFeeMap().getOrDefault(stock.getId(), new BigDecimal(0)));
                    detail.setConsignSalePrice(null);
                    detail.setPurchasePrice(null);
                    return detail;
                })
                .collect(Collectors.toList());
        fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getBuyBackServiceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        fd.setOrderNumber(fddList.size());
        Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2Out = Tuple2.of(fd, fddList);
        linkedList.add(tuple2Out);
        return linkedList;
    }

    @Override
    public LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> generatePurchaseMarginCover(FinancialPurchaseDto dto) {
        LinkedList<Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>>> linkedList = new LinkedList<>();
        Map<Integer, List<StockPo>> stockMap = dto.getStockList().stream().collect(Collectors.groupingBy(StockPo::getSourceSubjectId));
        stockMap.forEach((sourceSubjectId, list) -> {
            FinancialDocuments fd = FinancialDocumentsConvert.INSTANCE.convert(dto);
            fd.setOrderType(FinancialDocumentsTypeEnum.CG.getValue());
            fd.setBelongId(sourceSubjectId);
            List<FinancialDocumentsDetail> fddList = getFinancialDocumentsDetails(list);
            fddList.forEach(a -> {
                if (dto.getStockMap().containsKey(a.getStockId()))
                    a.setMarginPrice(dto.getStockMap().get(a.getStockId()).subtract(a.getPurchasePrice()));
            });
            fd.setOrderMoney(fddList.stream().map(FinancialDocumentsDetail::getMarginPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            fd.setOrderNumber(fddList.size());
            Tuple2<FinancialDocuments, List<FinancialDocumentsDetail>> tuple2 = Tuple2.of(fd, fddList);
            linkedList.add(tuple2);
        });
        return linkedList;
    }


    private boolean checkDivideInto(StockPo vo, PurchaseSubject subject, StoreRelationshipSubject saleSubject) {
        if (FlywheelConstant.EXCLUDE_SUBJECT_ID.contains(vo.getRightOfManagement()))
            return false;
        if (Objects.equals(vo.getRightOfManagement(), subject.getId()) ||
                Objects.equals(vo.getRightOfManagement(), saleSubject.getSubjectId()))
            return false;
        if ((saleSubject.getSubjectId() == FlywheelConstant.SUBJECT_NN_YD && vo.getRightOfManagement() == FlywheelConstant.SUBJECT_NN_ED) ||
                saleSubject.getSubjectId() == FlywheelConstant.SUBJECT_NN_ED && vo.getRightOfManagement() == FlywheelConstant.SUBJECT_NN_YD)
            return false;
        return true;
    }
}
