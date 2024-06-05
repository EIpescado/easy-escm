package org.group1418.easy.escm.common.serializer;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import org.group1418.easy.escm.common.utils.DateTimeUtil;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate 转字符
 *
 * @author yq 2024年6月5日 15:58:27
 */
public class LocalDateWriter implements ObjectWriter<LocalDate> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        LocalDate value = (LocalDate) object;
        if (value == null) {
            jsonWriter.writeNull();
        } else {
            String dateFormat = jsonWriter.getContext().getDateFormat();
            DateTimeFormatter dateFormatter = jsonWriter.getContext().getDateFormatter();
            jsonWriter.writeString(DateTimeUtil.formatDate(value));
        }
    }

}
