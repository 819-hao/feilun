package com.seeease.flywheel.serve.purchase.rpc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.AccountReceiptConfirmAddRequest;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest;
import com.seeease.flywheel.financial.result.ApplyFinancialPaymentCreateResult;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.purchase.IPurchaseFacade;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.base.*;
import com.seeease.flywheel.serve.base.event.BillHandlerEventPublisher;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.entity.CustomerContacts;
import com.seeease.flywheel.serve.customer.entity.CustomerPO;
import com.seeease.flywheel.serve.customer.service.CustomerContactsService;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.dict.entity.DictData;
import com.seeease.flywheel.serve.dict.service.DictDataService;
import com.seeease.flywheel.serve.financial.convert.ApplyFinancialPaymentConvert;
import com.seeease.flywheel.serve.financial.entity.*;
import com.seeease.flywheel.serve.financial.enums.*;
import com.seeease.flywheel.serve.financial.mapper.AccountStockRelationMapper;
import com.seeease.flywheel.serve.financial.service.*;
import com.seeease.flywheel.serve.financial.template.payment.PaymentCTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentDTemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentETemplate;
import com.seeease.flywheel.serve.financial.template.payment.PaymentFTemplate;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.entity.PurchaseSubject;
import com.seeease.flywheel.serve.maindata.entity.Tag;
import com.seeease.flywheel.serve.maindata.entity.User;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.flywheel.serve.maindata.service.UserService;
import com.seeease.flywheel.serve.pricing.service.BillPricingService;
import com.seeease.flywheel.serve.purchase.convert.PurchaseConverter;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLineDetailsVO;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchasePaymentMethodEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.event.PurchaseUploadEvent;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseReturnLineService;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseService;
import com.seeease.flywheel.serve.purchase.strategy.PurchaseContext;
import com.seeease.flywheel.serve.qt.enums.QualityTestingStateEnum;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import com.seeease.flywheel.serve.recycle.service.IRecycleOrderService;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import com.seeease.flywheel.serve.sale.entity.BuyBackPolicyMapper;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderLineService;
import com.seeease.flywheel.serve.sale.service.BillSaleOrderService;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.flywheel.serve.storework.service.BillStoreWorkPreService;
import com.seeease.flywheel.storework.request.StoreWorKCreateRequest;
import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.LoginRole;
import com.seeease.springframework.context.LoginUser;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.BusinessException;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/1/7
 */
@DubboService(version = "1.0.0")
@Slf4j
@Component
public class PurchaseFacade implements IPurchaseFacade {

    private final static List<Double> DISCOUNT_RANGE = Lists.newArrayList(9.5d, 9d, 8.5d, 8d, 7.5d, 7d, 6.5d, 6d, 5.5d, 5d, 4.5d, 4d);

    @Resource
    private BillPurchaseService billPurchaseService;

    @Resource
    private BillPurchaseLineService billPurchaseLineService;

    @Resource
    private BillStoreWorkPreService billStoreWorkPreService;

    @Resource
    private PurchaseSubjectService purchaseSubjectService;

    @Resource
    private TagService tagService;

    @Resource
    private CustomerService customerService;
    @Resource
    private AccountStockRelationService stockRelationService;
    @Resource
    private CustomerContactsService customerContactsService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private PurchaseContext purchaseContext;

    @Resource
    private ApplyFinancialPaymentService applyFinancialPaymentService;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Resource
    private BillSaleOrderService billSaleOrderService;

    @Resource
    private BillSaleOrderLineService billSaleOrderLineService;

    @Resource
    private GoodsWatchService goodsWatchService;
    @Resource
    private BillPricingService pricingService;
    @Resource
    private StockService stockService;
    @Resource
    private SeriesService seriesService;
    @Resource
    private UserService userService;

    @Resource
    private FinancialDocumentsService financialDocumentsService;

    @Resource
    private AccountsPayableAccountingService accountingService;
    @Resource
    private IRecycleOrderService recycleOrderService;
    @Resource
    private BillPurchaseReturnLineService billPurchaseReturnLineService;

    @Resource
    private BillHandlerEventPublisher billHandlerEventPublisher;
    @Resource
    private AccountStockRelationMapper accountStockRelationMapper;
    @Resource
    private FinancialStatementCompanyService financialStatementCompanyService;
    @Resource
    private AccountReceiptConfirmService accountReceiptConfirmService;

