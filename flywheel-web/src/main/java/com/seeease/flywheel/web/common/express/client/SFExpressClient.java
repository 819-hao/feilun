package com.seeease.flywheel.web.common.express.client;

import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.web.common.express.SFExpressConfig;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.code.ExpressServiceCodeEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@Slf4j
public class SFExpressClient {
    private static final MediaType JSON = MediaType.get("application/x-www-form-urlencode");

    private SFExpressConfig clientConfig;

    public SFExpressClient(SFExpressConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    public SfExpressCreateOrderResult createOrder(SfExpressCreateOrderRequest request) {
        request.setService(ExpressServiceCodeEnum.EXP_RECE_CREATE_ORDER);

        //默认月结帐号
        if (Objects.isNull(request.getMonthlyCard())) {
            request.setMonthlyCard(clientConfig.getMonthlyCard());
        }

        String data = this.execute(request);
        SfExpressCreateOrderResult result = JSONObject.parseObject(data, SfExpressCreateOrderResult.class);
        if (result.getSuccess().equals("true") && result.getErrorCode().equals("S0000")) {
            return result;
        }
        throw new SfExpressException(result.getErrorMsg());
    }

    /**
     * 打印订单
     *
     * @param request
     * @return
     */
    public SfExpressTrackOrderResult trackOrder(SfExpressTrackOrderRequest request) {
        request.setService(ExpressServiceCodeEnum.EXP_RECE_SEARCH_ROUTES);

        String data = this.execute(request);
        SfExpressTrackOrderResult result = JSONObject.parseObject(data, SfExpressTrackOrderResult.class);
        if (result.getSuccess().equals("true") && result.getErrorCode().equals("S0000")) {
            return result;
        }
        throw new SfExpressException(result.getErrorMsg());
    }

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    public SfExpressCancelOrderResult cancelOrder(SfExpressCancelOrderRequest request) {
        request.setService(ExpressServiceCodeEnum.EXP_RECE_UPDATE_ORDER);

        String data = this.execute(request);
        SfExpressCancelOrderResult result = JSONObject.parseObject(data, SfExpressCancelOrderResult.class);
        if (result.getSuccess().equals("true") && result.getErrorCode().equals("S0000")) {
            return result;
        }
        throw new SfExpressException(result.getErrorMsg());
    }

    /**
     * 发送
     *
     * @param request
     * @return
     */
    private String execute(SfExpressBaseRequest request) {
        try {
            //业务报文
            String msg = JSONObject.toJSONString(request);
            //uuid
            String timeStamp = String.valueOf(System.currentTimeMillis());
            //数字签名
            String digest = CallExpressServiceTools.getMsgDigest(msg, timeStamp, clientConfig.getSecret());
            //数据包
            RequestBody body = RequestBody.create(msg, JSON);

            Request r = new Request.Builder()
                    .url(clientConfig.getGatewayService()
                            + "?serviceCode=" + request.getService().getCode()
                            + "&partnerID=" + clientConfig.getPartnerID()
                            + "&requestID=" + request.getRequestId()
                            + "&timestamp=" + timeStamp
                            + "&msgDigest=" + digest
                            + "&msgData=" + msg)
                    .post(body)
                    .build();
            String responseBody = new OkHttpClient().newCall(r).execute().body().string();
            log.info("顺丰请求成功 request=[{}],result=[{}]", msg, responseBody);

            SfExpressBaseResult result = Optional.ofNullable(responseBody)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> JSONObject.parseObject(t, SfExpressBaseResult.class))
                    .orElse(null);
            if (Objects.isNull(result)) {
                throw new SfExpressException("请求异常结果为空");
            }
            if (result.getApiResultCode().equals("A1000")) {
                return result.getApiResultData();
            }
            throw new SfExpressException(result.getApiErrorMsg());
        } catch (SfExpressException e) {
            throw e;
        } catch (Exception e) {
            throw new SfExpressException(e);
        }
    }
}
