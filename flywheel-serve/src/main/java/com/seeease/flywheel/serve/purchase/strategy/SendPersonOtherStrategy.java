package com.seeease.flywheel.serve.purchase.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.purchase.request.PurchaseCreateRequest;
import com.seeease.flywheel.serve.base.BigDecimalUtil;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.customer.entity.Customer;
import com.seeease.flywheel.serve.customer.service.CustomerService;
import com.seeease.flywheel.serve.maindata.service.PurchaseSubjectService;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.exception.e.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 个人寄售-其他
 *
 * @author Tiro
 * @date 2023/3/2
 */
@Component
public class SendPersonOtherStrategy extends PurchaseStrategy {

    @Override
    public BusinessBillTypeEnum getType() {
        return BusinessBillTypeEnum.GR_JS;
    }

    @Resource
    private PurchaseSubjectService purchaseSubjectService;
    @Resource
    private CustomerService customerService;

    @Override
    void preRequestProcessing(PurchaseCreateRequest request) {

        request.setPurchaseMode(PurchaseModeEnum.OTHER.getValue());

        //计算寄售加点
        Integer consignmentPoint = Optional.ofNullable(purchaseSubjectService.getById(request.getPurchaseSubjectId()).getConsignmentPoint()).orElse(0);

        request.getDetails().forEach(billPurchaseLineDto -> {
            //增加寄售天数，新增加逻辑，为了不影响原来逻辑，所以加一层判断
            if(StringUtils.isEmpty(request.getConsignmentTime())){
                //采购价
                BigDecimal purchasePrice = BigDecimalUtil.multiplyRoundHalfUp(billPurchaseLineDto.getDealPrice(), BigDecimal.valueOf(FlywheelConstant.COEFFICIENT));

                billPurchaseLineDto.setPurchasePrice(purchasePrice);
                //寄售价
                billPurchaseLineDto.setConsignmentPrice(purchasePrice.add(BigDecimalUtil.multiplyRoundHalfUp(purchasePrice,
                        BigDecimal.valueOf(consignmentPoint).divide(BigDecimal.valueOf(FlywheelConstant.MULTIPLIER_100)))
                ));
            }else{
                //寄售价就是采购价，新增加逻辑
                billPurchaseLineDto.setConsignmentPrice(billPurchaseLineDto.getPurchasePrice());
            }

            billPurchaseLineDto.setIsSettlement(WhetherEnum.NO.getValue());
        });
        request.setApplyPaymentSerialNo(null);
        //判断身份证图是否为空
        if(request.getCustomerId() != null &&
                CollectionUtil.isEmpty(request.getFrontIdentityCard()) &&
                CollectionUtil.isEmpty(request.getReverseIdentityCard())){
            Customer customer = customerService.getById(request.getCustomerId());
            if(Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getIdentityCardImage())){
                List<String> list = Arrays.asList(customer.getIdentityCardImage().split(","));
                for (int i = 0; i < list.size(); i++) {
                    if(i == 0){
                        request.setFrontIdentityCard(Arrays.asList(list.get(i)));
                    }
                    if(i >0 ){
                        request.setReverseIdentityCard(Arrays.asList(list.get(i)));
                    }
                }
            }
        }
    }

    @Override
    void checkRequest(PurchaseCreateRequest request) throws BusinessException {

        Assert.isTrue(!(request.getDealEndTime().isEmpty() || request.getDealEndTime().isEmpty()), "寄售时间不能为空");

        //Assert.isTrue(request.getDetails().stream().allMatch(t -> Objects.nonNull(t.getDealPrice())), "寄售协议价不能为空");

        Assert.isTrue(request.getDetails().size() == FlywheelConstant.ONE, "个人寄售只能单只表");
    }
}