    private static final Set<Integer> PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.TH_CG_PL.getValue(),
            BusinessBillTypeEnum.TH_CG_DJ.getValue(), BusinessBillTypeEnum.TH_CG_BH.getValue(),
            BusinessBillTypeEnum.GR_HS_JHS.getValue(), BusinessBillTypeEnum.GR_HS_ZH.getValue(),
            BusinessBillTypeEnum.TH_CG_QK.getValue(), BusinessBillTypeEnum.TH_CG_DJTP.getValue());
    private static final Set<Integer> GRHG_PURCHASE_TYPE = ImmutableSet.of(BusinessBillTypeEnum.GR_HG_ZH.getValue(), BusinessBillTypeEnum.GR_HG_JHS.getValue());
    private static final Set<String> ROLE_NAMES = ImmutableSet.of("admin", "总部查看", "财务查看");

    @Override
    public PageResult<PurchaseListResult> list(PurchaseListRequest request) {

        LoginUser user = UserContext.getUser();

        if (user.getRoles().stream().anyMatch(v -> v.getRoleName().equals("admin"))){
            request.setStoreId(null);
        }else {
            Integer id = user.getStore().getId();
            if (id != 1){
                request.setStoreId(id);
            }
        }
//
//        if (CollectionUtils.isNotEmpty(UserContext.getUser().getRoles()) &&
//                UserContext.getUser().getRoles().stream().map(LoginRole::getRoleName).anyMatch(ROLE_NAMES::contains)) {
//            request.setStoreId(null);
//        } else {
//            request.setStoreId(UserContext.getUser().getStore().getId());
//        }
        //重置采购类型条件，前端-1=全部
        request.setPurchaseType(Optional.ofNullable(request.getPurchaseType())
                .filter(v -> v != -1)
                .orElse(null));

        request.setPurchaseMode(Optional.ofNullable(request.getPurchaseMode())
                .filter(v -> v != -1)
                .orElse(null));
        //重置采购状态条件，前端-1=全部
        request.setPurchaseState(Optional.ofNullable(request.getPurchaseState())
                .filter(v -> v != -1)
                .orElse(null));

        List<Customer> customerList = null;
        //客户信息查询条件
        if (StringUtils.isNotBlank(request.getCustomerName())) {
            customerList = customerService.searchByName(request.getCustomerName());
            if (CollectionUtils.isEmpty(customerList)) {
                return PageResult.<PurchaseListResult>builder()
                        .result(Collections.EMPTY_LIST)
                        .build();
            }
            request.setCustomerIdList(customerList.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList()));
        }

        Page<BillPurchase> billPurchasePage = billPurchaseService.listByRequest(request);
        //补充客户信息
        Map<Integer/**customerId**/, Customer> customerMap = Optional.ofNullable(customerList)
                .orElseGet(() -> customerService.findCustomer(billPurchasePage.getRecords()
                        .stream()
                        .map(BillPurchase::getCustomerId)
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));

        Map<Integer, Customer> finalCustomerMap = customerMap;
        List<PurchaseListResult> result = Optional.ofNullable(billPurchasePage.getRecords())
                .orElse(Lists.newArrayList())
                .stream()
                .map(t -> {
                    PurchaseListResult r = PurchaseConverter.INSTANCE.convertPurchaseListResult(t);
                    r.setCustomerName(Optional.ofNullable(finalCustomerMap.get(t.getCustomerId())).map(Customer::getCustomerName).orElse(null));
                    return r;
                })
                .collect(Collectors.toList());

        return PageResult.<PurchaseListResult>builder()
                .result(result)
                .totalCount(billPurchasePage.getTotal())
                .totalPage(billPurchasePage.getPages())
                .build();
    }

    @Override
    public PurchaseDetailsResult details(PurchaseDetailsRequest request) {
        BillPurchase purchase = Optional.ofNullable(request)
                .filter(t -> Objects.nonNull(t.getId()) || StringUtils.isNotBlank(t.getSerialNo()))
                .map(t -> billPurchaseService.getOne(Wrappers.<BillPurchase>lambdaQuery()
                        .eq(BillPurchase::getId, t.getId())
                        .or().eq(BillPurchase::getSerialNo, t.getSerialNo())))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_BILL_NOT_EXIST));

        PurchaseDetailsResult result = PurchaseConverter.INSTANCE.convertPurchaseDetailsResult(purchase);

        //客户信息
        Customer customer = Optional.ofNullable(purchase.getCustomerId())
                .map(customerService::getById)
                .orElse(null);
        if (Objects.nonNull(customer)) {
            result.setCustomerName(customer.getCustomerName());
            log.info("采购单的银行信息boolean为：{},值信息：{}", StringUtils.isNotEmpty(purchase.getAccountName()), purchase.getAccountName());
            result.setAccountName(StringUtils.isNotEmpty(purchase.getAccountName()) ? purchase.getAccountName() : customer.getAccountName());
            log.info("返回赋值之后信息：{}", result.getAccountName());
            result.setBank(customer.getBank());
            result.setBankAccount(customer.getBankAccount());
            result.setIdentityCard(customer.getIdentityCard());
        }

        //联系人信息
        CustomerContacts customerContacts = Optional.ofNullable(purchase.getCustomerContactId())
                .map(customerContactsService::getById)
                .orElse(null);
        if (Objects.nonNull(customerContacts)) {
            result.setContactName(customerContacts.getName());
            result.setContactPhone(customerContacts.getPhone());
            result.setContactAddress(customerContacts.getAddress());
        }

        //采购主体
        PurchaseSubject purchaseSubject = Optional.ofNullable(purchase.getPurchaseSubjectId())
                .map(purchaseSubjectService::getById)
                .orElse(null);
        if (Objects.nonNull(purchaseSubject)) {
            result.setPurchaseSubjectName(purchaseSubject.getName());
        }

        //需求门店
        Tag tag = Optional.ofNullable(purchase.getDemanderStoreId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);
        if (Objects.nonNull(tag)) {
            result.setDemanderStoreName(tag.getTagName());
        }

        Tag recycle = Optional.ofNullable(purchase.getRecyclerId())
                .map(tagService::selectByStoreManagementId)
                .orElse(null);
        if (Objects.nonNull(recycle)) {
            result.setRecyclerName(recycle.getTagName());
        }


        //流转采购主体
        PurchaseSubject viaSubject = Optional.ofNullable(purchase.getViaSubjectId())
                .map(purchaseSubjectService::getById)
                .orElse(null);
        if (Objects.nonNull(viaSubject)) {
            result.setViaSubjectName(viaSubject.getName());
        }

        //流转采购主体
        User user = Optional.ofNullable(purchase.getPurchaseId())
                .map(userService::getById)
                .orElse(null);
        if (Objects.nonNull(user)) {
            result.setPurchaseBy(user.getName());
        }

        List<DictData> dataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        List<BillPurchaseLineDetailsVO> lineDetailsVOList = billPurchaseLineService.selectByPurchaseId(purchase.getId());

        lineDetailsVOList.forEach(billPurchaseLineDetailsVO -> billPurchaseLineDetailsVO.setAttachment(convert(dataList, JSONObject.parseArray(billPurchaseLineDetailsVO.getAttachmentList(), Integer.class),
                billPurchaseLineDetailsVO.getIsCard(), billPurchaseLineDetailsVO.getWarrantyDate(), billPurchaseLineDetailsVO.getGoodsId())));

        result.setLines(PurchaseConverter.INSTANCE.convertPurchaseLineVO(lineDetailsVOList));
        BigDecimal difference = BigDecimal.ZERO;
        switch (purchase.getPurchaseSource()) {
            case GR_HS_ZH:
                difference = purchase.getSalePrice().subtract(purchase.getTotalPurchasePrice());
                break;
            case GR_HG_ZH:

                difference = purchase.getSalePrice().subtract(purchase.getTotalPurchasePrice()).subtract(lineDetailsVOList.stream().map
                        (billPurchaseLineDetailsVO -> Optional.ofNullable(billPurchaseLineDetailsVO.getRecycleServePrice()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add));
                break;
            default:
                break;
        }
        result.setDifference(difference);
        //-1 支 0 平 1 收
        result.setDifferenceType(difference.signum());

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PurchaseExportResult> export(PurchaseExportRequest request) {
        PurchaseListRequest newReq = new PurchaseListRequest();
        BeanUtils.copyProperties(request, newReq);
        newReq.setPage(1);
        newReq.setLimit(Integer.MAX_VALUE);

        ArrayList<Integer> purchaseStatus = Lists.newArrayList(PurchaseLineStateEnum.IN_RETURN.getValue(),
                PurchaseLineStateEnum.RETURNED.getValue(),
                PurchaseLineStateEnum.ORDER_CANCEL_WHOLE.getValue());

        return this.list(newReq).getResult()
                .stream()
                .map(v -> {
                    //id 过滤
                    if (request.getIds() != null && !request.getIds().isEmpty()) {
                        if (!request.getIds().contains(v.getId())) {
                            return Collections.<PurchaseExportResult>emptyList();
                        }
                    }


                    Map<String, List<Map<String, Object>>> map = billPurchaseLineService.selectByPurchaseId(v.getId())
                            .stream()
                            .map(line -> {

                                Map<String, Object> ret = JSONObject.parseObject(JSONObject.toJSONString(line), HashMap.class);

                                if (purchaseStatus.contains(line.getPurchaseLineState())) {
                                    PurchaseLineStateEnum purchaseLineStateEnum = PurchaseLineStateEnum.fromValue(line.getPurchaseLineState());
                                    ret.put("status", purchaseLineStateEnum.getDesc());
                                    return ret;
                                }

                                LambdaQueryWrapper<BillPurchaseReturnLine> wq = Wrappers
                                        .<BillPurchaseReturnLine>lambdaQuery().
                                        eq(BillPurchaseReturnLine::getStockId, line.getStockId())
                                        .orderByDesc(BillPurchaseReturnLine::getId)
                                        .last("limit 1");

                                BillPurchaseReturnLine one = billPurchaseReturnLineService.getOne(wq);


                                if (one != null) {
                                    ret.put("status", one.getPurchaseReturnLineState().getDesc());
                                } else {
                                    ret.put("status", "");
                                }
                                return ret;

                            }).collect(Collectors.groupingBy(item -> (String) item.get("status")));


                    StringBuffer contactName = new StringBuffer();

                    //获取打款人信息
                    if (StringUtils.isNotEmpty(v.getApplyPaymentSerialNo())) {
                        LambdaQueryWrapper<ApplyFinancialPayment> wq = Wrappers.<ApplyFinancialPayment>lambdaQuery()
                                .eq(ApplyFinancialPayment::getSerialNo, v.getApplyPaymentSerialNo());

                        ApplyFinancialPayment one = applyFinancialPaymentService.getOne(wq);
                        if (one != null) {
                            contactName.append(one.getBankCustomerName());
                        }
                    }

                    //组合数据
                    return map.entrySet()
                            .stream()
                            .map(e -> {
                                return e.getValue()
                                        .stream()
                                        .map(line -> {
                                            PurchaseExportResult result = new PurchaseExportResult();
                                            result.setContactName(contactName.toString());
                                            result.setContactPhone(customerContactsService.getById(v.getCustomerContactId()).getPhone());
                                            result.setId((int) line.get("id"));
                                            result.setSerialNo(v.getSerialNo());
                                            result.setStatus(e.getKey());
                                            result.setCreatedTime(v.getCreatedTime());
                                            result.setPurchasePrice(((BigDecimal) line.get("purchasePrice")).toPlainString());
                                            //采购人
                                            Optional.ofNullable(v.getPurchaseId())
                                                    .map(userService::getById)
                                                    .ifPresent(user -> result.setPurchaseBy(user.getName()));

                                            //商品数据
                                            result.setProductTitle((String) line.get("brandName") + line.get("model"));
                                            result.setSerialNo(v.getSerialNo());
                                            result.setSn((String) line.get("stockSn"));
                                            result.setFineness((String) line.get("finess"));
                                            return result;

                                        }).collect(Collectors.toList());
                            })
                            .flatMap(List::stream)
                            .collect(Collectors.toList());


                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

    }

    @Override
    public PurchaseCreateListResult create(PurchaseCreateRequest request) {

        //新增采购单
        PurchaseCreateListResult result = purchaseContext.create(request);

        //查询当前登陆用户的简码
        result.setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes());


        return result;
    }

    @Override
    public PurchaseEditResult edit(PurchaseEditRequest request) {
        return billPurchaseService.edit(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseCancelResult cancel(PurchaseCancelRequest request) {
        return billPurchaseService.cancel(request);
    }

    @Resource
    private PaymentCTemplate paymentCTemplate;
    @Resource
    private PaymentETemplate paymentETemplate;

    @Resource
    private AccountStockRelationService accountStockRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseExpressNumberUploadListResult uploadExpressNumber(PurchaseExpressNumberUploadRequest request) {
        //上传单号
        PurchaseExpressNumberUploadListResult result = billPurchaseService.uploadExpressNumber(request);

        List<BillPurchaseLine> lines = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, result.getId()));

        switch (BusinessBillTypeEnum.fromValue(result.getPurchaseSource())) {
            case TH_CG_BH:
            case TH_CG_DJ:
            case GR_HS_JHS:

                Integer i = applyFinancialPaymentService.getOne(Wrappers.<ApplyFinancialPayment>lambdaQuery().eq(ApplyFinancialPayment::getSerialNo, result.getApplyPaymentSerialNo())).getId();
                //申请打款单绑定
                for (BillPurchaseLine line : lines) {

                    AccountStockRelation a = new AccountStockRelation();
                    a.setStockId(line.getStockId());
                    a.setAfpId(i);
                    a.setOriginPrice(line.getPurchasePrice());
                    a.setOriginSerialNo(result.getSerialNo());

                    accountStockRelationService.save(a);
                }

                //创建预付单
                paymentCTemplate.generatePrepaid(new JSONObject()
                        .fluentPut("purchase", billPurchaseService.getById(result.getId()))
                        .fluentPut("purchaseLine", lines)
                );
                break;
            case GR_HS_ZH:

                paymentETemplate.createPaymentAndGeneratePayable(new JSONObject()
                        .fluentPut("purchase", billPurchaseService.getById(result.getId()))
                        .fluentPut("purchaseLine", lines)
                );
                break;

            default:
                break;
        }

        //同行采购 填写快递单号后 生成财务
        if (PURCHASE_TYPE.contains(result.getPurchaseSource())) {
            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(result.getId());
            dto.setStockList(lines.stream().map(BillPurchaseLine::getStockId).collect(Collectors.toList()));
            dto.setType(result.getPurchaseSource());
            financialDocumentsService.generatePurchase(dto);
        }

        //通知商城?? todo
//        recycleOrderService.checkIntercept(result.getSerialNo());
        //更改操作
//        Map<String, Integer> map = lines.stream().collect(Collectors.toMap(BillPurchaseLine::getStockSn, BillPurchaseLine::getStockId));
//        List<AccountsPayableAccounting> list = accountingService.selectListByOriginSerialNoAndStatusAndType(result.getSerialNo(), Lists.newArrayList(FinancialStatusEnum.PENDING_REVIEW, FinancialStatusEnum.IN_REVIEW, FinancialStatusEnum.RETURN_PENDING_REVIEW, FinancialStatusEnum.AUDITED), Lists.newArrayList(ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT, ReceiptPaymentTypeEnum.PRE_RECEIVE_AMOUNT, ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE));
//        list.forEach(accountsPayableAccounting -> {
//            if (ObjectUtils.isEmpty(accountsPayableAccounting.getStockId()) && map.containsKey(accountsPayableAccounting.getStockSn())) {
//                AccountsPayableAccounting accounting = new AccountsPayableAccounting();
//                accounting.setId(accountsPayableAccounting.getId());
//                accounting.setStockId(map.getOrDefault(accountsPayableAccounting.getStockSn(), -1));
//                accountingService.updateById(accounting);
//            }
//        });

        //生成入库单 总部出库单
        List<StoreWorKCreateRequest> storeWorKCreateRequests = lines
                .stream()
                .map(t -> StoreWorKCreateRequest.builder()
                        .goodsId(t.getGoodsId())
                        .stockId(t.getStockId())
                        .expressNumber(request.getExpressNumber())
                        .originSerialNo(result.getSerialNo())
                        .workSource(result.getPurchaseSource())
                        .customerId(result.getCustomerId())
                        .customerContactId(result.getCustomerContactId())
                        .workType(StoreWorkTypeEnum.INT_STORE.getValue())
                        .belongingStoreId(FlywheelConstant._ZB_ID)
                        .build())
                .collect(Collectors.toList());

        //创建入库作业
        List<StoreWorkCreateResult> storeWorkList = billStoreWorkPreService.create(storeWorKCreateRequests);

        //入库单结果
        result.setStoreWorkList(storeWorkList);

        result.setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes());

        billHandlerEventPublisher.publishEvent(new PurchaseUploadEvent(result.getId()
                , PurchaseTypeEnum.fromCode(result.getPurchaseType())));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseExpressNumberUploadListResult shopReceiving(PurchaseExpressNumberUploadRequest request) {

        PurchaseExpressNumberUploadListResult result = billPurchaseService.shopReceiving(request);

        result.setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes());

        billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.IN_RETURN).build());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseExpressNumberUploadListResult confirmReturn(PurchaseExpressNumberUploadRequest request) {

        PurchaseExpressNumberUploadListResult result = billPurchaseService.confirmReturn(request);

        result.setShortcodes(tagService.selectByStoreManagementId(request.getStoreId()).getShortcodes());

        billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.RETURNED).build());

        //直接更改商品状态
        stockService.updateStockStatus(Arrays.asList(result.getStoreWorkList().get(0).getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_RETURNED_ING_PURCHASE_RETURNED);

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseAcceptRepairResult acceptRepair(PurchaseAcceptRepairRequest request) {

        BillPurchaseLine billPurchaseLine = billPurchaseLineService.getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId()));

        //更新质检单
        billQualityTestingService.update(billPurchaseLine.getStockId(), request.getAcceptState().intValue() == 0 ? 1 : 0, QualityTestingStateEnum.CONFIRM_FIX);

        //关联单据
        BillPurchase billPurchase = billPurchaseService.getById(request.getPurchaseId());
        //0 否 退货 1 是 维修
        if (WhetherEnum.fromValue(request.getAcceptState()) == WhetherEnum.NO) {
            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.IN_RETURN).build());
            //直接修改商品状态
            stockService.updateStockStatus(Arrays.asList(billPurchaseLine.getStockId()), StockStatusEnum.TransitionEnum.PURCHASE_IN_TRANSIT_PURCHASE_RETURNED_ING);
        } else if (WhetherEnum.fromValue(request.getAcceptState()) == WhetherEnum.YES) {
            billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.IN_FIX_INSPECTION).isSettlement(WhetherEnum.YES).build());
            //商城客户接受维修 RecycleOrderProducers
            recycleOrderService.checkIntercept(billPurchase.getSerialNo());
        }

        StoreWorkCreateResult storeWorkCreateResult = new StoreWorkCreateResult();
        storeWorkCreateResult.setStockId(billPurchaseLine.getStockId());

        PurchaseAcceptRepairResult build = PurchaseAcceptRepairResult.builder()
                .id(request.getPurchaseId())
                .serialNo(billPurchase.getSerialNo())
                .storeWorkList(Arrays.asList(storeWorkCreateResult))
                .build();

        return build;
    }

    @Resource
    private PaymentDTemplate paymentDTemplate;
    @Resource
    private PaymentFTemplate paymentFTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applySettlement(PurchaseApplySettlementRequest request) {

        BillPurchase billPurchase = billPurchaseService.getById(request.getPurchaseId());

        if (GRHG_PURCHASE_TYPE.contains(billPurchase.getPurchaseSource().getValue())) {
            BillPurchaseLine purchaseLine = billPurchaseLineService
                    .getOne(Wrappers.<BillPurchaseLine>lambdaQuery().eq(BillPurchaseLine::getPurchaseId, request.getPurchaseId()));
            //修改价格
//            purchaseLine.setOldPlanFixPrice(purchaseLine.getPlanFixPrice());
//            purchaseLine.setOldWatchbandReplacePrice(purchaseLine.getWatchbandReplacePrice());
            purchaseLine.setPlanFixPrice(request.getPlanFixPrice());
            purchaseLine.setWatchbandReplacePrice(request.getWatchbandReplacePrice());

//            BigDecimal tempPrice = purchaseLine.getReferenceBuyBackPrice()
//                    .subtract(request.getPlanFixPrice())
//                    .subtract(request.getWatchbandReplacePrice());
//
//
//            if (tempPrice.compareTo(purchaseLine.getConsignmentPrice()) > -1){
//                purchaseLine.setPurchasePrice(purchaseLine.getConsignmentPrice());
//                purchaseLine.setRecycleServePrice(tempPrice.subtract(purchaseLine.getConsignmentPrice()));
//            }else {
//                purchaseLine.setPurchasePrice(tempPrice);
//            }
            billPurchaseLineService.updateById(purchaseLine);
        }

        //申请打款单参数
        PurchaseApplySettlementResult purchaseApplySettlementResult = billPurchaseService.applySettlement(request);

        //todo 确认收款单参数
        AccountReceiptConfirm accountReceiptConfirm = null;

        ApplyFinancialPayment byId = null;

        //个人回购 质检填写预估维修价，门店申请结算后
        if (GRHG_PURCHASE_TYPE.contains(purchaseApplySettlementResult.getPurchaseSource())) {

            FinancialGenerateDto dto = new FinancialGenerateDto();
            dto.setId(purchaseApplySettlementResult.getPurchaseId());
            dto.setStockList(Lists.newArrayList(purchaseApplySettlementResult.getStockId()));
            dto.setType(purchaseApplySettlementResult.getPurchaseSource());
            financialDocumentsService.generatePurchase(dto);

            if (Objects.equals(purchaseApplySettlementResult.getPurchaseSource(), BusinessBillTypeEnum.GR_HG_ZH.getValue())) {
                //当付款方式为全款时）
                if (PurchasePaymentMethodEnum.FK_QK.equals(billPurchase.getPaymentMethod())) {
                    //回购置换，在客户同意并申请结算提交后创建应付单，商品入库并关联打款单已打款后自动核销，核销说明：入库自动核销
//                    createAfp(request, purchaseApplySettlementResult, ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
                    ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(purchaseApplySettlementResult);

                    createRequest.setBankAccount(request.getBank());
                    createRequest.setBankCard(request.getBankAccount());
                    createRequest.setBankName(request.getAccountName());
                    createRequest.setBankCustomerName(request.getBankCustomerName());
                    createRequest.setMerchandiseNews(purchaseApplySettlementResult.getStockSn());
                    createRequest.setCustomerName(purchaseApplySettlementResult.getCustomerName());
                    createRequest.setShopId(request.getStoreId());
                    createRequest.setWhetherUse(1);
                    createRequest.setBuyBackTransfer(purchaseApplySettlementResult.getBuyBackTransfer());

                    createRequest.setPayment(ApplyFinancialPaymentEnum.PAID.getValue());
                    createRequest.setOriginSerialNo(purchaseApplySettlementResult.getSerialNo());
                    createRequest.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT.getValue());
                    createRequest.setSubjectPayment(21);
                    createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
                    createRequest.setPricePayment(purchaseApplySettlementResult.getSettlementPrice());
                    byId = applyFinancialPaymentService.getById(createAfpAnd(request, createRequest, purchaseApplySettlementResult));
                    paymentFTemplate.generatePayable(new JSONObject().fluentPut("afp", byId));

//                    accountingService.createApa(purchaseApplySettlementResult.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE, FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(purchaseApplySettlementResult.getStockId()), purchaseApplySettlementResult.getSettlementPrice(), false);
                    purchaseApplySettlementResult.setPricePayment(purchaseApplySettlementResult.getSettlementPrice());

                } else if (PurchasePaymentMethodEnum.FK_CE.equals(billPurchase.getPaymentMethod())) {

                    int compareTo = purchaseApplySettlementResult.getSettlementPrice().compareTo(billPurchase.getSalePrice());

                    if (compareTo > 0) {
                        //2’回购置换，在客户同意并申请结算提交后创建应付单，商品入库并关联打款单已打款后自动核销，核销说明：入库自动核销
//                        accountingService.createApa(billPurchase.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE, FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(purchaseApplySettlementResult.getStockId()), purchaseApplySettlementResult.getSettlementPrice(), false);
//
//                        //生成申请打款单金额为差额 申请打款单 回购置换-客户同意后主动创建
//                        createAfp(request, purchaseApplySettlementResult, ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
                        ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(purchaseApplySettlementResult);

                        createRequest.setBankAccount(request.getBank());
                        createRequest.setBankCard(request.getBankAccount());
                        createRequest.setBankName(request.getAccountName());
                        createRequest.setBankCustomerName(request.getBankCustomerName());
                        createRequest.setMerchandiseNews(purchaseApplySettlementResult.getStockSn());
                        createRequest.setCustomerName(purchaseApplySettlementResult.getCustomerName());
                        createRequest.setShopId(request.getStoreId());
                        createRequest.setWhetherUse(1);
                        createRequest.setBuyBackTransfer(purchaseApplySettlementResult.getBuyBackTransfer());

                        createRequest.setPayment(ApplyFinancialPaymentEnum.PAID.getValue());
                        createRequest.setOriginSerialNo(purchaseApplySettlementResult.getSerialNo());
                        createRequest.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_D.getValue());
                        createRequest.setSubjectPayment(21);
                        createRequest.setPricePayment(purchaseApplySettlementResult.getSettlementPrice().subtract(billPurchase.getSalePrice()));
                        createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
                        byId = applyFinancialPaymentService.getById(createAfpAnd(request, createRequest, purchaseApplySettlementResult));
                        paymentFTemplate.generatePayable(new JSONObject().fluentPut("afp", byId));

                    } else if (compareTo < 0) {
                        //确认收款
                        accountReceiptConfirm = accountReceiptConfirmService.accountReceiptConfirmAdd(AccountReceiptConfirmAddRequest.builder()
                                .receivableAmount(billPurchase.getTotalPurchasePrice().negate())
                                .collectionType(CollectionTypeEnum.XF_TK.getValue())
                                .waitAuditPrice(billPurchase.getTotalPurchasePrice())
                                .status(AccountReceiptConfirmStatusEnum.WAIT.getValue())
                                .shopId(billPurchase.getStoreId())
                                .miniAppSource(Boolean.FALSE)
                                .classification(FinancialClassificationEnum.GR_HS.getValue())
                                .salesMethod(FinancialSalesMethodEnum.PURCHASE_D.getValue())
                                .payer("-")
                                .originSerialNo(billPurchase.getSerialNo())
                                .statementCompanyId(financialStatementCompanyService.list(Wrappers.<FinancialStatementCompany>lambdaQuery()
                                        .like(FinancialStatementCompany::getSubjectId, billPurchase.getPurchaseSubjectId())).stream().findFirst().get().getId())
                                .customerName(customerService.getById(billPurchase.getCustomerId()).getCustomerName())
                                .contactPhone(customerContactsService.getById(billPurchase.getCustomerContactId()).getPhone())
                                .build());

                        AccountStockRelation accountStockRelation = new AccountStockRelation();
                        accountStockRelation.setOriginSerialNo(billPurchase.getSerialNo());
                        BillPurchaseLine billPurchaseLine = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                                .eq(BillPurchaseLine::getPurchaseId, billPurchase.getId())).stream().findFirst().get();
                        accountStockRelation.setOriginPrice(billPurchaseLine.getPurchasePrice());
                        accountStockRelation.setStockId(billPurchaseLine.getStockId());
                        accountStockRelation.setArcId(accountReceiptConfirm.getId());

                        accountStockRelationMapper.insert(accountStockRelation);

                        paymentFTemplate.generatePayable(new JSONObject().fluentPut("arc", accountReceiptConfirmService.getById(accountReceiptConfirm.getId())));
                    } else {
                        log.warn("收支平衡,serialNo={}", billPurchase.getSerialNo());
                        // 2’回购置换，在客户同意并申请结算提交后创建应付单，商品入库并关联打款单已打款后自动核销，核销说明：入库自动核销
                        // todo
                        //单独应收应付。。。。 阿西吧
                        accountingService.createApa(billPurchase.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE, FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(purchaseApplySettlementResult.getStockId()), purchaseApplySettlementResult.getSettlementPrice(), false);
                    }
                }

