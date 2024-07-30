package com.seeease.flywheel.serve.maindata.rpc;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.StoreQuotaImportRequest;
import com.seeease.flywheel.maindata.IStoreFacade;
import com.seeease.flywheel.maindata.request.StoreQuotaAddRequest;
import com.seeease.flywheel.maindata.request.StoreQuotaQueryRequest;
import com.seeease.flywheel.maindata.result.StoreQuotaQueryResult;
import com.seeease.flywheel.maindata.result.TransferUsableQuotaQueryResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.financial.mapper.ApplyFinancialPaymentMapper;
import com.seeease.flywheel.serve.goods.entity.Brand;
import com.seeease.flywheel.serve.goods.service.BrandService;
import com.seeease.flywheel.serve.maindata.convert.StoreQuotaConverter;
import com.seeease.flywheel.serve.maindata.entity.*;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.maindata.service.StoreQuotaService;
import com.seeease.flywheel.serve.maindata.service.StoreRelationshipSubjectService;
import com.seeease.flywheel.serve.maindata.service.TagService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
public class StoreFacade implements IStoreFacade {
    @Resource
    private StoreQuotaService storeQuotaService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private TagService tagService;
    @Resource
    private BrandService brandService;
    @Resource
    private ApplyFinancialPaymentMapper applyFinancialPaymentMapper;
    @Resource
    private StoreRelationshipSubjectService storeRelationshipSubjectService;


    @Override
    public TransferUsableQuotaQueryResult query(Integer shopId) {

        Integer sjId = storeRelationshipSubjectService.getByShopId(shopId).getSubjectId();

        TransferUsableQuotaQueryResult ret = new TransferUsableQuotaQueryResult();
        BigDecimal usedCtQuota = applyFinancialPaymentMapper.usedCtQuota(sjId, null);
        ret.setUsedCtQuota(usedCtQuota);


        LambdaQueryWrapper<StoreQuota> wq = Wrappers.<StoreQuota>lambdaQuery()
                .eq(StoreQuota::getSmId, shopId);

        String nowDateStr = DateUtil.format(new Date(), DateUtils.YMD);
        Date now = DateUtils.parseDate(DateUtils.YMD,nowDateStr);

        storeQuotaService.list(wq).forEach(v->{
            if (now.compareTo(v.getStartDate()) > -1 && now.compareTo(v.getEndDate()) < 1){
                BigDecimal usedOsQuota = applyFinancialPaymentMapper.usedOsQuota(shopId,sjId, null);
                BigDecimal temp = BigDecimal.ZERO;
                ArrayList<TransferUsableQuotaQueryResult.Item> list = new ArrayList<>();
                for (StoreQuota.Line l : v.getOsLines()){
                    temp = temp.add(l.getQuota());
                    BigDecimal usedOsQuota1 = Optional.ofNullable(
                            applyFinancialPaymentMapper.usedOsQuota(shopId,sjId, Collections.singletonList(l.getBrandId()))
                    ).orElse(BigDecimal.ZERO);

                    Brand brand = brandService.getById(l.getBrandId());
                    TransferUsableQuotaQueryResult.Item item = new TransferUsableQuotaQueryResult.Item();
                    item.setBrandName(brand.getName());
                    item.setBrandId(brand.getId());
                    item.setOsQuota(l.getQuota().subtract(usedOsQuota1));
                    list.add(item);
                }
                ret.setList(list);
                ret.setOsQuota(temp.subtract(usedOsQuota));
                ret.setIsCtl(v.getIsCtl());
            }
        });
        return ret;
    }




    @Override
    public Integer quotaSubmit(StoreQuotaAddRequest request) {
        if (request.getId() == null){
            Date start = DateUtils.parseDate(DateUtils.YMD,request.getStartDate());
            Date end = DateUtils.parseDate(DateUtils.YMD,request.getEndDate());


            LambdaQueryWrapper<StoreQuota> eq = Wrappers.<StoreQuota>lambdaQuery()
                    .eq(StoreQuota::getSmId, request.getShopId());
            storeQuotaService.list(eq).forEach(v -> {
                if (!(start.after(v.getEndDate()) || end.before(v.getStartDate()))) {
                    throw new OperationRejectedException(OperationExceptionCode.STEP5);
                }
            });
        }else {
            request.setStartDate(null);
            request.setEndDate(null);
        }



        List<StoreQuota.Line> ctQuotas = request.getCtQuotas().stream().map(StoreQuotaConverter.INSTANCE::to).collect(Collectors.toList());
        List<StoreQuota.Line> osQuotas = request.getOsQuotas().stream().map(StoreQuotaConverter.INSTANCE::to).collect(Collectors.toList());
        StoreQuota storeQuota = StoreQuotaConverter.INSTANCE.to(request,
                ctQuotas,
                osQuotas
        );

        storeQuotaService.saveOrUpdate(storeQuota);
        return storeQuota.getId();
    }

