package com.seeease.flywheel.serve.financial.rpc;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.IFinancialFacade;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.financial.convert.FinancialDocumentsConvert;
import com.seeease.flywheel.serve.financial.entity.FinancialAllocationData;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.entity.kingDee.FinancialConfig;
import com.seeease.flywheel.serve.financial.entity.FinancialDocuments;
import com.seeease.flywheel.serve.financial.entity.kingDee.FinancialDocumentsJDDto;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsOriginEnum;
import com.seeease.flywheel.serve.financial.service.FinancialAllocationDataService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsDetailService;
import com.seeease.flywheel.serve.financial.service.FinancialDocumentsService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.StoreRelationshipSubject;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.enums.SaleOrderChannelEnum;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.sale.service.BillSaleReturnOrderService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import kingdee.bos.webapi.client.K3CloudApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Slf4j
@DubboService(version = "1.0.0")
public class FinancialFacade implements IFinancialFacade {
    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private FinancialDocumentsService service;
    @Resource
    private FinancialDocumentsDetailService detailService;
    @Resource
    private StoreRelationshipSubjectService subjectService;
    @Resource
    private CustomerService customerService;
    @Resource
    private StockService stockService;
    @Resource
    private UserService userService;
    @Resource
    private BillSaleOrderService saleOrderService;
    @Resource
    private BillSaleOrderLineService saleOrderLineService;
    @Resource
    private BillSaleReturnOrderService saleReturnOrderService;
    @Resource
    private FinancialAllocationDataService dataService;
    @Resource
    private StoreRelationshipSubjectService relationshipSubjectService;

