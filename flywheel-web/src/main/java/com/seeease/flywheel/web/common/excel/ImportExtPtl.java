package com.seeease.flywheel.web.common.excel;

import com.alibaba.cola.extension.ExtensionPointI;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.ImportRequest;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.ReflectUtils;
import com.seeease.springframework.utils.excel.ExcelReader;
import com.seeease.springframework.utils.excel.ExcelTemplateException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/3/30
 */
public interface ImportExtPtl<T extends ImportRequest, R> extends ExtensionPointI {
    /**
     * 获取入参class
     *
     * @return
     */
    Class<T> getRequestClass();

    /**
     * 参数校验
     *
     * @param cmd
     */
    void validate(ImportCmd<T> cmd);

    /**
     * 导入处理
     *
     * @param cmd
     * @return
     */
    ImportResult<R> handle(ImportCmd<T> cmd);

    /**
     * 参数转换
     *
     * @param cmd
     */
    default void convert(ImportCmd<T> cmd, MultipartFile file) {
        if (Objects.isNull(cmd.getRequest())) {
            return;
        }
        T request = JSONObject.parseObject(JSONObject.toJSONString(cmd.getRequest()), getRequestClass());

        try {
            ExcelReader excelReader = new ExcelReader(ReflectUtils.getClassGenricType(getRequestClass()));
            request.setDataList(excelReader.importExcel(file.getInputStream()));
        } catch (ExcelTemplateException e) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.EXCEL_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OperationRejectedException(OperationExceptionCodeEnum.EXCEL_DATA_EXCEPTION);
        }
        cmd.setRequest(request);
    }
}