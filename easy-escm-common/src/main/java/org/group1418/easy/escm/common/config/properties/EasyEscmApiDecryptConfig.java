package org.group1418.easy.escm.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * EasyEscmApiDecryptConfig
 * 接口数据加解密配置
 * @author yq 2024/6/14 10:05
 */
@ConfigurationProperties(prefix = "easy.escm.api-decrypt-config")
@Data
@Component
public class EasyEscmApiDecryptConfig {
    /**
     * 前端传递的AES加密头标识 由前端生成AES密钥 -> base64编码 -> 使用公钥加密密钥
     * 请求参数 使用AES加密,
     */
    private String headerName;
    /**
     * 请求参数解密 私钥
     */
    private String requestPrivateKey;
    /**
     * 响应参数加密 公钥
     */
    private String responsePublicKey;
}
