package org.group1418.easy.escm.common.deserializer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import org.group1418.easy.escm.common.utils.PudgeUtil;

import java.lang.reflect.Type;

/**
 * 字符全角转半角移除前后空格
 * @author yq 2021/10/13 9:26
 */
public class StrFull2HalfTrimReader implements ObjectReader<String> {

    @Override
    public String readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        if (jsonReader.nextIfNull()) {
            return null;
        }
        String s = jsonReader.readString();
        if (StrUtil.isNotBlank(s)) {
            return PudgeUtil.full2Half(s).trim();
        }
        return null;
    }
}
