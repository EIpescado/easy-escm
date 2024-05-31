package org.group1418.easy.escm.common.converter;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.group1418.easy.escm.common.utils.PudgeUtil;

/**
 * 布尔值转中文 是/否
 *
 * @author yq 2020/07/03 14:09
 */
public class BooleanCnConverter implements Converter<Boolean> {


    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Boolean convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return BooleanUtil.toBooleanObject(cellData.getStringValue());
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Boolean> context) throws Exception {
        Boolean val = context.getValue();
        return new WriteCellData<>(PudgeUtil.boolToChinese(val));
    }

}
