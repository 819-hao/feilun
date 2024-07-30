package com.seeease.flywheel.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.common.util.TaoBaoSignUtil;
import com.seeease.flywheel.web.controller.xianyu.FlywheelXianYuClient;
import com.seeease.flywheel.web.controller.xianyu.RequestHandleContext;
import com.seeease.flywheel.web.controller.xianyu.XianYuConfig;
import com.seeease.flywheel.web.controller.xianyu.result.QiMenBaseResult;
import com.taobao.api.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author Tiro
 * @date 2023/10/12
 */
@Slf4j
@RestController
@RequestMapping("/xyCallback")
public class XyRecycleCallbackController {
    @Resource
    private RequestHandleContext context;
    @Resource
    private FlywheelXianYuClient flywheelXianYuClient;

    /**
     * @return
     */
    @PostMapping("/receive")
    public Object receive(HttpServletRequest request) {
        String reqBody;
        try {
            // 直接从HttpServletRequest的Reader流中获取RequestBody
            BufferedReader reader = request.getReader();
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            reqBody = builder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //请求头数据
        HashMap<String, String> parameterMap = new HashMap();
        request.getParameterMap().keySet().forEach(key -> parameterMap.put(key, request.getParameter(key)));

        //平台签名
        String sign = parameterMap.get("sign");

        //计算签名
        String signCalculate = TaoBaoSignUtil.signTopRequest(parameterMap, reqBody, XianYuConfig.APP_SECRET, Optional.ofNullable(parameterMap.get("sign_method")).orElse(Constants.SIGN_METHOD_MD5));

        log.info("闲鱼回调数据 parameterMap=[{}], reqBody=[{}], 签名[{}-{}]", JSONObject.toJSONString(parameterMap), reqBody, sign, signCalculate);

        if (!StringUtils.equals(sign, signCalculate)) {
            return QiMenBaseResult.buildSignFail();
        }
        //请求数据
        return context.handle(parameterMap.get("method"), JSONObject.parseObject(reqBody));
    }


    @PostMapping("/queryOrder")
    public Object queryOrder(@RequestParam("orderId") Long orderId) {
        return flywheelXianYuClient.queryOrder(orderId);
    }
}
