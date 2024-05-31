package org.group1418.easy.escm.common.converter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.group1418.easy.escm.common.enums.IBaseEnum;

/**
 * 枚举 excel converter
 *
 * @author yq 2024年5月30日 17:37:48
 */
public class BaseEnumConverter implements Converter<IBaseEnum> {

    private final Class<?> supportJavaTypeKey;

    public BaseEnumConverter(Class<?> supportJavaTypeKey) {
        this.supportJavaTypeKey = supportJavaTypeKey;
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return supportJavaTypeKey;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public IBaseEnum convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String enumVal = cellData.getStringValue();
        return null;
    }

    @Override
    public WriteCellData<?> convertToExcelData(IBaseEnum value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new WriteCellData(value != null ? value.toString() : StrUtil.EMPTY);
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<IBaseEnum> context) throws Exception {
        return convertToExcelData(context.getValue(), null, null);
    }
}
