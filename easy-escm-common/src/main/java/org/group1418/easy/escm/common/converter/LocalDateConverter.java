package org.group1418.easy.escm.common.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.group1418.easy.escm.common.utils.DateTimeUtil;

import java.time.LocalDate;

/**
 * 日期转化 是/否
 *
 * @author yq 2024年6月3日 17:20:39
 */
public class LocalDateConverter implements Converter<LocalDate> {


    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String date = cellData.getStringValue();
        return DateTimeUtil.parseDate(date);
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<LocalDate> context) throws Exception {
        LocalDate value = context.getValue();
        return new WriteCellData<>(DateTimeUtil.formatDate(value));
    }

}