//                if (purchaseApplySettlementResult.getSalePrice().compareTo(purchaseApplySettlementResult.getTotalPurchasePrice()) < 0) {
//                    //结算后（成交价-回收价＜0）提交后 申请打款单生成
//                    createAfp(request, purchaseApplySettlementResult, ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
//                } else {
//                    //结算后 成交价-回收价＞0提交后 应收单生成
//                    accountingService.createApa(purchaseApplySettlementResult.getSerialNo(),
//                            ReceiptPaymentTypeEnum.AMOUNT_RECEIVABLE, FinancialStatusEnum.PENDING_REVIEW,
//                            Lists.newArrayList(purchaseApplySettlementResult.getStockId()),
//                            purchaseApplySettlementResult.getSalePrice().subtract(purchaseApplySettlementResult.getTotalPurchasePrice()), false);
//                }
            } else if (Objects.equals(purchaseApplySettlementResult.getPurchaseSource(), BusinessBillTypeEnum.GR_HG_JHS.getValue())) {
                //createAfp(request, purchaseApplySettlementResult, ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());

                ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(purchaseApplySettlementResult);

                createRequest.setBankAccount(request.getBank());
                createRequest.setBankCard(request.getBankAccount());
                createRequest.setBankName(request.getAccountName());
                createRequest.setBankCustomerName(request.getBankCustomerName());
                createRequest.setMerchandiseNews(purchaseApplySettlementResult.getStockSn());
                createRequest.setCustomerName(purchaseApplySettlementResult.getCustomerName());
                createRequest.setShopId(request.getStoreId());
                createRequest.setWhetherUse(1);
                createRequest.setBuyBackTransfer(purchaseApplySettlementResult.getBuyBackTransfer());
                createRequest.setOriginSerialNo(purchaseApplySettlementResult.getSerialNo());

                createRequest.setPayment(ApplyFinancialPaymentEnum.PAID.getValue());
                createRequest.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT.getValue());
                createRequest.setSubjectPayment(21);
                createRequest.setPricePayment(purchaseApplySettlementResult.getSettlementPrice());
                createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.BUY_BACK.getValue());
                byId = applyFinancialPaymentService.getById(createAfpAnd(request, createRequest, purchaseApplySettlementResult));
                paymentFTemplate.generatePayable(new JSONObject().fluentPut("afp", byId));

            }
            BillPurchase purchase = new BillPurchase();
            purchase.setId(request.getPurchaseId());
            purchase.setIsSettlement(WhetherEnum.NO);
            purchase.setApplyPaymentSerialNo(Objects.nonNull(byId) ? byId.getSerialNo() : null);
            billPurchaseService.updateById(purchase);

        } else if (Objects.equals(purchaseApplySettlementResult.getPurchaseSource(), BusinessBillTypeEnum.GR_JS.getValue())) {
            //个人寄售-结算 业务方自主操作结算后 申请打款单生成

            ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(purchaseApplySettlementResult);

            createRequest.setBankAccount(request.getBank());
            createRequest.setBankCard(request.getBankAccount());
            createRequest.setBankName(request.getAccountName());
            createRequest.setBankCustomerName(request.getBankCustomerName());
            createRequest.setMerchandiseNews(purchaseApplySettlementResult.getStockSn());
            createRequest.setCustomerName(purchaseApplySettlementResult.getCustomerName());
            createRequest.setShopId(request.getStoreId());
            createRequest.setWhetherUse(1);
            createRequest.setBuyBackTransfer(purchaseApplySettlementResult.getBuyBackTransfer());

            createRequest.setPayment(ApplyFinancialPaymentEnum.REJECTED.getValue());
            createRequest.setSalesMethod(FinancialSalesMethodEnum.FULL_PAYMENT.getValue());
            createRequest.setSubjectPayment(21);
            createRequest.setPricePayment(purchaseApplySettlementResult.getPricePayment());
            createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.SEND_PERSON.getValue());
            Integer afpAnd = createAfpAnd(request, createRequest, purchaseApplySettlementResult);
            if (DateUtil.between(DateUtil.parse("2024-03-01 00:00:00"), purchaseApplySettlementResult.getTime(), DateUnit.SECOND, false) >= 0) {
                paymentDTemplate.updatePayable(new JSONObject().fluentPut("afp", applyFinancialPaymentService.getById(afpAnd)));
            }

