package com.seeease.flywheel.serve.appexpress.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.appexpress.IAppExpressFacade;
import com.seeease.flywheel.appexpress.request.AppExpressSubmitRequest;
import com.seeease.flywheel.appexpress.result.AppExpressPageResult;
import com.seeease.flywheel.financial.result.AccountReceiptConfirmPageResult;
import com.seeease.flywheel.serve.appexpress.entity.MineExpress;
import com.seeease.flywheel.serve.appexpress.service.MineExpressService;
import com.seeease.springframework.SingleResponse;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author 西门 游
 * @version 1.0
 * @since 5/10/24 6:11 下午
 **/
@DubboService(version = "1.0.0")
public class AppExpressFacade implements IAppExpressFacade {
    @Resource
    private MineExpressService mineExpressService;

    @Override
    public PageResult<AppExpressPageResult> queryPage(String pageNum, String pageSize, String code) {
        LambdaQueryWrapper<MineExpress> qw = Wrappers.<MineExpress>lambdaQuery()
                .eq(StringUtils.isNotEmpty(code), MineExpress::getCode, code)
                .orderByDesc(MineExpress::getCreatedTime);
        Page<MineExpress> page = Page.of(Long.parseLong(pageNum), Long.parseLong(pageSize));
        mineExpressService.page(page,qw);

        List<AppExpressPageResult> list = page.getRecords().stream().map(v -> {
            AppExpressPageResult ret = new AppExpressPageResult();
            BeanUtils.copyProperties(v, ret);
            return ret;
        }).collect(Collectors.toList());

        return PageResult.<AppExpressPageResult>builder()
                .result(list)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public void submit(AppExpressSubmitRequest request) {
        MineExpress m = new MineExpress();
        BeanUtils.copyProperties(request, m);
         mineExpressService.save(m);
    }
}
