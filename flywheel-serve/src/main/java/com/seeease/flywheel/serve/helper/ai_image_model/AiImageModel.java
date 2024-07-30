package com.seeease.flywheel.serve.helper.ai_image_model;

public interface AiImageModel {

    /**
     * 从网络加载入库图片
     * @param url   图片url
     * @param brief 图片对应关键字
     */
    void submitFormUrl(String url,String brief);

    /**
     * 图片匹配
     * @param imageBytes 图片数组
     * @return 返回入库时的brief
     */
    String match(byte[] imageBytes);

    /**
     * 获取模型类型
     * @return
     */
    Model getModel();

    enum Model{
        BAIDU
    }
}