//                        accountingService.createApa(purchaseApplySettlementResult.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE, FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(purchaseApplySettlementResult.getStockId()), null, false);

            BillPurchase purchase = new BillPurchase();
            purchase.setId(request.getPurchaseId());
            purchase.setIsSettlement(WhetherEnum.NO);
            billPurchaseService.updateById(purchase);
        }

        billPurchaseLineService.noticeListener(PurchaseLineNotice.builder().purchaseId(request.getPurchaseId()).lineState(PurchaseLineStateEnum.IN_SETTLED).build());
    }

    private void createAfp(PurchaseApplySettlementRequest request, PurchaseApplySettlementResult purchaseApplySettlementResult, Integer typePayment) {
        ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentConvert.INSTANCE.convertApplyFinancialPaymentCreateRequest(purchaseApplySettlementResult);
        createRequest.setTypePayment(typePayment);
        createRequest.setBankAccount(request.getBank());
        createRequest.setBankCard(request.getBankAccount());
        createRequest.setBankName(request.getAccountName());
        createRequest.setBankCustomerName(request.getBankCustomerName());
        createRequest.setMerchandiseNews(purchaseApplySettlementResult.getStockSn());
        createRequest.setCustomerName(purchaseApplySettlementResult.getCustomerName());
        createRequest.setShopId(request.getStoreId());
        createRequest.setWhetherUse(1);
        createRequest.setBuyBackTransfer(purchaseApplySettlementResult.getBuyBackTransfer());
        createRequest.setSalesMethod(PurchaseModeEnum.convert(PurchaseModeEnum.fromCode(purchaseApplySettlementResult.getPurchaseMode())).getValue());
        //回填打款单号
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);
        //更新关联单号
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setId(request.getPurchaseId());
        billPurchase.setApplyPaymentSerialNo(result.getSerialNo());
        billPurchase.setFrontIdentityCard(request.getFrontIdentityCard());
        billPurchase.setReverseIdentityCard(request.getReverseIdentityCard());
        billPurchase.setAgreementTransfer(request.getAgreementTransfer());
        billPurchase.setBuyBackTransfer(request.getBuyBackTransfer());
        billPurchase.setIsSettlement(null);
        billPurchase.setBatchPictureUrl(CollectionUtils.isNotEmpty(purchaseApplySettlementResult.getBatchPictureUrl()) ? purchaseApplySettlementResult.getBatchPictureUrl() : null);
        billPurchaseService.updateById(billPurchase);

        //申请打款单 和 商品关联关系
        stockRelationService.save(AccountStockRelation.builder()
                .afpId(result.getId())
                .stockId(purchaseApplySettlementResult.getStockId())
                .originSerialNo(purchaseApplySettlementResult.getSerialNo())
                .originPrice(result.getPricePayment())
                .build());
    }

    private Integer createAfpAnd(PurchaseApplySettlementRequest request, ApplyFinancialPaymentCreateRequest createRequest, PurchaseApplySettlementResult purchaseApplySettlementResult) {
        //回填打款单号
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);
        //更新关联单号
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setId(request.getPurchaseId());
        billPurchase.setApplyPaymentSerialNo(result.getSerialNo());
        billPurchase.setFrontIdentityCard(request.getFrontIdentityCard());
        billPurchase.setReverseIdentityCard(request.getReverseIdentityCard());
        billPurchase.setAgreementTransfer(request.getAgreementTransfer());
        billPurchase.setBuyBackTransfer(request.getBuyBackTransfer());
        billPurchase.setIsSettlement(null);
        billPurchase.setBatchPictureUrl(CollectionUtils.isNotEmpty(purchaseApplySettlementResult.getBatchPictureUrl()) ? purchaseApplySettlementResult.getBatchPictureUrl() : null);
        billPurchaseService.updateById(billPurchase);

        //申请打款单 和 商品关联关系
        stockRelationService.save(AccountStockRelation.builder().afpId(result.getId()).stockId(purchaseApplySettlementResult.getStockId()).originSerialNo(purchaseApplySettlementResult.getSerialNo()).originPrice(result.getPricePayment()).build());
        return result.getId();
    }

    @Override
    public PurchaseForSaleResult purchaseForSale(PurchaseForSaleRequest request) {

        //1。销售单是否存在
        BillSaleOrder billSaleOrder = billSaleOrderService.getOne(Wrappers.<BillSaleOrder>lambdaQuery()
                .eq(BillSaleOrder::getSerialNo, request.getOriginSaleSerialNo())
                .eq(BillSaleOrder::getSaleState, BusinessBillStateEnum.COMPLETE)
        );

        Optional.ofNullable(billSaleOrder).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER));

        //查询表是否存在回购
        List<BillPurchaseLine> purchaseLineList = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery()
                .eq(BillPurchaseLine::getOriginStockId, request.getOriginStockId()));
        //查询是否商城做过回购
        MallRecyclingOrder mallRecyclingOrder = recycleOrderService.queryBySaleSerialNo(request.getOriginSaleSerialNo());
        if (Objects.nonNull(mallRecyclingOrder)) {
            throw new OperationRejectedException(OperationExceptionCode.MALL_BUY_BACK_ORDER);
        }

        //是否存在相同的回购单
        if (CollectionUtils.isNotEmpty(purchaseLineList)) {
            List<BillPurchase> collect = billPurchaseService.listByIds(purchaseLineList.stream().map(BillPurchaseLine::getPurchaseId).collect(Collectors.toList()))
                    .stream().filter(billPurchase -> request.getOriginSaleSerialNo().equals(billPurchase.getOriginSaleSerialNo()) && !billPurchase.getPurchaseState().equals(BusinessBillStateEnum.CANCEL_WHOLE)).collect(Collectors.toList());
            //
            if (!collect.isEmpty()) {
                throw new OperationRejectedException(OperationExceptionCode.BUY_BACK_EXITS);
            }
        }

        //2。销售单行好是否存在
        BillSaleOrderLine billSaleOrderLine = billSaleOrderLineService.getOne(Wrappers.<BillSaleOrderLine>lambdaQuery()
                .eq(BillSaleOrderLine::getSaleId, billSaleOrder.getId())
                .eq(BillSaleOrderLine::getStockId, request.getOriginStockId())
                .eq(BillSaleOrderLine::getIsCounterPurchase, 1)
                .eq(BillSaleOrderLine::getIsRepurchasePolicy, 1)
        );

        Optional.ofNullable(billSaleOrderLine).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER));

        //3。回购政策是否满足
        List<BuyBackPolicyMapper> mappers = billSaleOrderLine.getBuyBackPolicy().stream().sorted(Comparator.comparing(BuyBackPolicyMapper::getBuyBackTime)).collect(Collectors.toList());

        //关联销售成交价
        BigDecimal clinchPrice = billSaleOrderLine.getClinchPrice();

        //仅置换
        ImmutableRangeMap.Builder<Comparable<Date>, BigDecimal> builderIn = ImmutableRangeMap.<Comparable<Date>, BigDecimal>builder();

        //仅回收
        ImmutableRangeMap.Builder<Comparable<Date>, BigDecimal> builderRecycle = ImmutableRangeMap.<Comparable<Date>, BigDecimal>builder();
        //差值 初始者
        AtomicReference<Integer> difference = new AtomicReference<>(0);
        //回购加点值
        mappers.forEach(buyBackPolicyMapper -> {
            // 确定 到天 前开后闭
            //YYYY_MM_DD ？？？？ 建单时间 初始时间
            Date date = DateUtils.stepMonth(DateUtil.parse(DateUtil.formatDate(billSaleOrder.getCreatedTime())), difference.get());

            //递增值
            Date month = DateUtils.stepMonth(DateUtil.parse(DateUtil.formatDate(billSaleOrder.getCreatedTime())), buyBackPolicyMapper.getBuyBackTime());

            //置换
            builderIn.put(Range.openClosed(date, month), buyBackPolicyMapper.getDiscount().add(buyBackPolicyMapper.getReplacementDiscounts()));

            //回收
            builderRecycle.put(Range.openClosed(date, month), buyBackPolicyMapper.getDiscount());

            difference.set(buyBackPolicyMapper.getBuyBackTime());
        });

        //建单时间 参考回购折扣 置换
        BigDecimal referenceBuyBackDiscount = Optional.ofNullable(builderIn.build().get(DateUtil.parse(DateUtil.today()).toJdkDate())).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE));
        //建单时间 参考回购价 置换
        BigDecimal referenceBuyBackInPrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                //值点
                referenceBuyBackDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

        //建单时间 参考回购折扣 回收
        BigDecimal referenceBuyBackRecycleDiscount = Optional.ofNullable(builderRecycle.build().get(DateUtil.parse(DateUtil.today()).toJdkDate())).orElseThrow(() -> new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE));
        //建单时间 参考回购价 回收
        BigDecimal referenceBuyBackRecyclePrice = BigDecimalUtil.multiplyRoundHalfUp(clinchPrice,
                //值点
                referenceBuyBackRecycleDiscount.divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_10), 4, BigDecimal.ROUND_HALF_UP));

        if (ObjectUtils.isEmpty(referenceBuyBackInPrice) || ObjectUtils.isEmpty(referenceBuyBackRecyclePrice)
                || referenceBuyBackInPrice.compareTo(BigDecimal.ZERO) <= 0 || referenceBuyBackRecyclePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationRejectedException(OperationExceptionCode.REFERENCE_BUY_BACK_PRICE);
        }

        //基本信息
        List<WatchDataFusion> watchDataFusionList = goodsWatchService.getWatchDataFusionListByStockIds(Arrays.asList(request.getOriginStockId()));

        Assert.notEmpty(watchDataFusionList, "基本信息为空");

        PurchaseForSaleResult purchaseForSaleResult = new PurchaseForSaleResult();

        WatchDataFusion watchDataFusion = watchDataFusionList.get(0);
        purchaseForSaleResult.setStockId(watchDataFusion.getStockId());
        purchaseForSaleResult.setStockSn(watchDataFusion.getStockSn());
        purchaseForSaleResult.setBrandName(watchDataFusion.getBrandName());
        purchaseForSaleResult.setSeriesName(watchDataFusion.getSeriesName());
        purchaseForSaleResult.setGoodsId(watchDataFusion.getGoodsId());
        purchaseForSaleResult.setModel(watchDataFusion.getModel());
        purchaseForSaleResult.setFiness(watchDataFusion.getFiness());
        purchaseForSaleResult.setAttachment(watchDataFusion.getAttachment());
        purchaseForSaleResult.setPricePub(watchDataFusion.getPricePub());

        //表带类型
        purchaseForSaleResult.setStrapMaterial(billSaleOrderLine.getStrapMaterial());
        purchaseForSaleResult.setWatchSection(billSaleOrderLine.getWatchSection());

        purchaseForSaleResult.setReferenceBuyBackRecyclePrice(referenceBuyBackRecyclePrice);
        purchaseForSaleResult.setReferenceBuyBackInPrice(referenceBuyBackInPrice);
        purchaseForSaleResult.setStrapReplacementPrice(billSaleOrderLine.getStrapReplacementPrice());
        purchaseForSaleResult.setClinchPrice(clinchPrice);

        purchaseForSaleResult.setDisplaceDiscountRange(DISCOUNT_RANGE.stream()
                .filter(t -> t <= referenceBuyBackDiscount.doubleValue())
                .map(Objects::toString)
                .collect(Collectors.toList())
        );
        purchaseForSaleResult.setRecycleDiscountRange(DISCOUNT_RANGE.stream()
                .filter(t -> t <= referenceBuyBackRecycleDiscount.doubleValue())
                .map(Objects::toString)
                .collect(Collectors.toList()));

        return purchaseForSaleResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeRecycle(PurchaseChangeRecycleRequest request) {

        BillPurchase purchase = billPurchaseService.changeRecycle(request);

        FinancialGenerateDto dto = new FinancialGenerateDto();
        dto.setId(purchase.getId());
        dto.setType(purchase.getPurchaseSource().getValue());
        dto.setStockList(Lists.newArrayList(request.getStockId()));
        financialDocumentsService.generatePurchase(dto);

        ApplyFinancialPaymentCreateRequest createRequest = new ApplyFinancialPaymentCreateRequest();
        createRequest.setTypePayment(ApplyFinancialPaymentTypeEnum.SEND_PERSON.getValue());
        createRequest.setPricePayment(purchase.getTotalPurchasePrice());
        createRequest.setSubjectPayment(purchase.getPurchaseSubjectId());
        createRequest.setRemarks(purchase.getRemarks());
        createRequest.setBankAccount(purchase.getBank());
        createRequest.setBankCard(purchase.getBankAccount());
        createRequest.setBankName(purchase.getAccountName());
        createRequest.setBankCustomerName(purchase.getBankCustomerName());
        createRequest.setCustomerContactsId(purchase.getCustomerContactId());
        createRequest.setCustomerName(customerContactsService.getById(purchase.getCustomerContactId()).getName());
        createRequest.setShopId(purchase.getStoreId());
        createRequest.setWhetherUse(1);
        createRequest.setSalesMethod(PurchaseModeEnum.convert(purchase.getPurchaseMode()).getValue());
        //回填打款单号
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);
        //更新关联单号
        BillPurchase billPurchase = new BillPurchase();
        billPurchase.setId(purchase.getId());
        billPurchase.setApplyPaymentSerialNo(result.getSerialNo());
        billPurchaseService.updateById(billPurchase);

        //增加申请打款单 和表的关系
        AccountStockRelation accountStockRelation = new AccountStockRelation();
        accountStockRelation.setAfpId(result.getId());
        accountStockRelation.setOriginPrice(result.getPricePayment());
        accountStockRelation.setStockId(request.getStockId());
        accountStockRelation.setOriginSerialNo(purchase.getSerialNo());
        accountStockRelationService.save(accountStockRelation);

        //增加个人寄售核销掉
        List<AccountsPayableAccounting> payableAccountingList = accountingService.list(Wrappers.<AccountsPayableAccounting>lambdaQuery()
                .eq(AccountsPayableAccounting::getType, ReceiptPaymentTypeEnum.AMOUNT_PAYABLE)
                .eq(AccountsPayableAccounting::getOriginSerialNo, billPurchaseService.getById(request.getPurchaseId()).getSerialNo())
                .eq(AccountsPayableAccounting::getStockId, request.getStockId())
        );

        if (CollectionUtils.isNotEmpty(payableAccountingList)) {
            accountingService.batchAudit(payableAccountingList.stream().map(AccountsPayableAccounting::getId).collect(Collectors.toList()), FlywheelConstant.EXCHANGE_R_AUDIT, UserContext.getUser().getUserName());
        }

        accountingService.createApa(purchase.getSerialNo(), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE,
                FinancialStatusEnum.PENDING_REVIEW, Lists.newArrayList(request.getStockId()), null, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void extendTime(PurchaseExtendTimeRequest request) {
        billPurchaseService.extendTime(request);
    }

    @Override
    public ImportResult<PurchaseStockQueryImportResult> stockQueryImport(PurchaseStockQueryImportRequest request) {

        List<PurchaseStockQueryImportRequest.ImportDto> dataList = request.getDataList();

        Set<String> errorList = new HashSet<>();

        List<String> snList = dataList.stream().map(PurchaseStockQueryImportRequest.ImportDto::getStockSn).collect(Collectors.toList());

        Lists.partition(snList, 500).forEach(lis -> {

            Map<String, BillPurchaseLine> collectByLine = billPurchaseLineService.list(Wrappers.<BillPurchaseLine>lambdaQuery().in(BillPurchaseLine::getStockSn, lis)
                    .notIn(BillPurchaseLine::getPurchaseLineState, Arrays.asList(
                            PurchaseLineStateEnum.RETURNED,
                            PurchaseLineStateEnum.ORDER_CANCEL_WHOLE,
                            PurchaseLineStateEnum.WAREHOUSED,
                            PurchaseLineStateEnum.IN_SETTLED
                    ))).stream().collect(Collectors.toMap(BillPurchaseLine::getStockSn, Function.identity()));

            errorList.addAll(collectByLine.keySet());

            Map<String, Stock> collectByStock = stockService.list(Wrappers.<Stock>lambdaQuery().in(Stock::getSn, lis)
                    .notIn(Stock::getStockStatus, Arrays.asList(
                            StockStatusEnum.PURCHASE_RETURNED_ING,
                            StockStatusEnum.SOLD_OUT,
                            StockStatusEnum.PURCHASE_RETURNED
                    ))).stream().collect(Collectors.toMap(Stock::getSn, Function.identity()));

            errorList.addAll(collectByStock.keySet());
        });

        List<PurchaseStockQueryImportRequest.ImportDto> importDtoList = dataList.stream()
                .filter(importDto -> !errorList.contains(importDto.getStockSn()))
                .collect(Collectors.toList());

        List<DictData> dictDataList = dictDataService.list(Wrappers.<DictData>lambdaQuery().likeRight(DictData::getDictType, "stock_"));

        Map<String, List<DictData>> collect = dictDataList.stream().collect(Collectors.groupingBy(DictData::getDictType));

        Map<String, Map<String, List<GoodsBaseInfo>>> goodsMap = goodsWatchService.listGoodsBaseInfo(importDtoList.stream()
                        .map(PurchaseStockQueryImportRequest.ImportDto::getBrandName)
                        .collect(Collectors.toList()), importDtoList.stream()
                        .map(PurchaseStockQueryImportRequest.ImportDto::getModel)
                        .map(StringTools::purification)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(GoodsBaseInfo::getBrandName, Collectors.groupingBy(GoodsBaseInfo::getModel)));

        List<PurchaseStockQueryImportResult> purchaseStockQueryImportResultList = importDtoList.stream()
                .map(importDto -> {
                    GoodsBaseInfo goodsBaseInfo = Optional.ofNullable(goodsMap.get(importDto.getBrandName()))
                            .map(t -> Optional.ofNullable(t.get(importDto.getModel())) // 优先匹配型号相同
                                    .orElse(t.entrySet()
                                            .stream()
                                            .filter(et -> StringTools.purification(et.getKey()).equals(StringTools.purification(importDto.getModel()))) //过滤型号简码相同的
                                            .map(Map.Entry::getValue)
                                            .findFirst()
                                            .orElse(null)))
                            .map(t -> t.stream().findFirst().orElse(null))
                            .orElse(null);

                    if (Objects.isNull(goodsBaseInfo)) {
                        errorList.add(importDto.getStockSn());
                        return null;
                    }
                    //处理附件信息 分组信息

                    /**
                     * 根据属性名获取
                     */
                    List<Integer> dictListAll = new ArrayList<>();

                    Map<String, List<Integer>> attachmentMap = new HashMap<>();

                    for (Map.Entry<String, List<DictData>> entry : collect.entrySet()) {

                        if (entry.getKey().equals("stock_basis")) {
                            List<DictData> convert = convert(entry.getValue(), Arrays.asList(
                                    Integer.parseInt(importDto.getBox()),
                                    Integer.parseInt(importDto.getBook()),
                                    Integer.parseInt(importDto.getWarranty()),
                                    Integer.parseInt(importDto.getInvoice())));
                            attachmentMap.put(entry.getKey(), convert.stream().map(dictData -> Integer.parseInt(dictData.getDictValue())).collect(Collectors.toList()));
                            dictListAll.addAll(convert.stream().map(dictData -> dictData.getDictCode().intValue()).collect(Collectors.toList()));
                        }

                        if (entry.getKey().equals("stock_three")) {
                            List<DictData> convert = convert(entry.getValue(), Arrays.asList(
                                    Integer.parseInt(importDto.getZCheck()),
                                    Integer.parseInt(importDto.getGCheck()),
                                    Integer.parseInt(importDto.getXCheck()),
                                    Integer.parseInt(importDto.getOCheck())));
                            attachmentMap.put(entry.getKey(), convert.stream().map(dictData -> Integer.parseInt(dictData.getDictValue())).collect(Collectors.toList()));
                            dictListAll.addAll(convert.stream().map(dictData -> dictData.getDictCode().intValue()).collect(Collectors.toList()));
                        }

                        if (entry.getKey().equals("stock_other")) {

                            List<DictData> convert = convert(entry.getValue(), Arrays.asList(
                                    Integer.parseInt(importDto.getDrillCard()),
                                    Integer.parseInt(importDto.getShoulderStrap()),
                                    Integer.parseInt(importDto.getDustCoverBag()),
                                    Integer.parseInt(importDto.getPurchaseVoucher()),
                                    Integer.parseInt(importDto.getJewelCertificate()),
                                    Integer.parseInt(importDto.getHolomembrane()),
                                    Integer.parseInt(importDto.getNotacoria())

                            ));
                            attachmentMap.put(entry.getKey(), convert.stream().map(dictData -> Integer.parseInt(dictData.getDictValue())).collect(Collectors.toList()));
                            dictListAll.addAll(convert.stream().map(dictData -> dictData.getDictCode().intValue()).collect(Collectors.toList()));
                        }

                        if (entry.getKey().equals("stock_type")) {
                            List<DictData> convert = convert(entry.getValue(), Arrays.asList(
                                    Integer.parseInt(importDto.getAttachment()),
                                    Integer.parseInt(importDto.getSingleStock()))
                            );
                            attachmentMap.put(entry.getKey(), convert.stream().map(dictData -> Integer.parseInt(dictData.getDictValue())).collect(Collectors.toList()));
                            dictListAll.addAll(convert.stream().map(dictData -> dictData.getDictCode().intValue()).collect(Collectors.toList()));
                        }
                    }

                    //附件字符串
                    String attachment = convert(dictDataList, dictListAll, Integer.parseInt(importDto.getCard()), ObjectUtils.isEmpty(importDto.getWarrantyDate()) ? StringUtils.EMPTY : DateUtils.parseDateToStr(DateUtils.YYYY_MM, importDto.getWarrantyDate()),
                            goodsBaseInfo.getGoodsId());

                    //公价 图片 型号id
                    return PurchaseStockQueryImportResult.builder()
                            .brandName(importDto.getBrandName())
                            .seriesName(importDto.getSeriesName())
                            .model(importDto.getModel())
                            .goodsId(goodsBaseInfo.getGoodsId())
                            .pricePub(goodsBaseInfo.getPricePub())
                            .finess(importDto.getFiness())
                            .stockSn(importDto.getStockSn())
                            .purchasePrice(importDto.getPurchasePrice())
                            .attachment(attachment)
                            .attachmentMap(attachmentMap)
                            .seriesType(goodsBaseInfo.getSeriesType())
                            .isCard(Integer.parseInt(importDto.getCard()))
                            .warrantyDate(ObjectUtils.isEmpty(importDto.getWarrantyDate()) ? StringUtils.EMPTY : DateUtils.parseDateToStr(DateUtils.YYYY_MM, importDto.getWarrantyDate()))
                            .remarks(importDto.getRemarks())
                            .strapMaterial(importDto.getStrapMaterial())
                            .wuyuPrice(importDto.getWuyuPrice())
                            .build();


                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ImportResult.<PurchaseStockQueryImportResult>builder()
                .successList(purchaseStockQueryImportResultList)
                .errList(Lists.newArrayList(errorList))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSettle(PurchaseBatchSettleRequest request) {

        Assert.notNull(request, "request参数不能为空");

        Assert.isTrue(Objects.nonNull(request.getCustomerId()), "customerId参数不能为空");

        Assert.notNull(request.getSubjectId(), "subjectId参数不能为空");

        Assert.notNull(request.getBank(), "bank参数不能为空");

        Assert.notNull(request.getAccountName(), "accountName参数不能为空");

        Assert.notNull(request.getBankAccount(), "BankAccount参数不能为空");

        Assert.notNull(request.getBankCustomerName(), "BankCustomerName参数不能为空");

        Assert.isTrue(CollectionUtils.isNotEmpty(request.getDetails()), "request.getDetails参数不能为空");

        Customer customer = customerService.getById(request.getCustomerId());

        Assert.notNull(customer, "客户不存在");

        Map<String, List<PurchaseBatchSettleRequest.BillPurchaseLineDto>> map = request.getDetails().stream().collect(Collectors.groupingBy(PurchaseBatchSettleRequest.BillPurchaseLineDto::getOriginSerialNo));

        //客户是否相同 采购行不校验
        if (!billPurchaseService.list(Wrappers.<BillPurchase>lambdaQuery()
                .in(BillPurchase::getSerialNo, map.keySet())).stream().allMatch(i -> i.getCustomerId().equals(request.getCustomerId()))) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

//        if (!stockService.selectByStockIdList(request.getDetails().stream()
//                .map(PurchaseBatchSettleRequest.BillPurchaseLineDto::getStockId).collect(Collectors.toList())).stream().allMatch(i -> Arrays.asList(StockStatusEnum.MARKETABLE.getValue(), StockStatusEnum.WAIT_PRICING.getValue()).contains(i.getStockStatus()))) {
//            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
//        }

        PurchaseSubject subject = purchaseSubjectService.getById(request.getSubjectId());
        Assert.notNull(subject, "打款主体不存在");

        ApplyFinancialPaymentCreateRequest createRequest = ApplyFinancialPaymentCreateRequest.builder()
                .typePayment(ApplyFinancialPaymentTypeEnum.PEER_CONSIGNMENT.getValue())
                .pricePayment(request.getDetails().stream().map(PurchaseBatchSettleRequest.BillPurchaseLineDto::getSettlePrice).reduce(BigDecimal.ZERO, BigDecimal::add))
                .subjectPayment(subject.getId())
                .remarks(request.getRemarks())
                .bankAccount(request.getBank())
                .bankCard(request.getBankAccount())
                .bankName(request.getAccountName())
                .bankCustomerName(request.getBankCustomerName())
                .customerName(customer.getCustomerName())
                .shopId(UserContext.getUser().getStore().getId())
                .whetherUse(WhetherEnum.YES.getValue())
                .salesMethod(FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT.getValue())
                .payment(ApplyFinancialPaymentEnum.REJECTED.getValue())
                .originSerialNo(request.getDetails().stream().map(PurchaseBatchSettleRequest.BillPurchaseLineDto::getOriginSerialNo).collect(Collectors.joining(",")))
                .build();
        // 回填打款单号 创建申请打款单
        ApplyFinancialPaymentCreateResult result = applyFinancialPaymentService.create(createRequest);

        //创建表和申请打款单关系
        billPurchaseLineService.updateBatchById(billPurchaseLineService.listByStockIds(request.getDetails().stream().map(PurchaseBatchSettleRequest.BillPurchaseLineDto::getStockId).collect(Collectors.toList())).stream().map(a -> {
            BillPurchaseLine line = new BillPurchaseLine();
            line.setId(a.getLineId());
            line.setWhetherSettle(WhetherEnum.YES);
            return line;
        }).collect(Collectors.toList()));

        //申请打款单 和 商品关联关系
        List<AccountStockRelation> relationList = request.getDetails().stream().map(a -> AccountStockRelation.builder().afpId(result.getId()).stockId(a.getStockId()).originPrice(a.getSettlePrice()).originSerialNo(a.getOriginSerialNo()).build()).collect(Collectors.toList());
        stockRelationService.saveBatch(relationList);
        //修改应付单
        paymentDTemplate.updatePayable(new JSONObject().fluentPut("afp", applyFinancialPaymentService.getById(result.getId())));

//        List<AccountsPayableAccounting> accountingList = accountingService.selectListByStockSnAndType(request.getDetails().stream().map(PurchaseBatchSettleRequest.BillPurchaseLineDto::getStockId).collect(Collectors.toList()), ReceiptPaymentTypeEnum.AMOUNT_PAYABLE).stream().map(t -> {
//            AccountsPayableAccounting accounting = new AccountsPayableAccounting();
//            accounting.setId(t.getId());
//            accounting.setAfpSerialNo(result.getSerialNo());
//            accounting.setAfpId(result.getId());
//            return accounting;
//        }).collect(Collectors.toList());
//        //修改应付状态
//        accountingService.updateBatchById(accountingList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void marginCover(PurchaseMarginCoverRequest request) {
        Assert.notNull(request, "request参数不能为空");
        Assert.isTrue(Objects.nonNull(request.getLineId()), "lineId参数不能为空");
        Assert.isTrue(Objects.nonNull(request.getFinalPurchase()), "finalPurchase参数不能为空");

        //业务处理： 录入后商品的成本需要自动变更
        BillPurchaseLine purchaseLine = billPurchaseLineService.getById(request.getLineId());
        Assert.isNull(purchaseLine.getOldPurchasePrice(), " 补差额只能进行一次");
        Assert.isTrue(request.getFinalPurchase().compareTo(purchaseLine.getPurchasePrice()) <= 0, "补差价格不能大于原采购价");
        BillPurchase purchase = billPurchaseService.getById(purchaseLine.getPurchaseId());

        if (purchase.getPurchaseState() != BusinessBillStateEnum.COMPLETE) {
            throw new OperationRejectedException(OperationExceptionCode.ILLEGAL_PARAMETER);
        }

        Stock s = stockService.getById(purchaseLine.getStockId());

        if (Arrays.asList(StockStatusEnum.PURCHASE_RETURNED_ING, StockStatusEnum.PURCHASE_RETURNED).contains(s.getStockStatus())) {
            throw new OperationRejectedException(OperationExceptionCode.PURCHASE_RETURN_ING);
        }

        BillPurchaseLine line = new BillPurchaseLine();
        line.setId(purchaseLine.getId());
        line.setOldPurchasePrice(purchaseLine.getPurchasePrice());
        line.setPurchasePrice(request.getFinalPurchase());
        billPurchaseLineService.updateById(line);

        Stock stock = new Stock();
        stock.setId(purchaseLine.getStockId());
        stock.setPurchasePrice(request.getFinalPurchase());
        stockService.updateById(stock);

        stockService.recalculateConsignmentPrice(Lists.newArrayList(line.getStockId()));

        pricingService.updateByStockId(purchaseLine.getStockId(), request.getFinalPurchase());

        //财务处理：
        // 1、生成财物单据：单据查询（新） 订单类型【采购折让（新增）】；订单分类【同行采购】；
        FinancialGenerateDto dto = new FinancialGenerateDto();
        dto.setId(purchaseLine.getPurchaseId());
        dto.setStockList(Lists.newArrayList(purchaseLine.getStockId()));
        dto.setType(purchase.getPurchaseSource().getValue());
        financialDocumentsService.generatePurchaseMarginCover(dto);
        //2、预付，列表枚举
        List<Integer> apa = accountingService.createApa(purchase.getSerialNo(),
                ReceiptPaymentTypeEnum.PRE_PAID_AMOUNT, FinancialStatusEnum.PENDING_REVIEW,
                Lists.newArrayList(purchaseLine.getStockId()),
                line.getPurchasePrice().subtract(line.getOldPurchasePrice()), false);
        //3、确认收款，列表枚举
        log.info("accountReceiptConfirmAdd function of SaleListenerForLogisticsDelivery start and purchase={},line={}",
                JSON.toJSONString(purchase), JSON.toJSONString(purchaseLine));
        List<CustomerContacts> customerContactsList = customerContactsService.searchByCustomerId(purchase.getCustomerId());
        customerContactsList = customerContactsList.stream().filter(Objects::nonNull)
                .filter(e -> e.getId().equals(purchase.getCustomerContactId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(customerContactsList)) {
            log.info("customerContactsList or lines of accountReceiptConfirmAdd function is empty .");
            return;
        }

        CustomerPO customerPO = customerService.queryCustomerPO(purchase.getCustomerId());

        CustomerContacts customerContacts = customerContactsList.get(0);
        AccountReceiptConfirmAddRequest addRequest = new AccountReceiptConfirmAddRequest();
        addRequest.setCustomerId(purchase.getCustomerId());
        addRequest.setCustomerName(null != customerPO && StringUtils.isNotEmpty(customerPO.getCustomerName()) ? customerPO.getCustomerName() : FlywheelConstant.CUSTOMER_CONTACTNAME_VALUE);
        addRequest.setContactId(purchase.getCustomerContactId());
        addRequest.setContactName(customerContacts.getName());
        addRequest.setContactAddress(customerContacts.getAddress());
        addRequest.setContactPhone(customerContacts.getPhone());
        addRequest.setShopId(UserContext.getUser().getStore().getId());
        addRequest.setMiniAppSource(Boolean.FALSE);

        addRequest.setOriginSerialNo(purchase.getSerialNo());
        addRequest.setReceivableAmount(line.getOldPurchasePrice().subtract(line.getPurchasePrice()));
        addRequest.setWaitAuditPrice(addRequest.getReceivableAmount());
        addRequest.setOriginType(OriginTypeEnum.CG.getValue());
        addRequest.setClassification(FinancialClassificationEnum.TH_CG.getValue());
        addRequest.setCollectionType(CollectionTypeEnum.CG_TK.getValue());

        addRequest.setSalesMethod(FinancialSalesMethodEnum.PURCHASE_MARGIN_COVER.getValue());
        addRequest.setStatus(AccountReceiptConfirmStatusEnum.WAIT.getValue());
        addRequest.setCollectionNature(CollectionNatureEnum.PURCHASE_RETURN.getValue());
        addRequest.setTotalNumber(1);
        AccountReceiptConfirm accountReceiptConfirm = accountReceiptConfirmService.accountReceiptConfirmAdd(addRequest);

        //回填补差表的关联关系
        AccountStockRelation accountStockRelation = new AccountStockRelation();
        accountStockRelation.setArcId(accountReceiptConfirm.getId());
        accountStockRelation.setOriginPrice(line.getOldPurchasePrice().subtract(line.getPurchasePrice()));
        accountStockRelation.setStockId(purchaseLine.getStockId());
        accountStockRelation.setOriginSerialNo(purchase.getSerialNo());
        accountStockRelationMapper.insert(accountStockRelation);

        if (Objects.nonNull(apa) && apa.stream().allMatch(Objects::nonNull)) {
            apa.forEach(i -> {
                AccountsPayableAccounting accounting = new AccountsPayableAccounting();
                accounting.setId(i);
                accounting.setArcSerialNo(accountReceiptConfirm.getSerialNo());
                accountingService.updateById(accounting);
            });
        }
    }

    /**
     * 字段拼接
     *
     * @param dataList
     * @param itemList
     * @param isCard
     * @param warrantyDate
     * @return
     */
    private String convert(List<DictData> dataList, List<Integer> itemList, Integer isCard, String warrantyDate, Integer goodsId) {
        String card = "空白保卡";
        String cardDate = "保卡(date)";
        if (Objects.nonNull(goodsId)) {
            Integer seriesType = seriesService.getSeriesTypeByGoodsId(goodsId);
            if (Objects.nonNull(seriesType) && SeriesTypeEnum.BAGS.getValue().equals(seriesType)) {
                card = "空白身份卡";
                cardDate = "身份卡(date)";
            }
        }

        String join = ObjectUtils.isEmpty(isCard) ? StringUtils.EMPTY : (isCard.equals(1) ? StringUtils.replace(cardDate, "date", warrantyDate) : isCard.equals(0) ? StringUtils.EMPTY : card);

        String attachment = StringUtils.EMPTY;

        if (CollectionUtils.isNotEmpty(dataList)) {

            List<String> collect = null;
            if (CollectionUtils.isNotEmpty(itemList)) {
                collect = itemList.stream().flatMap(item -> dataList.stream().filter(dictData -> item.intValue() == (dictData.getDictCode().intValue()))).map(dictData -> dictData.getDictLabel()).collect(Collectors.toList());
            }
            attachment = CollectionUtils.isEmpty(itemList) ? StringUtils.EMPTY + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : join) : StringUtils.join(collect, "/") + (ObjectUtils.isEmpty(join) ? StringUtils.EMPTY : "/" + join);

        } else {
            return join;
        }

        return attachment;
    }

    /**
     * @param dataList 排序正序
     * @param itemList 正序
     * @return
     */
    private List<DictData> convert(List<DictData> dataList, List<Integer> itemList) {

        List<DictData> list = new ArrayList<>();

        dataList.sort(Comparator.comparing(DictData::getDictCode));

        for (int i = 0; i < dataList.size(); i++) {
            if (itemList.get(i).equals(1)) {
                list.add(dataList.get(i));
            }
        }
        return list;
    }

}
