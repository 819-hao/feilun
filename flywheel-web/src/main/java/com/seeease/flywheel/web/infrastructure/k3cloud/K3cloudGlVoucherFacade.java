package com.seeease.flywheel.web.infrastructure.k3cloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.account.IAccountFacade;
import com.seeease.flywheel.account.ICostJdFlMappingFacade;
import com.seeease.flywheel.account.IShopCompanyMappingFacade;
import com.seeease.flywheel.account.request.AccountDeleteByFinanceRequest;
import com.seeease.flywheel.account.request.AccountImportByFinanceQueryRequest;
import com.seeease.flywheel.account.result.AccountQueryImportResult;
import com.seeease.flywheel.account.result.CostJdFlMappingResult;
import com.seeease.flywheel.account.result.ShopCompanyMappingResult;
import com.seeease.flywheel.k3cloud.IK3cloudGlVoucherFacade;
import com.seeease.flywheel.k3cloud.request.K3cloudGlVoucherRequest;
import com.seeease.flywheel.k3cloud.result.K3cloudGlVoucherResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description FEntity_FEntryId,
 * @Date create in 2023/8/2 17:53
 */
@Slf4j
//@DubboService(version = "1.0.0")
@Service
public class K3cloudGlVoucherFacade implements IK3cloudGlVoucherFacade {

    @DubboReference(check = false, version = "1.0.0")
    private IAccountFacade accountFacade;

    @DubboReference(check = false, version = "1.0.0")
    private IShopCompanyMappingFacade shopCompanyMappingFacade;

    @DubboReference(check = false, version = "1.0.0")
    private ICostJdFlMappingFacade costJdFlMappingFacade;


    /**
     * {
     * "FormId": "GL_VOUCHER",
     * "FieldKeys": "FACCOUNTID,FEXPLANATION",
     * "FilterString": [],
     * "OrderString": "",
     * "TopRowCount": 0,
     * "StartRow": 0,
     * "Limit": 0,
     * "SubSystemId": ""
     * }
     */
    /**
     * FAccountBookID.FName
     * [[312101,"14284","6602","管理费用","朱瑶(瘦瘦) 报销住宿补贴","2023-07-01T00:00:00",2000.0,0.0,"022","嘉兴稀蜴臻品科技有限公司"]]
     * <p>
     * ["沈阳稀蜴臻品寄卖服务有限公司","公用""2022-07-01T00:00:00",111.8600000000,"销售费用","平台扣点","POS结算手续费"]
     */


    private static final String FIELD_KEYS = "FAccountBookID.FName,FDetailID.FFLEX5.FName,FDate,FDEBIT,FACCOUNTID.FName,FDetailID.FFLEX9.FName,FEXPLANATION";

    private static final String FORM_ID = "GL_VOUCHER";

    private static final Map<String, String> FIELD_MAP = new HashMap<String, String>() {{
        put("销售费用", "6601");
        put("管理费用", "6602");
        put("手续费", "6603.04");
        put("利息支出", "6603.01");
    }};

    @Override
    public K3cloudGlVoucherResult executeBillQuery(K3cloudGlVoucherRequest request) {

        K3CloudGiVoucherRequest k3CloudGiVoucherRequest = covertRequest(request);

        List<List> execute = execute(k3CloudGiVoucherRequest);

        if (CollectionUtils.isEmpty(execute) || execute.stream().allMatch(Objects::isNull)) {
            return K3cloudGlVoucherResult.builder().list(Arrays.asList()).build();
        }
        AccountImportByFinanceQueryRequest covert = this.covert(execute, request.isTask());
        //伪删除
        accountFacade.delete(AccountDeleteByFinanceRequest.builder()
                .accountGroupList(CollectionUtils.isEmpty(request.getCodeList()) ? new ArrayList<>(FIELD_MAP.keySet()) : FIELD_MAP.entrySet().stream().filter(r -> request.getCodeList().contains(r.getKey())).map(r -> r.getKey()).collect(Collectors.toList()))
                .completeDateStart(request.getCompleteDateStart())
                .completeDateEnd(request.getCompleteDateEnd())
                .build());
        ImportResult<AccountQueryImportResult> result = accountFacade.queryImport(covert);
        return K3cloudGlVoucherResult.builder().list(result.getSuccessList().stream().map(r -> r.getId()).collect(Collectors.toList())).build();
    }

    /**
     * 转换金蝶请求体
     *
     * @param request
     * @return
     */
    private K3CloudGiVoucherRequest covertRequest(K3cloudGlVoucherRequest request) {
        /**
         * 当前时间
         */
        Date now = new Date();

        String accountString = StringUtils.join(FIELD_MAP.values(), ",");

        String dateStart = DateFormatUtils.format(DateUtils.truncate(now, 2), "yyyy-MM-dd");

        String dateEnd = DateFormatUtils.format(DateUtils.addDays(DateUtils.ceiling(now, 5), -1), "yyyy-MM-dd");

        K3CloudGiVoucherRequest k3CloudGiVoucherRequest = K3CloudGiVoucherRequest.builder().formId(FORM_ID).fieldKeys(FIELD_KEYS)
                //                .limit(5)//                .topRowCount(100)//                .startRow(100)
                .build();

        /**
         * 过滤条件
         */
        List<K3CloudGiVoucherRequest.FilterStringDTO> dtoArrayList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(request.getCodeList())) {
            accountString = StringUtils.join(FIELD_MAP.entrySet().stream().filter(r -> request.getCodeList().contains(r.getKey())).map(r -> r.getValue()).collect(Collectors.toList()), ",");
        }
        dtoArrayList.add(K3CloudGiVoucherRequest.FilterStringDTO.builder().left("(").fieldName("FACCOUNTID.FNumber").compare("in").value(accountString).right(")").logic("and").build());
        if (StringUtils.isNotEmpty(request.getCompleteDateStart()) && StringUtils.isNotEmpty(request.getCompleteDateEnd())) {
            dateStart = request.getCompleteDateStart().substring(0, 10);
            dateEnd = request.getCompleteDateEnd().substring(0, 10);
        }
        //目标日期范围
        dtoArrayList.add(K3CloudGiVoucherRequest.FilterStringDTO.builder().left("(").fieldName("FDate").compare(">=").value(dateStart).right(")").logic("and").build());
        dtoArrayList.add(K3CloudGiVoucherRequest.FilterStringDTO.builder().left("(").fieldName("FDate").compare("<=").value(dateEnd).right(")").logic("").build());

