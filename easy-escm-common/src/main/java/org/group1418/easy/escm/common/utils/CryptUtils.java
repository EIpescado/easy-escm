package org.group1418.easy.escm.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.group1418.easy.escm.common.exception.EasyEscmException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全相关工具类
 *
 * @author yuqian 2024年4月8日 10:07:02
 */
public class CryptUtils {
    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "publicKey";
    /**
     * 私钥
     */
    private static final String PRIVATE_KEY = "privateKey";
    /**
     * AES秘钥长度要求为16位、24位、32位
     */
    private static final int[] AES_LENGTH_ARRAY = {16, 24, 32};

    /**
     * Base64加密
     *
     * @param data 待加密数据
     * @return 加密后字符串
     */
    public static String encryptBase64(String data) {
        return Base64.encode(data);
    }

    /**
     * Base64解密
     *
     * @param data 待解密数据
     * @return 解密后字符串
     */
    public static String decryptBase64(String data) {
        return Base64.decodeStr(data);
    }

    /**
     * AES加密
     *
     * @param data     待解密数据
     * @param password 秘钥字符串
     * @return 加密后字符串, 采用Base64编码
     */
    public static String encryptAes(String data, String password) {
        Assert.notBlank(password);
        checkLength(password);
        return SecureUtil.aes(password.getBytes(StandardCharsets.UTF_8)).encryptBase64(data, StandardCharsets.UTF_8);
    }

    private static void checkLength(String password) {
        if (!ArrayUtil.contains(AES_LENGTH_ARRAY, password.length())) {
            throw EasyEscmException.i18n("params.aes.private.key.length.limit");
        }
    }


    /**
     * AES解密
     *
     * @param data     待解密数据
     * @param password 秘钥字符串
     * @return 解密后字符串
     */
    public static String decryptAes(String data, String password) {
        Assert.notBlank(password);
        checkLength(password);
        return SecureUtil.aes(password.getBytes(StandardCharsets.UTF_8)).decryptStr(data, StandardCharsets.UTF_8);
    }

    /**
     * 产生RSA加解密需要的公钥和私钥
     *
     * @return 公私钥Map
     */
    public static Map<String, String> generateRsaKey() {
        Map<String, String> keyMap = new HashMap<>(2);
        RSA rsa = SecureUtil.rsa();
        keyMap.put(PRIVATE_KEY, rsa.getPrivateKeyBase64());
        keyMap.put(PUBLIC_KEY, rsa.getPublicKeyBase64());
        return keyMap;
    }

    /**
     * rsa公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密后字符串, 采用Base64编码
     */
    public static String encryptRsa(String data, String publicKey) {
        Assert.notBlank(publicKey);
        RSA rsa = SecureUtil.rsa(null, publicKey);
        return rsa.encryptBase64(data, StandardCharsets.UTF_8, KeyType.PublicKey);
    }

    /**
     * rsa公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return 加密后字符串, 采用Hex编码
     */
    public static String encryptByRsaHex(String data, String publicKey) {
        if (StrUtil.isBlank(publicKey)) {
            throw EasyEscmException.i18n("rsa.need.public.key");
        }
        RSA rsa = SecureUtil.rsa(null, publicKey);
        return rsa.encryptHex(data, StandardCharsets.UTF_8, KeyType.PublicKey);
    }

    /**
     * rsa私钥解密
     *
     * @param data       待加密数据
     * @param privateKey 私钥
     * @return 解密后字符串
     */
    public static String decryptRsa(String data, String privateKey) {
        if (StrUtil.isBlank(privateKey)) {
            throw EasyEscmException.i18n("rsa.need.private.key");
        }
        RSA rsa = SecureUtil.rsa(privateKey, null);
        return rsa.decryptStr(data, KeyType.PrivateKey, StandardCharsets.UTF_8);
    }

}