    @Override
    public PageResult<FinancialPageAllResult> queryAll(FinancialQueryAllRequest financialQueryAllRequest) {
        FinancialQueryAllRequest request = handleParam(financialQueryAllRequest);

        Page<FinancialPageAllResult> page = service.selectByFinancialQueryAllRequest(request);
        Map<Integer, String> map = purchaseSubjectService.list().stream().collect(Collectors.toMap(PurchaseSubject::getId, PurchaseSubject::getName));
        page.getRecords().forEach(result -> result.setBelongSubjectName(map.getOrDefault(result.getBelongId(), "未知来源")));
        return PageResult.<FinancialPageAllResult>builder()
                .result(page.getRecords())
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<FinancialDetailsResult> detail(FinancialDetailsRequest request) {
        FinancialDocuments documents = service.getById(request.getId());
        if (Objects.isNull(documents)) {
            throw new BusinessException(ExceptionCode.FINANCIAL_DOCUMENTS_NOT_EXIST);
        }
        Customer customer = customerService.getById(documents.getCustomerId());
        Map<Integer, PurchaseSubject> purchaseSubjectMap = purchaseSubjectService.list().stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, Function.identity(), (k1, k2) -> k2));
        Map<Integer, String> storeManagementMap = storeManagementService.getStoreMap();
        List<FinancialDetailsResult> list = Optional.ofNullable(detailService.detail(request))
                .orElseGet(com.google.common.collect.Lists::newArrayList)
                .stream()
                .map(t -> {
                    FinancialDetailsResult result = FinancialDocumentsConvert.INSTANCE.convert(t);
                    result.setCustomerType(Optional.ofNullable(customer).map(Customer::getType).orElseGet(null).getValue());
                    result.setCustomerName(Optional.ofNullable(customer).map(Customer::getCustomerName).orElse(null));
                    //商品归属
                    result.setBelongName(Optional.ofNullable(purchaseSubjectMap.get(t.getBelongId())).map(PurchaseSubject::getName).orElse("未知所属"));
                    //所在仓库
                    result.setLocationName(storeManagementMap.getOrDefault(t.getLocationId(), "未知所在"));
                    //采购主体
                    result.setPurchaseSubject(Optional.ofNullable(purchaseSubjectMap.get(t.getSourceSubjectId())).map(PurchaseSubject::getName).orElse(null));
                    //经营权
                    result.setOutletStoreStr(Optional.ofNullable(purchaseSubjectMap.get(t.getOutletStore())).map(PurchaseSubject::getName).orElse(null));
                    //销售位置
                    result.setSalesPositionStr(storeManagementMap.getOrDefault(t.getSalesPosition(), "未知所在"));
                    //商品归属
                    result.setGoodsBelongStr(Optional.ofNullable(purchaseSubjectMap.get(t.getGoodsBelong())).map(PurchaseSubject::getName).orElse(null));
                    //商品位置
                    result.setGoodsPositionStr(storeManagementMap.getOrDefault(t.getGoodsPosition(), "未知所在"));
                    //需求门店
                    result.setDemandStoreStr(storeManagementMap.getOrDefault(t.getDemandId(), "未知所在"));

                    Stock stock = stockService.getById(result.getStockId());
                    if (ObjectUtil.isNotNull(stock))
                        result.setAttachment(stock.getAttachment());
                    return result;
                })
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public List<FinancialExportResult> export(FinancialQueryAllRequest financialQueryAllRequest) {
        FinancialQueryAllRequest request = handleParam(financialQueryAllRequest);
        List<FinancialExportResult> results = service.selectExcelByFinancialDocumentsQueryDto(request);
        Map<Integer, PurchaseSubject> purchaseSubjectMap = purchaseSubjectService.list().stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, Function.identity(), (k1, k2) -> k2));
        Map<Integer, String> storeManagementMap = storeManagementService.getStoreMap();

        //查同行销售
        List<String> xsSale = results.stream().filter(t -> Objects.nonNull(t.getOrderOrigin()) && t.getOrderOrigin().equals(FinancialDocumentsOriginEnum.TH_XS.getValue()) &&
                        FinancialDocumentsModeEnum.SALE_NORMAL.getValue().equals(t.getSaleMode()))
                .map(FinancialExportResult::getAssocSerialNumber).collect(Collectors.toList());
        Map<String, BillSaleOrder> xsMap = saleOrderService.selectBySerialNoList(xsSale)
                .stream().collect(Collectors.toMap(BillSaleOrder::getSerialNo, Function.identity()));
        //查同行寄售
        Map<String, BillSaleOrder> jsSaleMap = saleOrderService.selectBySerialNoList(results.stream()
                        .filter(t ->  Objects.nonNull(t.getOrderOrigin()) && t.getOrderOrigin().equals(FinancialDocumentsOriginEnum.TH_XS.getValue()) &&
                                FinancialDocumentsModeEnum.SALE_CONSIGN_FOR_SALE.getValue().equals(t.getSaleMode()))
                        .map(FinancialExportResult::getAssocSerialNumber).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(BillSaleOrder::getSerialNo, Function.identity()));
        Map<Integer, String> jsMap = saleOrderLineService.selectBySaleIds(jsSaleMap.values().stream().map(BillSaleOrder::getId).collect(Collectors.toList()))
                .stream().filter(t -> StringUtils.isNotBlank(t.getConsignmentSettlementOperator()))
                .collect(Collectors.toMap(BillSaleOrderLine::getStockId, BillSaleOrderLine::getConsignmentSettlementOperator, (v1, v2) -> v1));
        Map<Long, String> userMap = userService.list().stream().collect(Collectors.toMap(User::getId, User::getName));
        results.forEach(t -> {
            //同行销售
            if (xsMap.containsKey(t.getAssocSerialNumber())) {
                if (userMap.containsKey(xsMap.get(t.getAssocSerialNumber()).getFirstSalesman().longValue())) {
                    t.setSalesMan(userMap.get(xsMap.get(t.getAssocSerialNumber()).getFirstSalesman().longValue()));
                }
            }
            //同行寄售
            if (jsSaleMap.containsKey(t.getAssocSerialNumber()) && jsMap.containsKey(t.getStockId())) {
                t.setSalesMan(jsMap.get(t.getStockId()));
            }
            t.setBelongName(Optional.ofNullable(purchaseSubjectMap.get(t.getSourceSubjectId())).map(PurchaseSubject::getName).orElse("未知所属"));
            //所在仓库
            t.setLocationName(storeManagementMap.getOrDefault(t.getLocationId(), "未知所在"));
            t.setPurchaseSubject(Optional.ofNullable(purchaseSubjectMap.get(t.getSourceSubjectId())).map(PurchaseSubject::getName).orElse(null));
            t.setBelongSubjectName(Optional.ofNullable(purchaseSubjectMap.get(t.getBelongId())).map(PurchaseSubject::getName).orElse("未知来源"));
            //经营权
            t.setOutletStoreStr(Optional.ofNullable(purchaseSubjectMap.get(t.getOutletStore())).map(PurchaseSubject::getName).orElse(null));
            //销售位置
            t.setSalesPositionStr(storeManagementMap.getOrDefault(t.getSalesPosition(), "未知所在"));
            //商品归属
            t.setGoodsBelongStr(Optional.ofNullable(purchaseSubjectMap.get(t.getGoodsBelong())).map(PurchaseSubject::getName).orElse(null));
            //商品位置
            t.setGoodsPositionStr(storeManagementMap.getOrDefault(t.getGoodsPosition(), "未知所在"));
            //需求门店
            t.setDemandStoreStr(storeManagementMap.getOrDefault(t.getDemandId(), "未知所在"));
        });
        return results;
    }

    @Override
    public String jDImport(FinancialQueryAllRequest financialQueryAllRequest) {
        /**
         * 获取对应参数
         */
        FinancialQueryAllRequest request = handleParam(financialQueryAllRequest);
        List<FinancialExportResult> vos = service.selectExcelByFinancialDocumentsQueryDto(request);
        Map<Integer, PurchaseSubject> purchaseSubjectMap = purchaseSubjectService.list().stream()
                .collect(Collectors.toMap(PurchaseSubject::getId, Function.identity(), (k1, k2) -> k2));
        Map<Integer, String> storeManagementMap = storeManagementService.getStoreMap();
        vos.forEach(t -> {
                    t.setBelongName(Optional.ofNullable(purchaseSubjectMap.get(t.getBelongId())).map(PurchaseSubject::getName).orElse("未知所属"));
                    t.setLocationName(storeManagementMap.getOrDefault(t.getLocationId(), "未知所在"));
                    t.setPurchaseSubject(Optional.ofNullable(purchaseSubjectMap.get(t.getSourceSubjectId())).map(PurchaseSubject::getName).orElse(null));
                    t.setBelongSubjectName(Optional.ofNullable(purchaseSubjectMap.get(t.getBelongId())).map(PurchaseSubject::getName).orElse("未知来源"));
                    //经营权
                    t.setOutletStoreStr(Optional.ofNullable(purchaseSubjectMap.get(t.getOutletStore())).map(PurchaseSubject::getName).orElse(null));
                    //销售位置
                    t.setSalesPositionStr(storeManagementMap.getOrDefault(t.getSalesPosition(), "未知所在"));//商品归属
                    t.setGoodsBelongStr(Optional.ofNullable(purchaseSubjectMap.get(t.getGoodsBelong())).map(PurchaseSubject::getName).orElse(null));
                    //商品位置
                    t.setGoodsPositionStr(storeManagementMap.getOrDefault(t.getGoodsPosition(), "未知所在"));
                    //需求门店
                    t.setDemandStoreStr(storeManagementMap.getOrDefault(t.getDemandId(), "未知所在"));
                }
        );
        List<String> errorList = CollUtil.newArrayList();

        //2。过滤对应税局是否符合
        vos.stream().forEach(result -> {
            if (!FinancialConfig.ORDER_TYPE.contains(result.getOrderType())) {
                errorList.add(result.getSerialNumber());
            }
        });


        if (CollUtil.isNotEmpty(errorList)) {
            throw new OperationRejectedException(OperationExceptionCode.DOES_NOT_CONFORM_TO_THE_FINANCIAL_SYSTEM_SPECIFICATIONS_OF_KING_DEE, errorList);
        }

        //前置用户信息
        List<User> users = userService.list();
        users.forEach(user -> {
            FinancialConfig.USER_MAP.put(user.getName(), user.getJobNumber());
        });

        //失败
        List<String> errorExportList = CollUtil.newArrayList();

        //警告封装数据 不符合数据
        List<String> warningExportList = CollUtil.newArrayList();

        List<FinancialAllocationData> dataList = dataService.list();

        //抖音门店
        Map<Integer, FinancialAllocationData> douYinDataMap = dataList.stream()
                .filter(a -> a.getType() == 1 && Objects.nonNull(a.getDouyinShopId()))
                .collect(Collectors.toMap(FinancialAllocationData::getDouyinShopId, Function.identity(), (e1, e2) -> e1));
        //飞轮订单来源
        Map<Integer, FinancialAllocationData> shopDataMap = dataList.stream()
                .filter(a -> a.getType() == 0 && Objects.nonNull(a.getSubjectId()))
                .collect(Collectors.toMap(FinancialAllocationData::getSubjectId, Function.identity(), (e1, e2) -> e1));

        vos.forEach(result -> {
            K3CloudApiClient client = new K3CloudApiClient("http://8.142.124.57:8090/k3cloud/");
            //3。包装金蝶对象
            FinancialDocumentsJDDto financialDocumentsJDDto = packageFinancialDocumentsJDDto(result, douYinDataMap, shopDataMap);

            if (ObjectUtil.isEmpty(financialDocumentsJDDto)) {
                log.error(StrUtil.format("金蝶空请求参数->{}", financialDocumentsJDDto));
                warningExportList.add(result.getSerialNumber());
                return;
            }

            log.info(StrUtil.format("金蝶请求参数->{}", JSON.toJSONString(financialDocumentsJDDto)));

            //4.获取金蝶客户端，判断是否登陆
//            if (!FinancialConfig.login) {
//                FinancialConfig.login = getaBoolean(client);
//            }

            if (getaBoolean(client)) {

                String save = StrUtil.EMPTY;

                try {
                    save = client.save("AR_OtherRecAble", JSON.toJSONString(financialDocumentsJDDto));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info(StrUtil.format("单据->{}", save));

                //导入失败
                if (StrUtil.isBlank(save)) {
                    errorExportList.add(result.getSerialNumber());
                }
            }
        });

        if (CollUtil.isNotEmpty(errorExportList) || CollUtil.isNotEmpty(warningExportList)) {
            throw new OperationRejectedException(OperationExceptionCode.KING_DEE_RESULT_WARNING, errorExportList, warningExportList);
        }

        return StrUtil.EMPTY;
    }

    /**
     * 新财务 新增 1是退货 2是销售 3是采购 4是采购退货
     *
     * @return
     */
    @Override
    public void newGenerateFinancialOrder(FinancialGenerateOrderRequest request) {
        if (request.getSaleType() == 1) {
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(request.getId());
            dto.setStockList(request.getStockList());
            service.generateSaleReturn(dto);
        } else if (request.getSaleType() == 2) {
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(request.getId());
            dto.setStockList(request.getStockList());
            service.generateSale(dto);
        } else if (request.getSaleType() == 3) {
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(request.getId());
            dto.setStockList(request.getStockList());
            dto.setType(request.getType());
            service.generatePurchase(dto);
        } else if (request.getSaleType() == 4) {
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(request.getId());
            dto.setStockList(request.getStockList());
            dto.setType(request.getType());
            service.generatePurchaseReturn(dto);
        }
    }

    private Boolean getaBoolean(K3CloudApiClient client) {
        try {
            return client.login(
                    //数据库中心ID（即账套ID）
                    FinancialConfig.account,
                    //用户名
                    FinancialConfig.userName,
                    //密码
                    FinancialConfig.password,
                    //
                    FinancialConfig.lcId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FinancialConfig.login;
    }

    private FinancialDocumentsJDDto packageFinancialDocumentsJDDto(FinancialExportResult result, Map<Integer, FinancialAllocationData> douYinDataMap,
                                                                   Map<Integer, FinancialAllocationData> shopDataMap) {

        //JSON格式数据（详情参考JSON格式数据）（必录）
        FinancialDocumentsJDDto financialDocumentsJDDto = new FinancialDocumentsJDDto();

        //表单数据包，JSON类型（必录）
        FinancialDocumentsJDDto.ModelDTO modelDTO = new FinancialDocumentsJDDto.ModelDTO();

        /**
         * 单据头 ********** start ****************
         */

        //单据类型
        FinancialDocumentsJDDto.ModelDTO.FBillTypeIDDTO fBillTypeIDDTO = new FinancialDocumentsJDDto.ModelDTO.FBillTypeIDDTO();
        fBillTypeIDDTO.setFNUMBER("QTYSD01_SYS");
        modelDTO.setFBillTypeID(fBillTypeIDDTO);

        String format = DateUtil.format(result.getCreateTime(), DatePattern.NORM_DATE_PATTERN);
        //业务日期
        modelDTO.setFDATE(format);

        //往来单位
        //销售渠道/订单类型
        FinancialDocumentsJDDto.ModelDTO.FCONTACTUNITDTO fcontactunitdto = new FinancialDocumentsJDDto.ModelDTO.FCONTACTUNITDTO();

        String s = FinancialConfig.DEALING_UNIT.getOrDefault(result.getClcId(), "CUST0189");
        log.info(StrUtil.format("往来单位->{},往来单位ID->{}", result.getClcId(), s));
        fcontactunitdto.setFNumber(s);
        modelDTO.setFCONTACTUNIT(fcontactunitdto);

        //往来单位类型
        modelDTO.setFCONTACTUNITTYPE("BD_Customer");

        //币别
        FinancialDocumentsJDDto.ModelDTO.FCURRENCYIDDTO fcurrencyiddto = new FinancialDocumentsJDDto.ModelDTO.FCURRENCYIDDTO();
        fcurrencyiddto.setFNumber("PRE001");
        modelDTO.setFCURRENCYID(fcurrencyiddto);

        Integer douYinShopId = checkDouYinOrder(result);

        //结算组织#编码
        // 订单来源判断 结算组织和收款组织
        FinancialDocumentsJDDto.ModelDTO.FSETTLEORGIDDTO fsettleorgiddto = new FinancialDocumentsJDDto.ModelDTO.FSETTLEORGIDDTO();
        String sss = shopDataMap.getOrDefault(result.getBelongId(), new FinancialAllocationData()).getClearingOrganization();
        //如果业务方式是平台 销售渠道是抖音 先判断 douYinDataMap有没有
        if (douYinDataMap.containsKey(douYinShopId)) {
            sss = douYinDataMap.get(douYinShopId).getClearingOrganization();
        }
//        String sss = FinancialConfig.CLEARING_ORGANIZATION.get(result.getBelongSubjectName());
        if (ObjectUtil.isEmpty(sss)) {

            log.warn(StrUtil.format("结算组织->{}", result.getBelongSubjectName()));
            return null;
        }

        log.info(StrUtil.format("结算组织->{},结算组织ID->{}", result.getBelongSubjectName(), sss));

        fsettleorgiddto.setFNumber(sss);
        modelDTO.setFSETTLEORGID(fsettleorgiddto);

        // 本位币
        FinancialDocumentsJDDto.ModelDTO.FMAINBOOKSTDCURRIDDTO fmainbookstdcurriddto = new FinancialDocumentsJDDto.ModelDTO.FMAINBOOKSTDCURRIDDTO();
        fmainbookstdcurriddto.setFNumber("PRE001");
        modelDTO.setFMAINBOOKSTDCURRID(fmainbookstdcurriddto);

        //汇率类型
        FinancialDocumentsJDDto.ModelDTO.FEXCHANGETYPEDTO fexchangetypedto = new FinancialDocumentsJDDto.ModelDTO.FEXCHANGETYPEDTO();
        fexchangetypedto.setFNumber("HLTX01_SYS");
        modelDTO.setFEXCHANGETYPE(fexchangetypedto);

        //作废状态  默认传什么
        modelDTO.setFCancelStatus("A");

        //到期日计算日期
        modelDTO.setFACCNTTIMEJUDGETIME(format);

        //收款组织#编码
        // 订单来源判断 结算组织和收款组织
        FinancialDocumentsJDDto.ModelDTO.FPAYORGIDDTO fpayorgiddto = new FinancialDocumentsJDDto.ModelDTO.FPAYORGIDDTO();
//        String ssss = FinancialConfig.CLEARING_ORGANIZATION.get(result.getBelongSubjectName());
        String ssss = shopDataMap.getOrDefault(result.getBelongId(), new FinancialAllocationData()).getClearingOrganization();
        //如果业务方式是平台 销售渠道是抖音 先判断 douYinDataMap有没有
        if (douYinDataMap.containsKey(douYinShopId)) {
            ssss = douYinDataMap.get(douYinShopId).getClearingOrganization();
        }

        if (ObjectUtil.isEmpty(ssss)) {

            log.warn(StrUtil.format("收款组织->{}", result.getBelongSubjectName()));
            return null;
        }

        log.info(StrUtil.format("收款组织->{},收款组织ID->{}", result.getBelongSubjectName(), ssss));
        fpayorgiddto.setFNumber(ssss);
        modelDTO.setFPAYORGID(fpayorgiddto);

        //客户名称  F_TEZV_Text
        modelDTO.setF_TEZV_Text(result.getCustomerName());

        //客户类别 F_TEZV_Assistant
        FinancialDocumentsJDDto.ModelDTO.F_TEZV_Assistant f_tezv_assistant = new FinancialDocumentsJDDto.ModelDTO.F_TEZV_Assistant();
//        String ss = FinancialConfig.CLIENT_TYPE.get(result.getClcId());
//        log.info(StrUtil.format("客户类别->{},客户类别ID->{}", result.getClcId(), ss));

        String ss = FinancialConfig.GOODS_BELONG.get(result.getGoodsBelong());
        log.info(StrUtil.format("飞轮商品归属->{},金蝶商品归属ID->{}", result.getGoodsBelong(), ss));
        f_tezv_assistant.setFNumber(ss);
        modelDTO.setF_TEZV_Assistant(f_tezv_assistant);

        //员工#编码  F_TEZV_Base
        String ssssss = FinancialConfig.USER_MAP.get(result.getCreateBy());

        if (ObjectUtil.isEmpty(ssssss)) {

            log.warn(StrUtil.format("员工->{}", result.getCreateBy()));
        } else {
            log.info(StrUtil.format("员工->{}, 工号->{}", result.getCreateBy(), ssssss));

            FinancialDocumentsJDDto.ModelDTO.F_TEZV_Base f_tezv_base = new FinancialDocumentsJDDto.ModelDTO.F_TEZV_Base();
            f_tezv_base.setFSTAFFNUMBER(ssssss);
            modelDTO.setF_TEZV_Base(f_tezv_base);
        }

        //对应组织 todo F_TEZV_OrgId 不填

        modelDTO.setFAR_OtherRemarks(result.getStockSn());

        /**
         * 单据头 ********** end ****************
         */

        /**
         * 单据体 ********** start ****************
         */
        FinancialDocumentsJDDto.ModelDTO.FEntityDTO fEntityDTO = new FinancialDocumentsJDDto.ModelDTO.FEntityDTO();

//        //费用项目编码
//        FinancialDocumentsJDDto.ModelDTO.FEntityDTO.FCOSTIDDTO fcostiddto = new FinancialDocumentsJDDto.ModelDTO.FEntityDTO.FCOSTIDDTO();
//        // 必传
//        fcostiddto.setFNumber("xsfy014");

        //费用承担部门#编码 //订单来源  判断费用承担部门
        FinancialDocumentsJDDto.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO fcostdepartmentiddto = new FinancialDocumentsJDDto.ModelDTO.FEntityDTO.FCOSTDEPARTMENTIDDTO();
//        String sssss = FinancialConfig.EXPENSE_BEARING_DEPARTMENT.get(result.getBelongSubjectName());
        String sssss = shopDataMap.getOrDefault(result.getBelongId(), new FinancialAllocationData()).getExpenseBearingDepartment();
        //如果业务方式是平台 销售渠道是抖音 先判断 douYinDataMap有没有
        if (Objects.nonNull(douYinShopId)) {
            sssss = douYinDataMap.get(douYinShopId).getExpenseBearingDepartment();
        }

        if (ObjectUtil.isEmpty(sssss)) {

            log.warn(StrUtil.format("费用承担部门->{}", result.getBelongSubjectName()));
            return null;
        }

        log.info(StrUtil.format("费用承担部门->{},费用承担部门ID->{}", result.getBelongSubjectName(), sssss));
        fcostdepartmentiddto.setFNumber(sssss);
        fEntityDTO.setFCOSTDEPARTMENTID(fcostdepartmentiddto);

        //发票类型
        fEntityDTO.setFINVOICETYPE("普通发票");

        //不含税金额 小数
//        fEntityDTO.setFNOTAXAMOUNTFOR(result.getClinchPrice());

        if (FinancialConfig.RETURN_GOODS.contains(result.getOrderType())) {
            fEntityDTO.setFNOTAXAMOUNTFOR(StrUtil.format("-{}", result.getClinchPrice()));
            //总成本  F_TEZV_Amount
            fEntityDTO.setF_TEZV_Amount(new BigDecimal(result.getConsignSalePrice()).negate().toString());
            if (Objects.nonNull(result.getPromotionConsignmentPrice())) {
                fEntityDTO.setF_TEZV_Amount(result.getPromotionConsignmentPrice().negate().toString());
            }
            //数量  F_TEZV_Decimal
            fEntityDTO.setF_TEZV_Decimal(-1);
            //品牌营销费用
            if (ObjectUtil.isNotNull(result.getBrandMarketingExpenses()))
                fEntityDTO.setF_TEZV_Amount1(result.getBrandMarketingExpenses().negate().toString());
        } else if (FinancialConfig.GOODS.contains(result.getOrderType())) {
            fEntityDTO.setFNOTAXAMOUNTFOR(result.getClinchPrice());
            //总成本  F_TEZV_Amount
            fEntityDTO.setF_TEZV_Amount(result.getConsignSalePrice());
            if (Objects.nonNull(result.getPromotionConsignmentPrice())) {
                fEntityDTO.setF_TEZV_Amount(result.getPromotionConsignmentPrice().toString());
            }
            fEntityDTO.setF_TEZV_Decimal(1);
            //品牌营销费用
            if (ObjectUtil.isNotNull(result.getBrandMarketingExpenses()))
                fEntityDTO.setF_TEZV_Amount1(String.valueOf(result.getBrandMarketingExpenses()));
        }

//        fEntityDTO.setFNOTAXAMOUNTFOR(result.getClinchPrice());
        //总成本  F_TEZV_Amount
//        fEntityDTO.setF_TEZV_Amount(result.getOrderType() == 3 ?
//                new BigDecimal(result.getConsignSalePrice()).negate().toString()
//                : result.getConsignSalePrice());
        //品牌  F_TEZV_Text1
        fEntityDTO.setF_TEZV_Text1(result.getBrandName());
        //型号  F_TEZV_Text2
        fEntityDTO.setF_TEZV_Text2(result.getModelName());
        //表身号  F_TEZV_Text3
        fEntityDTO.setF_TEZV_Text3(result.getStockSn());
        //物料编码  F_TEZV_Base1
        FinancialDocumentsJDDto.ModelDTO.FEntityDTO.F_TEZV_Base1 f_tezv_base1 = new FinancialDocumentsJDDto.ModelDTO.FEntityDTO.F_TEZV_Base1();
        f_tezv_base1.setFNumber("001");
        fEntityDTO.setF_TEZV_Base1(f_tezv_base1);

        /**
         * 单据体 ********** end ****************
         */
        modelDTO.setFEntity(CollUtil.newArrayList(fEntityDTO));
        financialDocumentsJDDto.setModel(modelDTO);
        String str = JSON.toJSONString(financialDocumentsJDDto);

        log.info(str);

        return financialDocumentsJDDto;
    }

    private Integer checkDouYinOrder(FinancialExportResult result) {
        if (FinancialDocumentsModeEnum.SALE_ON_LINE.getValue().equals(result.getSaleMode()) && SaleOrderChannelEnum.DOU_YIN.getValue().equals(result.getClcId())) {
            if (FinancialConfig.GOODS.contains(result.getOrderType())) {
                return saleOrderService.selectDouYinOrderBySerialNo(result.getAssocSerialNumber());
            } else if (FinancialConfig.RETURN_GOODS.contains(result.getOrderType())) {
                return saleReturnOrderService.selectDouYinOrderBySerialNo(result.getAssocSerialNumber());
            }
        }
        return null;
    }

    /**
     * 列表条件处理
     *
     * @param request
     * @return
     */
    private FinancialQueryAllRequest handleParam(FinancialQueryAllRequest request) {

        //客户类型
        if (!Optional.ofNullable(request.getCustomerType()).filter(i -> i > 0).isPresent()) {
            request.setCustomerType(null);
        }

        //客户名称
        if (StringUtils.isBlank(request.getCustomerName())) {
            request.setCustomerName(null);
        }
        FinancialQueryAllRequest.FinancialDocumentsQueryExtDto ext = request.getExt();
        //扩张参数
        if (Objects.nonNull(ext) && !((StringUtils.isNotBlank(ext.getBeginSaleTime()) && StringUtils.isNotBlank(ext.getEndSaleTime()))
                || StringUtils.isNotBlank(ext.getStockSn())
                || StringUtils.isNotBlank(ext.getModelName())
                || StringUtils.isNotBlank(ext.getWno()))) {
            request.setExt(null);
        }
        //权限控制
        Integer storeId = UserContext.getUser().getStore().getId();
        if (storeId != SeeeaseConstant._ZB_ID) {
            StoreRelationshipSubject subject = subjectService.getByShopId(storeId);
            request.setBelongId(subject.getSubjectId());
        }

        //订单来源条件
        if (Objects.nonNull(request.getBelongSubject()) && request.getBelongSubject().size() == 0) {
            request.setBelongSubject(null);
        }
        //导出手选择项
        if (Objects.nonNull(request.getDocBatchIds()) && request.getDocBatchIds().size() == 0) {
            request.setDocBatchIds(null);
        }
        return request;
    }
}
