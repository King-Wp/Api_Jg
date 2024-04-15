package com.ruoyi.common.enums;

import lombok.Getter;

/**
 * @author: 11653
 * @createTime: 2024/03/25 11:40
 * @package: com.ruoyi.common.enums
 * @description:
 */
@Getter
public enum UrlAddressEnum {

    TOKEN_API("7b1f73a2-3709-4be1-ae59-6536d47aae1b");
//    BASE_INFO_V2("http://open.api.tianyancha.com/services/open/ic/baseinfoV2/2.0"),
//    APP_BK_INFO("http://open.api.tianyancha.com/services/open/m/"),
//    PROFILE("http://open.api.tianyancha.com/services/v4/open/profile/"),
//    COMPANY("https://www.tianyancha.com/company/"),
//    CERTIFICATE("http://open.api.tianyancha.com/services/open/m/certificate/");
    private final String val;

    UrlAddressEnum(String url) {
        this.val = url;
    }

}
