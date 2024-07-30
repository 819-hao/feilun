package com.seeease.flywheel.web.infrastructure.notify.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.seeease.flywheel.web.infrastructure.notify.message.WxCpMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MiniProgramNoticeMessage extends WxCpMessage implements Serializable {
    @JSONField(name = "miniprogram_notice")
    private MiniProgramNotice miniProgramNotice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MiniProgramNotice implements Serializable {
        @JSONField(name = "appid")
        private String appid;
        @JSONField(name = "page")
        private String page;
        @JSONField(name = "title")
        private String title;
        @JSONField(name = "description")
        private String description;
        @JSONField(name = "emphasis_first_item")
        private boolean emphasisFirstItem;
        @JSONField(name = "content_item")
        private List<ContentItem> contentItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentItem implements Serializable {
        /**
         * key值
         */
        private String key;
        /**
         * value值
         */
        private String value;
    }

}
