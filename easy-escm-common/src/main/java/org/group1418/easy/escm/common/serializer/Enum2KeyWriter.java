package org.group1418.easy.escm.common.serializer;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

/**
 * 枚举字段返回前端2个字段, 如 org 返回 toString, orgEnum 返回 name
 *
 * @author yq
 * @date 2023年9月1日 11:33:07
 */
public class Enum2KeyWriter implements ObjectWriter<Enum<?>> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        Enum<?> value = (Enum<?>) object;
        if (value == null) {
            jsonWriter.writeNull();
        } else {
            String f = fieldName.toString();
            jsonWriter.writeEnum(value);
            //额外返回枚举的name
            jsonWriter.writeName(f + "Enum");
            jsonWriter.writeColon();
            jsonWriter.writeString(((Enum<?>) object).name());
        }
    }

}
