package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportCmdExe;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 导入操作
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Slf4j
@RestController
@RequestMapping("/import")
public class ImportController {
    @Resource
    private ImportCmdExe importCmdExe;

    /**
     * 导入统一入口
     *
     * @param file
     * @param useCase
     * @return
     * @throws Exception
     */
    @PostMapping("/import/{useCase}")
    public SingleResponse importStock(@RequestParam("file") MultipartFile file
            , @RequestParam Map<String, Object> params
            , @PathVariable String useCase) {
        ImportCmd cmd = new ImportCmd();
        cmd.setBizCode(BizCode.IMPORT);
        cmd.setUseCase(useCase);
        cmd.setRequest(params);
        return SingleResponse.of(importCmdExe.handle(cmd, file));
    }
}
