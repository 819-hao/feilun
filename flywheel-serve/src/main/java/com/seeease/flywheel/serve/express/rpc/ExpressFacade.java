package com.seeease.flywheel.serve.express.rpc;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.seeease.flywheel.express.IExpressFacade;
import com.seeease.flywheel.express.request.infrastructure.SfExpressAccessTokenRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressCancelOrderRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressCreateOrderRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressTrackOrderRequest;
import com.seeease.flywheel.express.result.infrastructure.*;
import com.seeease.flywheel.serve.express.infrastructure.SfExpressConfig;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.IServiceCodeStandard;
import com.sf.csim.express.service.code.ExpressServiceCodeEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/26 14:54
 */
@DubboService(version = "1.0.0")
@Slf4j
public class ExpressFacade implements IExpressFacade {

    private static final MediaType JSON = MediaType.get("application/x-www-form-urlencode");

    @NacosValue(value = "${express.sf.gateway.service:https://sfapi-sbox.sf-express.com/std/service}", autoRefreshed = true)
    private String gatewayService;

    /**
     * XYWLKX3CG59P
     * 合作伙伴编码（即顾客编码）
     */
    @NacosValue(value = "${express.sf.partnerID:XYWLKX3CG59P}", autoRefreshed = true)
    private String partnerID;

    /**
     * Q9erZakvxyPasF0HIoJny8sJmUOgncKZ
     * 合作伙伴密钥 （即校验码）
     */
    @NacosValue(value = "${express.sf.secret:Q9erZakvxyPasF0HIoJny8sJmUOgncKZ}", autoRefreshed = true)
    private String secret;

    /**
     * accessToken 基本对象
     */
    @Resource
    private SfExpressConfig sfExpressConfig;

    @Override
    public SfSfExpressAccessTokenResult getAccessToken(SfExpressAccessTokenRequest request) {
        return Objects.nonNull(request.getPartnerID()) ? sfExpressConfig.getAccessToken(request.getPartnerID()) : sfExpressConfig.getAccessToken();
    }

    @Override
    public SfExpressCreateOrderResult createOrder(SfExpressCreateOrderRequest request) {
        return Optional.ofNullable(send(JSONObject.toJSONString(request), ExpressServiceCodeEnum.EXP_RECE_CREATE_ORDER, request.getRequestID()))
                .map(m -> JSONObject.parseObject(m, SfExpressCreateOrderResult.class))
                .orElse(null);
    }

    @Override
    public SfExpressTrackOrderResult track(SfExpressTrackOrderRequest request) {
        return Optional.ofNullable(send(JSONObject.toJSONString(request), ExpressServiceCodeEnum.EXP_RECE_SEARCH_ROUTES, IdUtil.simpleUUID()))
                .map(m -> JSONObject.parseObject(m, SfExpressTrackOrderResult.class))
                .filter(d -> d.getSuccess().equals("true") && d.getErrorCode().equals("S0000"))
                .orElse(null);
    }

    @Override
    public SfExpressCancelOrderResult cancel(SfExpressCancelOrderRequest request) {
        return Optional.ofNullable(send(JSONObject.toJSONString(request), ExpressServiceCodeEnum.EXP_RECE_UPDATE_ORDER, IdUtil.simpleUUID()))
                .map(m -> JSONObject.parseObject(m, SfExpressCancelOrderResult.class))
                .filter(d -> d.getSuccess().equals("true") && d.getErrorCode().equals("S0000"))
                .orElse(null);
    }

    /**
     * 发送
     *
     * @param msg       业务报文
     * @param service   服务
     * @param requestId 业务参数
     * @return
     */
    public String send(String msg, IServiceCodeStandard service, String requestId) {
        try {
            //uuid
            String timeStamp = String.valueOf(System.currentTimeMillis());
            //业务报文
            //数字签名
            String digest = CallExpressServiceTools.getMsgDigest(msg, timeStamp, secret);
            //数据包
            RequestBody body = RequestBody.create(msg, JSON);

            Request r = new Request.Builder()
                    .url(gatewayService
                            + "?serviceCode=" + service.getCode()
                            + "&partnerID=" + partnerID
                            + "&requestID=" + requestId
                            + "&timestamp=" + timeStamp
                            + "&msgDigest=" + digest
                            + "&msgData=" + msg
                    )
                    .post(body)
                    .build();
            String responseBody = new OkHttpClient().newCall(r).execute().body().string();
            log.info("顺丰执行成功:{}-{}", msg, responseBody);

            return Optional.ofNullable(responseBody)
                    .map(a -> JSONObject.parseObject(a, BaseSfExpressBusinessResult.class))
                    .filter(b -> b.getApiResultCode().equals("A1000") && Objects.nonNull(b.getApiResultData()))
                    .map(e -> e.getApiResultData())
                    .orElse(null);
        } catch (Exception e) {
            log.error("顺丰执行失败:{}{}", msg, e.getMessage(), e);
        }

        return null;
    }
}
