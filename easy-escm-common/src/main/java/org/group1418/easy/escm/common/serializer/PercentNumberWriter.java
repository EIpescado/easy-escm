package org.group1418.easy.escm.common.serializer;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * 数字转百分数
 *
 * @author yq
 * @date 2023年9月1日 11:33:07
 */
public class PercentNumberWriter implements ObjectWriter<BigDecimal> {

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        Number value = (Number) object;
        if (value == null) {
            jsonWriter.writeNull();
        } else {
            jsonWriter.writeString(NumberUtil.round(NumberUtil.div(value, HUNDRED), 2).toString());
        }
    }

}