    @Override
    public PageResult<StoreQuotaQueryResult> quotaPage(StoreQuotaQueryRequest request) {
        LambdaQueryWrapper<StoreQuota> qw = Wrappers.<StoreQuota>lambdaQuery()
                .eq(Objects.nonNull(request.getShopId()), StoreQuota::getSmId, request.getShopId())
                .eq(null != request.getStartDate(), StoreQuota::getStartDate, request.getStartDate())
                .eq(null != request.getEndDate(), StoreQuota::getEndDate, request.getEndDate())
                .orderByDesc(StoreQuota::getCreatedTime);


        Page<StoreQuota> page = Page.of(request.getPage(), request.getLimit());
        storeQuotaService.page(page, qw);
        if (page.getRecords().isEmpty()) {
            return PageResult.<StoreQuotaQueryResult>builder()
                    .result(Collections.emptyList())
                    .totalCount(page.getTotal())
                    .totalPage(page.getPages())
                    .build();
        }

        List<Integer> shopIds = page.getRecords().stream().map(StoreQuota::getSmId).distinct().collect(Collectors.toList());
        Map<Integer, String> idMap = storeManagementService.selectInfoByIds(shopIds).stream().collect(
                Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName)
        );

        List<StoreQuotaQueryResult> data = page.getRecords()
                .stream()
                .map(v -> {
                    String shopName = idMap.get(v.getSmId());
                    BigDecimal osQuota = BigDecimal.ZERO;
                    List<StoreQuotaQueryResult.Line> osQuotas = new ArrayList<>();
                    List<StoreQuotaQueryResult.Line> ctQuotas = new ArrayList<>();

                    if (null != v.getOsLines()) {
                        for (StoreQuota.Line line : v.getOsLines()) {
                            osQuota = osQuota.add(line.getQuota());
                            Brand brand = brandService.getById(line.getBrandId());
                            StoreQuotaQueryResult.Line l = StoreQuotaConverter.INSTANCE.to(line, brand.getName());
                            osQuotas.add(l);
                        }
                    }
                    BigDecimal ctQuota = BigDecimal.ZERO;
                    if (null != v.getCtLines()) {
                        for (StoreQuota.Line line : v.getCtLines()) {
                            ctQuota = ctQuota.add(line.getQuota());
                            Brand brand = brandService.getById(line.getBrandId());
                            StoreQuotaQueryResult.Line l = StoreQuotaConverter.INSTANCE.to(line, brand.getName());
                            ctQuotas.add(l);
                        }
                    }
                    Integer sjId = storeRelationshipSubjectService.getByShopId(v.getSmId()).getSubjectId();
                    BigDecimal usedOsQuota = applyFinancialPaymentMapper.usedOsQuota(
                            v.getSmId(),
                            sjId,
                            null
                    );

                    BigDecimal usedCtQuota = applyFinancialPaymentMapper.usedCtQuota(
                            sjId,
                            null
                    );


                    return StoreQuotaConverter.INSTANCE.to(
                            v,
                            shopName,
                            osQuota,
                            ctQuota,
                            osQuotas,
                            ctQuotas,
                            usedOsQuota,
                            usedCtQuota);
                })
                .collect(Collectors.toList());

        return PageResult.<StoreQuotaQueryResult>builder()
                .result(data)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    @Transactional
    public void importDate(StoreQuotaImportRequest request) {
        List<String> tagNames = request.getDataList().stream().map(StoreQuotaImportRequest.ImportDto::getTagName).distinct().collect(Collectors.toList());
        Map<String, Long> tagMap = tagService.list(Wrappers.<Tag>lambdaQuery().in(Tag::getTagName, tagNames))
                .stream()
                .collect(Collectors.toMap(Tag::getTagName, Tag::getId));
        if (tagMap.isEmpty()) {
            return;
        }

        Map<Integer, Integer> smMap = storeManagementService.list(Wrappers.<StoreManagement>lambdaQuery().in(StoreManagement::getTagId, tagMap.values()).eq(StoreManagement::getDelFlag, WhetherEnum.YES))
                .stream()
                .collect(Collectors.toMap(StoreManagement::getTagId, StoreManagement::getId));


        List<StoreQuota> collect = request.getDataList().stream().map(v -> {
            Date start, end;
            try {
                start = DateUtils.getTimesmorning(DateUtil.parse(v.getStart()));
                end = DateUtils.getTimesnight(DateUtil.parse(v.getEnd()));
            } catch (Exception ignore) {
                return null;
            }

            Integer smId = smMap.get(tagMap.getOrDefault(v.getTagName(), 0L).intValue());
            if (Objects.isNull(smId)) {
                return null;
            }

            StoreQuota storeQuota = new StoreQuota();
//            storeQuota.setQuota(v.getQuota());
//            storeQuota.setStart(start);
//            storeQuota.setEnd(end);
//            storeQuota.setSmId(smId);
            return storeQuota;

        }).filter(Objects::nonNull).collect(Collectors.toList());
        storeQuotaService.saveBatch(collect);
    }

    @Override
    public Integer quotaDel(Integer id) {
        storeQuotaService.removeById(id);
        return id;
    }


}
