package org.group1418.easy.escm.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.converter.BaseEnumConverter;
import org.group1418.easy.escm.common.converter.BooleanCnConverter;
import org.group1418.easy.escm.common.converter.LocalDateConverter;
import org.group1418.easy.escm.common.enums.IBaseEnum;
import org.group1418.easy.escm.common.exception.BaseEasyEscmException;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * excel工具
 *
 * @author yq  2022年3月14日 10:42:43
 */
@Slf4j
public class ExcelUtil {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_SHEET_NAME = "sheet1";
    /**
     * 内置的一些额外 converter
     */
    private static final List<Converter<?>> INNER_EXTRA_CONVERTER_LIST = ListUtil.of(new BooleanCnConverter(), new LocalDateConverter());

    /**
     * 导出xlsx
     *
     * @param realFileName 导出文件名称
     * @param response     响应流
     * @param exportFun    导出fun
     */
    public static void exportXlsx(String realFileName, HttpServletResponse response, ExportFun exportFun) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            //文件名称
            String fileName = URLEncoder.encode(realFileName, "UTF-8");
            if (!fileName.endsWith(XLSX)) {
                fileName = fileName + XLSX;
            }
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            exportFun.execute(response.getOutputStream());
            log.info("导出xlsx完毕");
        } catch (Exception e) {
            log.info("导出xlsx异常[{}]", e.getLocalizedMessage());
            //重置response
            response.reset();
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(PudgeUtil.APPLICATION_JSON_UTF_8);
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().println(JSON.toJSONString(R.fail(e.getLocalizedMessage())));
            } catch (IOException ioException) {
                log.info("导出xlsx响应异常[{}]", e.getLocalizedMessage());
            }
        }
    }

    /**
     * 导出xlsx
     *
     * @param data         数据
     * @param clazz        class
     * @param realFileName 文件名,可不带后缀
     */
    public static <T> void exportXlsx(List<T> data, Class<T> clazz, String realFileName, HttpServletResponse response) {
        exportXlsx(realFileName, response, outputStream -> exportXlsx(data, clazz, outputStream));
    }

    /**
     * 导出xlsx
     *
     * @param data         数据
     * @param clazz        class
     * @param outputStream 输出流
     */
    private static <T> void exportXlsx(List<T> data, Class<T> clazz, OutputStream outputStream) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream, clazz).autoCloseStream(false);
        registerCommonConverter(null, excelWriterBuilder, clazz);
        excelWriterBuilder.sheet(DEFAULT_SHEET_NAME).doWrite(data);
    }

    /**
     * 导出xlsx
     *
     * @param headList        表头
     * @param data            数据
     * @param outputStream    输出流
     * @param extraConverters 额外的转化器
     */
    private static <T> void exportXlsx(List<List<String>> headList, List<List<Object>> data, OutputStream outputStream,
                                       Collection<Converter<?>> extraConverters, Class<T> clazz) {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream).autoCloseStream(false);
        registerCommonConverter(null, excelWriterBuilder, clazz);
        if (CollUtil.isNotEmpty(extraConverters)) {
            extraConverters.forEach(excelWriterBuilder::registerConverter);
        }
        ExcelWriterSheetBuilder sheet1 = excelWriterBuilder.sheet(DEFAULT_SHEET_NAME);
        if (CollUtil.isNotEmpty(headList)) {
            sheet1.head(headList);
        }
        sheet1.doWrite(data);
    }

    /**
     * 自定义字段及顺序导出xlsx
     *
     * @param data         数据
     * @param clazz        class
     * @param realFileName 文件名,可不带后缀
     * @param exportCols   需要导出的字段,按此顺序
     */
    public static <T> void exportXlsx(List<T> data, Class<T> clazz, String realFileName, List<String> exportCols, HttpServletResponse response) {
        log.info("导出[{}],自定义字段[{}]个", clazz.getSimpleName(), CollUtil.size(exportCols));
        if (CollUtil.isEmpty(exportCols)) {
            exportXlsx(data, clazz, realFileName, response);
        } else {
            //动态表头
            final List<List<String>> headList = new ArrayList<>(exportCols.size());
            //需要导出字段 在类中的属性对象
            List<Field> exportFieldList = new ArrayList<>(exportCols.size());
            //读取类中所有属性
            Map<String, Field> dataFieldMap = Arrays.stream(ReflectUtil.getFields(clazz)).collect(Collectors.toMap(Field::getName, Function.identity(), (a, b) -> a));
            //部分字段可能配置了 @ExcelProperty 的converter 或 @DateTimeFormat
            Map<String, DateTimeFormatter> timeFormatMap = new HashMap<>(dataFieldMap.size());
            Map<String, Converter<?>> converterMap = new HashMap<>(dataFieldMap.size());
            //防止重复new converter对象
            Map<String, Converter<?>> converterClassMap = new HashMap<>(dataFieldMap.size());
            //是否过滤未被注解的属性
            boolean ignoreUnannotated = clazz.getAnnotation(ExcelIgnoreUnannotated.class) != null;
            //循环获取属性和描述,保证顺序
            exportCols.forEach(col -> {
                Field field = dataFieldMap.get(col);
                if (field != null) {
                    ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                    boolean ignoreField = field.getAnnotation(ExcelIgnore.class) != null;
                    if (annotation != null) {
                        if (ignoreField) {
                            return;
                        }
                        if (timeFormatSupport(field, field.getType(), col, timeFormatMap)) {
                            //配置了 @ExcelProperty 的converter,即自定义转化类
                            Class<? extends Converter<?>> converter = annotation.converter();
                            if (!converter.equals(AutoConverter.class)) {
                                converterMap.put(col, PudgeUtil.getAndPutIfNotExist(converterClassMap, converter.getName(), converterName -> ReflectUtil.newInstance(converter)));
                            }
                        }
                        //表头描述
                        headList.add(ListUtil.of(annotation.value()[0]));
                        exportFieldList.add(field);
                    } else {
                        //不过滤未被注解的 使用属性名做header
                        if (!ignoreUnannotated && !ignoreField) {
                            timeFormatSupport(field, field.getType(), col, timeFormatMap);
                            headList.add(ListUtil.of(field.getName()));
                            exportFieldList.add(field);
                        }
                    }
                }

            });
            if (CollUtil.isEmpty(data)) {
                exportXlsx(realFileName, response, outputStream -> exportXlsx(headList, new ArrayList<>(), outputStream, null, clazz));
            } else {
                //动态数据
                final List<List<Object>> newDataList = data.stream().map(row -> {
                    List<Object> rowDataList = new ArrayList<>(exportFieldList.size());
                    exportFieldList.forEach(field -> {
                        Object fieldValue = ReflectUtil.getFieldValue(row, field);
                        DateTimeFormatter dateTimeFormatter = timeFormatMap.get(field.getName());
                        if (dateTimeFormatter != null) {
                            if (fieldValue == null) {
                                rowDataList.add(null);
                            } else if (field.getType().equals(LocalDateTime.class)) {
                                rowDataList.add(LocalDateTimeUtil.format((LocalDateTime) fieldValue, dateTimeFormatter));
                            } else if (field.getType().equals(LocalDate.class)) {
                                rowDataList.add(LocalDateTimeUtil.format((LocalDate) fieldValue, dateTimeFormatter));
                            }
                        } else {
                            // 其他枚举默认使用toString
                            if (fieldValue instanceof IBaseEnum) {
                                rowDataList.add(fieldValue.toString());
                            } else if (fieldValue instanceof Enum) {
                                rowDataList.add(fieldValue.toString());
                            } else {
                                rowDataList.add(fieldValue);
                            }
                        }
                    });
                    return rowDataList;
                }).collect(Collectors.toList());
                exportXlsx(realFileName, response, outputStream -> exportXlsx(headList, newDataList, outputStream, converterMap.values(), clazz));
            }
        }
    }

    private static boolean timeFormatSupport(Field field, Class<?> type, String col, Map<String, DateTimeFormatter> timeFormatMap) {
        //日期类型 额外转化,可能配了注解
        if (type.equals(LocalDateTime.class) || type.equals(LocalDate.class)) {
            //日期类型 额外转化,可能配了注解,未配置给默认
            DateTimeFormat dateAnnotation = field.getAnnotation(DateTimeFormat.class);
            if (dateAnnotation != null) {
                timeFormatMap.put(col, DateTimeFormatter.ofPattern(dateAnnotation.value()));
            } else {
                if (type.equals(LocalDate.class)) {
                    //默认日期格式化
                    timeFormatMap.put(col, DateTimeUtil.YYYY_MM_DD_FORMATTER);
                } else {
                    //默认日期格式化
                    timeFormatMap.put(col, DateTimeUtil.DEFAULT_FORMATTER);
                }
            }
            return true;
        }
        return false;
    }

    @Data
    public static class CustomWriteHandler implements CellWriteHandler {
        private CellWriteHandlerContext context;

        @Override
        public void afterCellCreate(CellWriteHandlerContext context) {
            CellWriteHandler.super.afterCellCreate(context);
            this.context = context;
        }
    }

    /**
     * 导入模版
     *
     * @param fileName            文件名
     * @param headRowNumber       解析起始行号
     * @param inputStreamSupplier 输入流supplier
     * @param clazz               一行数据的类型
     * @param rowDataConsumer     行数据消费函数
     * @param <T>                 行数据类型
     */
    public static <T> void importFile(String fileName, Integer headRowNumber, InputStreamSupplier inputStreamSupplier, Class<T> clazz, RowDataConsumer<T> rowDataConsumer) {
        InputStream inputStream = null;
        try {
            log.info("导入文件[{}]开始", fileName);
            inputStream = inputStreamSupplier.get();
            ExcelReaderBuilder readerBuilder = EasyExcel.read(inputStream, clazz, new ReadListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    rowDataConsumer.accept(data, context, I18nUtil.getMessage("excel.import.row.no", context.readRowHolder().getRowIndex() + 1));
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("文件[{}]解析完毕", fileName);
                }

                @Override
                public void onException(Exception exception, AnalysisContext context) throws Exception {
                    if (exception instanceof BaseEasyEscmException) {
                        log.info("解析导入文件[{}]异常[{}]", fileName, exception.getLocalizedMessage());
                        throw exception;
                    } else if (exception instanceof ExcelDataConvertException) {
                        ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
                        int row = excelDataConvertException.getRowIndex() + 1;
                        int column = excelDataConvertException.getColumnIndex() + 1;
                        log.error("第[{}]行第[{}]列解析异常，数据为:[{}]", row, column, excelDataConvertException.getCellData().getStringValue());
                        throw EasyEscmException.i18n("excel.import.which.row.error", row, column);
                    } else {
                        log.error("文件解析异常:[{}]", exception.getLocalizedMessage());
                        throw EasyEscmException.i18n("excel.import.error");
                    }
                }
            });
            registerCommonConverter(readerBuilder, null, clazz);
            readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
        } catch (IOException e) {
            log.error("文件解析异常IO:[{}]", e.getLocalizedMessage());
            throw EasyEscmException.i18n("excel.import.error");
        } finally {
            IoUtil.close(inputStream);
        }
    }


    /**
     * 注册 默认的converter
     *
     * @param readerBuilder 读
     * @param writerBuilder 写
     * @param clazz         类型
     * @param <T>           类型
     */
    @SuppressWarnings("unchecked")
    private static <T> void registerCommonConverter(ExcelReaderBuilder readerBuilder, ExcelWriterBuilder writerBuilder,
                                                    Class<T> clazz) {
        //注入自定义的转化
        if (CollUtil.isNotEmpty(INNER_EXTRA_CONVERTER_LIST)) {
            if (readerBuilder != null) {
                INNER_EXTRA_CONVERTER_LIST.forEach(readerBuilder::registerConverter);
            }
            if (writerBuilder != null) {
                INNER_EXTRA_CONVERTER_LIST.forEach(writerBuilder::registerConverter);
            }
        }
        //注入通用枚举的转化,如需要
        List<Class<? extends IBaseEnum>> baseEnums = Arrays.stream(ReflectUtil.getFields(clazz))
                .filter(f -> IBaseEnum.class.isAssignableFrom(f.getType()))
                .map(f -> (Class<? extends IBaseEnum>) f.getType())
                .distinct().collect(Collectors.toList());
        if (CollUtil.isNotEmpty(baseEnums)) {
            baseEnums.forEach(c -> {
                if (readerBuilder != null) {
                    readerBuilder.registerConverter(new BaseEnumConverter(c));
                }
                if (writerBuilder != null) {
                    writerBuilder.registerConverter(new BaseEnumConverter(c));
                }
            });
        }
    }

    @FunctionalInterface
    public interface InputStreamSupplier {

        /**
         * 获取流
         *
         * @return 输入流
         */
        InputStream get() throws IOException;
    }

    @FunctionalInterface
    public interface ExportFun {
        void execute(OutputStream outputStream) throws IOException;
    }

    @FunctionalInterface
    public interface RowDataConsumer<T> {
        /**
         * 一行数据消费者
         *
         * @param t       行数据对象
         * @param context 解析上下文
         * @param noText  行描述, 即 第N行,方便前端显示
         */
        void accept(T t, AnalysisContext context, String noText);
    }
}

