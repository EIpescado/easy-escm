package org.group1418.easy.escm.common.serializer;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.support.redission.JSONCodec;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * FastJson2JsonRedissonCodec redisson使用fastjson2 codec
 *
 * @author yq 2024/6/7 18:33
 */
@Slf4j
public class FastJson2JsonRedissonCodec extends JSONCodec {

    static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(
            // 按需加上需要支持自动类型的类名前缀，范围越小越安全
            "org.group1418",
            "cn.dev33.satoken"
    );

    private final Encoder encoder;
    private final Decoder<Object> decoder;

    public FastJson2JsonRedissonCodec(ClassLoader classLoader, FastJson2JsonRedissonCodec codec) {
        super(Object.class);
        JSONCodec fastIns = new JSONCodec(
                JSONFactory.createWriteContext(JSONWriter.Feature.WriteClassName),
                JSONFactory.createReadContext(AUTO_TYPE_FILTER)
        );
        log.info("new FastJson2JsonRedissonCodec");
        encoder = fastIns.getValueEncoder();
        decoder = fastIns.getValueDecoder();
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
