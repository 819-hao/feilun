package com.seeease.flywheel.web.common.excel;

import com.alibaba.cola.extension.BizScenario;
import com.alibaba.cola.extension.ExtensionExecutor;
import com.seeease.flywheel.ImportResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author Tiro
 * @date 2023/3/30
 */
@Component
public class ImportCmdExe {
    @Resource
    private ExtensionExecutor extensionExecutor;

    /**
     * @param UseCase
     * @param file
     * @return
     */
    public ImportResult handle(ImportCmd cmd, MultipartFile file) {
        BizScenario bizScenario = BizScenario.valueOf(cmd.getBizCode(), cmd.getUseCase());
        // 执行参数转换
        extensionExecutor.executeVoid(ImportExtPtl.class, bizScenario, extension -> extension.convert(cmd, file));
        // 执行参数校验
        extensionExecutor.executeVoid(ImportExtPtl.class, bizScenario, extension -> extension.validate(cmd));
        // 执行参数校验
        return extensionExecutor.execute(ImportExtPtl.class, bizScenario, extension -> extension.handle(cmd));
    }
}
