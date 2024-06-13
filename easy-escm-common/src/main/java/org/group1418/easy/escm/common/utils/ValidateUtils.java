package org.group1418.easy.escm.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.spring.SpringContextHolder;
import org.group1418.easy.escm.common.validator.annotation.StrCheck;
import org.group1418.easy.escm.common.wrapper.ValidResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ValidateUtils 校验工具类
 *
 * @author yq 2024/2/27 11:23
 */
@Slf4j
public class ValidateUtils {

    private static final Validator VALIDATOR = SpringContextHolder.getBean(Validator.class);

    /**
     * 校验参数
     *
     * @param object 待校验对象
     * @param groups 校验组别
     * @param <T>    待校验类型
     */
    public static <T> void validate(T object, Class<?>... groups) {
        validate(null, object, groups);
    }

    /**
     * 校验参数
     *
     * @param tipPrefix 提示前缀
     * @param object    待校验对象
     * @param groups    校验组别
     * @param <T>       待校验类型
     */
    public static <T> void validate(String tipPrefix, T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            String message = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(StrUtil.COMMA));
            throw EasyEscmException.simple(StrUtil.nullToEmpty(tipPrefix) + message);
        }
    }

    public static ValidResult maxLength(String value, String fieldName, boolean required, int maxLength) {
        return checkData(value, fieldName, required, null, maxLength, -1, StrCheck.StringType.NONE);
    }

    public static ValidResult maxLengthTrim(String value, String fieldName, boolean required, int maxLength) {
        String val = PudgeUtil.full2HalfWithTrim(value);
        return checkData(val, fieldName, required, null, maxLength, -1, StrCheck.StringType.NONE);
    }

    public static ValidResult in(String value, String fieldName, boolean required, String[] dataArray) {
        return checkData(value, fieldName, required, dataArray, -1, -1, StrCheck.StringType.NONE);
    }

    public static ValidResult inPro(String value, String fieldName, boolean required, String... dataArray) {
        return checkData(value, fieldName, required, dataArray, -1, -1, StrCheck.StringType.NONE);
    }

    public static ValidResult checkData(String value, String fieldName,
                                        boolean required, String[] dataArray,
                                        int maxLength,
                                        int fixedLength,
                                        StrCheck.StringType type) {
        ValidResult result = new ValidResult(fieldName);
        if (StrUtil.isEmpty(value) || StrUtil.isEmpty(value.trim())) {
            return result.of(!required, "params.required");
        } else {
            //超出最大长度
            if (maxLength != -1 && value.length() > maxLength) {
                return result.no("params.max.length", maxLength);
            }
            //不为指定长度
            if (fixedLength != -1 && value.length() != fixedLength) {
                return result.no("params.fix.length", fixedLength);
            }
            //不在指定范围内
            if (ArrayUtil.isNotEmpty(dataArray) && !ArrayUtil.containsAny(dataArray, value)) {
                return result.no("params.invalid");
            }
            //校验类型
            if (type != null && StrCheck.StringType.NONE != type) {
                boolean pass;
                String tip = "params.invalid";
                switch (type) {
                    case PHONE:
                        pass = RegexUtil.isMobile(value);
                        break;
                    case MAIL:
                        pass = RegexUtil.isMail(value);
                        break;
                    case TEL:
                        pass = RegexUtil.isTel(value);
                        break;
                    case NO_CHINESE:
                        pass = !RegexUtil.isContainChinese(value);
                        tip = "params.no.chinese";
                        break;
                    case DATE:
                        pass = DateTimeUtil.isDate(value);
                        break;
                    case TIME:
                        pass = DateTimeUtil.isTime(value);
                        break;
                    case PHONE_OR_MAIL:
                        pass = RegexUtil.isPhoneOrMail(value);
                        break;
                    default:
                        pass = true;
                }
                return result.of(pass, tip);
            }
            return result.pass();
        }
    }

    public static ValidResult checkBigDecimal(BigDecimal value, String fieldName, boolean required, int precision, int scale, boolean gtZero) {
        ValidResult result = new ValidResult(fieldName);
        if (value == null) {
            return result.of(!required, "params.required");
        } else {
            if (value.precision() > precision) {
                return result.no("params.precision", Integer.toString(precision));
            }
            if (value.scale() > scale) {
                return result.no("params.scale", Integer.toString(scale));
            }
            if (gtZero && !NumberUtil.isGreater(value, BigDecimal.ZERO)) {
                return result.no("params.gt0");
            }
        }
        return result.pass();
    }

    public static ValidResult parseBigDecimal(String str, String fieldName, boolean required, int precision, int scale, boolean gtZero) {
        String strTrim = RegexUtil.removeAllSpecialChar(StrUtil.nullToEmpty(PudgeUtil.full2HalfWithTrim(str)));
        boolean notBlank = StrUtil.isNotBlank(strTrim);
        if (notBlank && !NumberUtil.isNumber(strTrim)) {
            throw EasyEscmException.i18n("params.invalid", fieldName);
        }
        BigDecimal result = notBlank ? new BigDecimal(strTrim) : null;
        return checkBigDecimal(result, fieldName, required, precision, scale, gtZero);
    }
}