        k3CloudGiVoucherRequest.setFilterString(dtoArrayList);
        /**
         * 过滤条件
         */

        request.setCompleteDateStart(dateStart);
        request.setCompleteDateEnd(dateEnd);
        return k3CloudGiVoucherRequest;
    }

    /**
     * 执行金蝶请求结果
     *
     * @param k3CloudGiVoucherRequest
     * @return
     */
    private List<List> execute(K3CloudGiVoucherRequest k3CloudGiVoucherRequest) {

        try {

            String data = K3cloudClientUtil.getInstance().execute("Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.ExecuteBillQuery", Arrays.asList(k3CloudGiVoucherRequest).toArray(), String.class);

            if (StringUtils.isBlank(data)) {
                return Arrays.asList();
            }

            return JSONObject.parseArray(data, List.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Arrays.asList();
        }

    }

    /**
     * 飞轮请求体
     *
     * @param request
     * @return
     */
    private AccountImportByFinanceQueryRequest covert(List<List> request, boolean isTask) {

        AccountImportByFinanceQueryRequest build = AccountImportByFinanceQueryRequest.builder().build();
        if (CollectionUtils.isEmpty(request)) {
            return build;
        }

        List<ShopCompanyMappingResult> shopCompanyMappingResultList = shopCompanyMappingFacade.list();
        List<CostJdFlMappingResult> costJdFlMappingResultList = costJdFlMappingFacade.list();

        List<AccountImportByFinanceQueryRequest.ImportDto> importDtoList = new ArrayList<>();

        for (List list : request) {
            try {

                String company = (String) list.get(0);
                String department = (String) list.get(1);
                String accountType = (String) list.get(5);
                String accountGroup = (String) list.get(4);
                String jdType = (String) list.get(6);

//                String shopName = shopCompanyMappingResultList.stream().filter(result -> result.getCompanyName().equals(company) && result.getDepartment().equals(department)).map(result -> result.getShopName()).findAny().orElse(null);
                String shopName = shopCompanyMappingResultList.stream().filter(result -> result.getDepartment().equals(department)).map(result -> result.getShopName()).findAny().orElse(null);
                CostJdFlMappingResult costJdFlMappingResult = costJdFlMappingResultList.stream().filter(result -> result.getJdType().equals(accountType)).findAny().orElse(null);

//                if(isTask && (accountGroup.equals("人员") || accountGroup.equals("场地费用"))){
                log.info("数据->{}", JSON.toJSONString(list));
                if (isTask && (Arrays.asList("销售佣金", "工资费用", "企业社保", "企业公积", "员工宿舍", "员工福利").contains(jdType) || jdType.equals("场地费用"))) {
                    continue;
                }

                if (accountGroup.equals("手续费")) {
                    if (ObjectUtils.isEmpty(shopName)) {
                        continue;
                    }
                    importDtoList.add(AccountImportByFinanceQueryRequest.ImportDto.builder()
                            //部门
                            .companyName(shopName)
                            //日期
                            .completeDate(DateUtils.parseDate((String) list.get(2), "yyyy-MM-dd'T'HH:mm:ss"))
                            //金额
                            .money((BigDecimal) list.get(3))
                            //分组
                            .accountGroup("管理费用")
                            //分类
                            .accountType("其他费用")
                            //摘要
                            .digest((String) list.get(6))
                            .build());
                    continue;
                }
                if (accountGroup.equals("利息支出")) {
                    if (ObjectUtils.isEmpty(shopName)) {
                        continue;
                    }
                    importDtoList.add(AccountImportByFinanceQueryRequest.ImportDto.builder()
                            //部门
                            .companyName(shopName)
                            //日期
                            .completeDate(DateUtils.parseDate((String) list.get(2), "yyyy-MM-dd'T'HH:mm:ss"))
                            //金额
                            .money((BigDecimal) list.get(3))
                            //分组
                            .accountGroup("管理费用")
                            //分类
                            .accountType("利息支出")
                            //摘要
                            .digest((String) list.get(6))
                            .build());
                    continue;
                }

                if (ObjectUtils.isEmpty(shopName) || ObjectUtils.isEmpty(costJdFlMappingResult)) {
                    continue;
                }

                importDtoList.add(AccountImportByFinanceQueryRequest.ImportDto.builder()
                        //部门
                        .companyName(shopName)
                        //日期
                        .completeDate(DateUtils.parseDate((String) list.get(2), "yyyy-MM-dd'T'HH:mm:ss"))
                        //金额
                        .money((BigDecimal) list.get(3))
                        //分组
                        .accountGroup(ObjectUtils.isEmpty(costJdFlMappingResult) ? "未知映射费用归类" : costJdFlMappingResult.getFlGroup())
                        //分类
                        .accountType(ObjectUtils.isEmpty(costJdFlMappingResult) ? "未知映射费用分类" : costJdFlMappingResult.getFlType())
                        //摘要
                        .digest((String) list.get(6))
                        .build());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        build.setDataList(importDtoList);

        return build;
    }


}
