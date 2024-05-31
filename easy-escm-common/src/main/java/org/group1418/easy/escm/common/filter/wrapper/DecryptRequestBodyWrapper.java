package org.group1418.easy.escm.common.filter.wrapper;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.utils.CryptUtils;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 请求参数解密, 请求参数前端加密->后端解密
 *
 * @author yuqian 2024年4月8日 09:36:50
 */
@Slf4j
public class DecryptRequestBodyWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public DecryptRequestBodyWrapper(HttpServletRequest request, String privateKey, String headerFlag) throws IOException {
        super(request);
        // 前端随机生成AES密钥,经base64后用RAS公钥加密
        String headerAesRsa = request.getHeader(headerFlag);
        String decryptAes = CryptUtils.decryptRsa(headerAesRsa, privateKey);
        // 解密AES密钥
        String aesPassword = CryptUtils.decryptBase64(decryptAes);
        request.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
        String requestBody = new String(readBytes, StandardCharsets.UTF_8);
        // 解密 body 采用 AES 加密
        String decryptBody = CryptUtils.decryptAes(requestBody, aesPassword);
        body = decryptBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    @Override
    public int getContentLength() {
        return body.length;
    }

    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }


    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream is = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return is.read();
            }

            @Override
            public int available() {
                return body.length;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
