package org.group1418.easy.escm.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.symmetric.DES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
import org.group1418.easy.escm.common.exception.SystemCustomException;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yq
 * @date 2021年4月14日 10:53:16
 * @description 一般工具
 * @since V1.0.0
 */
@Slf4j
public class PudgeUtil {

    private static final Digester SHA256_DIGESTER = new Digester(DigestAlgorithm.SHA256);
    private static final DES DES = SecureUtil.des();
    private static final Pattern TEL_AND_PHONE_PATTERN = Pattern.compile("1[0-9]\\d{9}|0\\d{2,3}-[1-9]\\d{6,7}");
    private static final String LEFT_BRACKET = "(";
    private static final String LEFT_BRACKET_CN = "（";
    private static final Map<String, String[]> FILE_TYPE_MAP = new ConcurrentHashMap<String, String[]>() {
        private static final long serialVersionUID = 1997234982651928201L;

        {
            put("xls", new String[]{"doc", "msi"});
            put("zip", new String[]{"docx", "xlsx", "pptx", "jar", "war"});
        }
    };
    /**
     * 中括号正则
     */
    public static final Pattern SQUARE_BRACKETS_PATTERN = Pattern.compile("\\[(.*?)]");

    public static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";

    /**
     * 移动电话
     */
    private final static Pattern MOBILE = Pattern.compile("(?:0|86|\\+86)?1[2-9]\\d{9}");
    private final static Pattern CHINESE = Pattern.compile("^[\\u4e00-\\u9fa5]*$");
    /**
     * 座机号码
     */
    private final static Pattern TEL = Pattern.compile("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)");

    private static final Pattern Y_M_D_PATTERN = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
    private static final Pattern Y_M_D_H_M_S_PATTERN = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)");
    private static final Pattern MAIL_PATTERN = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    public static final String PAI = "牌";
    public static final BigDecimal DOT_06 = new BigDecimal("0.06");
    public static final BigDecimal DOT_13 = new BigDecimal("0.13");
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final BigDecimal DOT_01 = new BigDecimal("0.01");
    public static final BigDecimal YBZ_RATE = NumberUtil.div(NumberUtil.add(BigDecimal.ONE, new BigDecimal("0.0005"), new BigDecimal("0.0009")), NumberUtil.sub(BigDecimal.ONE, new BigDecimal("0.0001")));
    public static final String HFY_DOMAIN_URL = "https://www.hfy-group.com";
    /**
     * MinIO内网根路径
     */
    private static final String MINIO_INNER_DOMAIN_URL = "http://192.168.0.250:9000/";
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"
    };
    private final static String UN_KNOWN = "unknown";
    private final static List<String> LOCALHOST_IP_LIST = CollectionUtil.newArrayList("127.0.0.1", "0:0:0:0:0:0:0:1");
    private final static Pattern STR_CHANGE_PRO_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\u4E00-\\u9FFF]|中国|省|市|县|镇|州|新|区");
    public static final String[] YES_OR_NO_CN = new String[]{"是", "否"};

    public static void responseJSON(HttpServletResponse response, R r) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_UTF_8);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().append(JSON.toJSONString(r));
        response.getWriter().flush();
        response.getWriter().close();
    }


    /**
     * 解密 HEX + DES
     *
     * @param data 待解密数据
     * @return 明文
     */
    public static String decrypt(String data) {
        return DES.decryptStr(HexUtil.decodeHex(data));
    }

    /**
     * 加密 DES + HEX
     *
     * @param data 待加密数据
     * @return 密文
     */
    public static String encrypt(String data) {
        return HexUtil.encodeHexStr(DES.encrypt(data)).toUpperCase();
    }

    /**
     * 明文密码加密
     *
     * @param plainText 明文密码
     * @return 加密后密码
     */
    public static String encodePwd(String plainText) {
        return SHA256_DIGESTER.digestHex(plainText);
    }

    /**
     * 生成redisKey
     *
     * @param keys 建
     * @return 值
     */
    public static String buildKey(Object... keys) {
        if (ArrayUtil.isNotEmpty(keys)) {
            return StrUtil.join(StrUtil.COLON, keys);
        }
        return null;
    }

    /**
     * 取文件内容的前28个字节.判定文件类型
     *
     * @param bytes 文件内容字节数据
     * @return 文件类型
     */
    public static String getFileType(String fileName, byte[] bytes) {
        //取前28个字节
        byte[] bytes28 = ArrayUtil.sub(bytes, 0, 28);
        //根据文件内容获取的文件类型
        String typeName = FileTypeUtil.getType(HexUtil.encodeHexStr(bytes28, false));
        if (StrUtil.isNotBlank(typeName)) {
            String[] mayMatchTypeArray = FILE_TYPE_MAP.get(typeName);
            //部分文件根据文件内容读取类型与扩展名不符,需转化
            if (ArrayUtil.isNotEmpty(mayMatchTypeArray)) {
                String extName = FileUtil.extName(fileName);
                if (Arrays.stream(mayMatchTypeArray).anyMatch(s -> s.equalsIgnoreCase(extName))) {
                    return extName;
                }
            }
            return typeName;
        } else {
            //部分文件根据内容无法获取文件名 直接获取扩展名
            return FileUtil.extName(fileName);
        }
    }

    public static String urlEncode(String str) {
        try {
            return StrUtil.isNotEmpty(str) ? URLEncoder.encode(str, "UTF-8") : "";
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * 生成tip code
     *
     * @param prefix    前缀
     * @param currentNo 当前号
     * @param len       补位长度 不包含前缀
     * @return tip code
     */
    public static String getTipCode(String prefix, String currentNo, int len) {
        return prefix + StrUtil.fillBefore(currentNo, '0', len);
    }

    /**
     * 分页查询包装
     *
     * @param qo     查询参数
     * @param helper 获取数据方法
     * @param <T>    返回类型
     * @return 分页查询结果
     */
    public static <T, Q extends BasePageQo> PageR<T> pageQuery(Q qo, ListPageQueryHelper<T, Q> helper) {
        return pageQuery(qo, helper, null);
    }

    /**
     * 分页查询包装
     *
     * @param qo      查询参数
     * @param helper  获取数据方法
     * @param countId 自定义countId
     * @param <T>     返回类型
     * @return 分页查询结果
     */
    public static <T, Q extends BasePageQo> PageR<T> pageQuery(Q qo, ListPageQueryHelper<T, Q> helper, String countId) {
        //自定义分页参数转 mybatis-plus分页参数
        Page<T> page = new Page<>();
        page.setSize(ObjectUtil.defaultIfNull(qo.getSize(), BasePageQo.DEFAULT_SIZE));
        page.setCurrent(ObjectUtil.defaultIfNull(qo.getPage(), BasePageQo.DEFAULT_PAGE));
        if (StrUtil.isNotBlank(countId)) {
            page.setCountId(countId);
        }
        //获得查询结果
        IPage<T> pageResult = helper.query(page, qo);
        //包装为自定义分页结果
        PageR<T> pageR = new PageR<>();
        pageR.setPages(pageResult.getPages());
        pageR.setTotal(pageResult.getTotal());
        pageR.setRows(pageResult.getRecords());
        return pageR;
    }

    /**
     * 分页查询包装
     *
     * @param qo     查询参数
     * @param helper 获取数据方法
     * @param <T>    返回类型
     * @return 分页查询结果
     */
    public static <T, Q extends BasePageQo> List<T> pageQueryButAll(Q qo, ListPageQueryHelper<T, Q> helper) {
        //自定义分页参数转 mybatis-plus分页参数
        Page<T> page = new Page<>();
        page.setSize(-1);
        page.setCurrent(ObjectUtil.defaultIfNull(qo.getPage(), BasePageQo.DEFAULT_PAGE));
        //获得查询结果
        IPage<T> pageResult = helper.query(page, qo);
        return pageResult.getRecords();
    }

    /**
     * 分页查询包装
     *
     * @param qo     查询参数
     * @param helper 获取数据方法
     * @param <T>    返回类型
     * @return 分页查询结果
     */
    public static <T, Q extends BasePageQo> PageR<T> pageQueryWithLoop(Q qo, ListPageQueryHelper<T, Q> helper, Consumer<T> singleDataConsumer) {
        return pageQueryWithLoop(qo, helper, singleDataConsumer, null);
    }

    /**
     * 分页查询包装
     *
     * @param qo     查询参数
     * @param helper 获取数据方法
     * @param <T>    返回类型
     * @return 分页查询结果
     */
    public static <T, Q extends BasePageQo> PageR<T> pageQueryWithLoop(Q qo, ListPageQueryHelper<T, Q> helper, Consumer<T> singleDataConsumer, String countId) {
        PageR<T> pageR = pageQuery(qo, helper, countId);
        if (CollUtil.isNotEmpty(pageR.getRows()) && singleDataConsumer != null) {
            pageR.getRows().forEach(singleDataConsumer);
        }
        return pageR;
    }

    public interface ListPageQueryHelper<T, Q extends BasePageQo> {
        /**
         * 分页查询
         *
         * @param page 分页参数
         * @param qo   查询参数
         * @return 查询结果
         */
        IPage<T> query(Page<T> page, Q qo);
    }

    /**
     * 是否手机
     */
    public static boolean isMobile(String str) {
        return ReUtil.isMatch(MOBILE, str);
    }

    /**
     * 是否电话
     */
    public static boolean isTel(String str) {
        return ReUtil.isMatch(TEL, str);
    }

    /**
     * 从集合中找出第一个满足条件的对象的指定字段
     *
     * @param collection 集合
     * @param predicate  条件
     * @param mapper     对象
     * @param <T>        集合泛型
     * @param <R>        返回值泛型
     * @return 指定字段
     */
    public static <T, R> R findFirstOneByPredicate(Collection<T> collection, Predicate<? super T> predicate, Function<? super T, ? extends R> mapper) {
        if (CollUtil.isEmpty(collection)) {
            return null;
        }
        Optional<T> optionalConfig = collection.stream().filter(predicate).findFirst();
        return optionalConfig.map(mapper).orElse(null);
    }

    /**
     * 从集合中找出第一个满足条件的对象
     *
     * @param collection 集合
     * @param predicate  条件
     * @param <T>        集合泛型
     * @return 对象
     */
    public static <T> T findFirstOneByPredicate(Collection<T> collection, Predicate<? super T> predicate) {
        if (CollUtil.isEmpty(collection)) {
            return null;
        }
        Optional<T> optionalConfig = collection.stream().filter(predicate).findFirst();
        return optionalConfig.orElse(null);
    }

    /**
     * 从数组中找出第一个满足条件的对象
     *
     * @param array     数组
     * @param predicate 条件
     * @param <T>       集合泛型
     * @return 对象
     */
    public static <T> T findFirstOneByPredicate(T[] array, Predicate<? super T> predicate) {
        if (ArrayUtil.isEmpty(array)) {
            return null;
        }
        Optional<T> optionalConfig = Arrays.stream(array).filter(predicate).findFirst();
        return optionalConfig.orElse(null);
    }

    /**
     * 格式化字符串
     * 例子  吃饭：{paramName}块
     *
     * @param template 模版
     * @param map      参数
     * @return 格式化后结果
     */
    public static String format(String template, Map<String, String> map) {
        if (StrUtil.isBlank(template)) {
            return template;
        }
        if (CollUtil.isNotEmpty(map)) {
            String result = template;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                result = StrUtil.replace(result, StrBuilder.create(StrUtil.DELIM_START, entry.getKey(), StrUtil.DELIM_END).toString(), StrUtil.nullToEmpty(entry.getValue()));
            }
            return result;
        }
        return template;
    }

    /**
     * 从map中获取元素,如果不存在则调用function 生成元素放入map
     *
     * @param map      map
     * @param key      key
     * @param function 生成v
     * @param <K>      key类型
     * @param <V>      值类型
     * @return 值
     */
    public static <K, V> V getAndPutIfNotExist(Map<K, V> map, K key, Function<K, V> function) {
        if (map == null) {
            return null;
        }
        if (ObjectUtil.isNotEmpty(key)) {
            V v = map.get(key);
            if (ObjectUtil.isEmpty(v)) {
                v = function.apply(key);
                map.put(key, v);
            }
            return v;
        }
        return null;
    }

    /**
     * 从map中获取指定key的当前值,执行function后将生成的新值覆盖存入map,
     *
     * @param map      map
     * @param key      key
     * @param function 生成v
     * @param <K>      key类型
     * @param <V>      value类型
     * @return 值
     */
    public static <K, V> V getAndPutAfterFun(Map<K, V> map, K key, Function<V, V> function) {
        if (map == null) {
            return null;
        }
        if (ObjectUtil.isNotEmpty(key)) {
            V lastV = map.get(key);
            V newV = function.apply(lastV);
            map.put(key, newV);
            return lastV;
        }
        return null;
    }

    /**
     * 从字符串中获取所有手机和固话
     *
     * @param str 字符串
     * @return 手机和固话
     */
    public static List<String> getTelAndPhone(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        Matcher matcher = TEL_AND_PHONE_PATTERN.matcher(str.trim().replaceAll(" ", ""));
        List<String> results = new ArrayList<>();
        while (matcher.find()) {
            results.add(matcher.group());
        }
        return results.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 全角转半角
     */
    public static String full2Half(String str) {
        return Convert.toDBC(str);
    }

    /**
     * 半角转全角
     */
    public static String half2full(String str) {
        return Convert.toSBC(str);
    }


    /**
     * 全角转半角,并移除前后空格
     */
    public static String full2HalfWithTrim(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return Convert.toDBC(str.trim());
    }

    public static String getStr(Map<String, String> params, String key, String defaultStr) {
        return StrUtil.nullToEmpty(MapUtil.getStr(params, key, defaultStr));
    }

    /**
     * 移除企业微信用户名称中的括号
     *
     * @param name
     * @return 结果
     */
    public static String removeCpUserNameBracket(String name) {
        if (StrUtil.isNotEmpty(name)) {
            String result = name;
            if (name.contains(StrUtil.BACKSLASH)) {
                result = name.substring(0, name.lastIndexOf(LEFT_BRACKET));
            } else if (name.contains(LEFT_BRACKET_CN)) {
                result = name.substring(0, name.lastIndexOf(LEFT_BRACKET_CN));
            }
            return result.trim();
        }
        return null;
    }

    /**
     * 是否包含中文
     */
    public static boolean isContainChinese(String str) {
        return ReUtil.isMatch(CHINESE, str);
    }


    public static boolean isMail(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return MAIL_PATTERN.matcher(str).matches();
    }

    public static boolean isPhoneOrMail(String str) {
        return isMobile(str) || isMail(str);
    }

    /**
     * 负数转0
     *
     * @param decimal 数字
     * @return 负数转0
     */
    public static BigDecimal negativeToZero(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        return NumberUtil.isLess(decimal, BigDecimal.ZERO) ? BigDecimal.ZERO : decimal;
    }

    /**
     * 从字符串中提取第一个数字
     *
     * @param str 字符串
     * @return 数字
     */
    public static String getNumberGroup0(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        return ReUtil.getGroup0(PudgeUtil.NUMBER_PATTERN, str);
    }

    /**
     * 布尔值转中文是/否
     *
     * @param b 布尔值
     * @return 是/否
     */
    public static String boolToChinese(Boolean b) {
        return BooleanUtil.isTrue(b) ? "是" : "否";
    }

    /**
     * 中文是/否转布尔值
     *
     * @param b 字符串 是/否
     * @return boolean
     */
    public static boolean chineseToBool(String b) {
        return "是".equals(b);
    }

    /**
     * 合并电话和手机
     *
     * @param tel   电话
     * @param phone 手机
     * @return 电话/手机
     */
    public static String unionWithSlash(String tel, String phone) {
        String result = "";
        if (StrUtil.isNotBlank(tel)) {
            result = tel;
        }
        if (StrUtil.isNotBlank(phone)) {
            result = StrUtil.isBlank(tel) ? phone : result + StrUtil.SLASH + phone;
        }
        return result;
    }

    public static Long b2l(Boolean b) {
        return b == null ? 0L : (b ? 1L : 0L);
    }

    public static Boolean s2b(String b) {
        if (StrUtil.isBlank(b)) {
            return null;
        }
        if ("1".equals(b)) {
            return true;
        } else if ("0".equals(b)) {
            return false;
        }
        return BooleanUtil.toBoolean(b);
    }

    public static boolean isStringHaveChange(String after, String before) {
        if (StrUtil.isBlank(before)) {
            return StrUtil.isNotBlank(after);
        }
        if (StrUtil.isNotBlank(after)) {
            return !StrUtil.trimToEmpty(after).equals(StrUtil.trimToEmpty(before));
        } else {
            return false;
        }
    }

    public static boolean isBooleanHaveChange(Boolean after, Boolean before) {
        if (before == null) {
            return after != null;
        }
        if (after != null) {
            return !after.equals(before);
        } else {
            return false;
        }
    }

    public static boolean isBigDecimalHaveChange(BigDecimal after, BigDecimal before) {
        BigDecimal a = NumberUtil.nullToZero(after);
        BigDecimal b = NumberUtil.nullToZero(before);
        return !NumberUtil.equals(a, b);
    }

    public static boolean isIntHaveChange(Integer after, Integer before) {
        if (before == null) {
            return after != null;
        }
        if (after != null) {
            return !NumberUtil.equals(after, before);
        } else {
            return false;
        }
    }

    public static boolean isLocalDateTimeHaveChange(LocalDateTime after, LocalDateTime before) {
        if (before == null) {
            return after != null;
        }
        if (after != null) {
            return !after.isEqual(before);
        } else {
            return false;
        }
    }

    public static boolean isLocalDateHaveChange(LocalDate after, LocalDate before) {
        if (before == null) {
            return after != null;
        }
        if (after != null) {
            return !after.isEqual(before);
        } else {
            return false;
        }
    }

    public static boolean isStringHaveChangePro(String after, String before) {
        if (StrUtil.isBlank(before)) {
            return StrUtil.isNotBlank(after);
        }
        if (StrUtil.isNotBlank(after)) {
            //去前后空格比较
            String aPro = StrUtil.trimToEmpty(after);
            String bPro = StrUtil.trimToEmpty(before);
            if (aPro.equals(bPro)) {
                return false;
            }
            //全转半角
            aPro = PudgeUtil.full2Half(aPro);
            bPro = PudgeUtil.full2Half(bPro);
            //先转半角比较
            if (aPro.equals(bPro)) {
                return false;
            }
            //转繁体比较
            return !ZhConverterUtil.toTraditional(aPro).equals(ZhConverterUtil.toTraditional(bPro));
        } else {
            return false;
        }
    }

    public static boolean isStringHaveChangeProMax(String after, String before) {
        if (StrUtil.isBlank(before)) {
            return StrUtil.isNotBlank(after);
        }
        if (StrUtil.isNotBlank(after)) {
            //移除前后空格,转半角,简体,小写,只保留英文,数字,汉字
            String aPro = transferStr(after);
            String bPro = transferStr(before);
            return !aPro.equals(bPro);
        } else {
            return false;
        }
    }

    /**
     * 移除前后空格转半角转简体, 移除所有非中文,英文,数字, 指定字符,转小写
     *
     * @param str 原始字符串
     * @return 移除后字符串
     */
    public static String transferStr(String str) {
        return ReUtil.delAll(STR_CHANGE_PRO_PATTERN, ZhConverterUtil.toSimple(PudgeUtil.full2Half(StrUtil.trimToEmpty(str))).toLowerCase());
    }

    public static <T, R> T boolGet(boolean bool, Supplier<T> trueSupplier, R r, Function<R, T> falseFun) {
        return bool ? trueSupplier.get() : falseFun.apply(r);
    }

    public static String toSimple(String s) {
        return StrUtil.isNotBlank(s) ? ZhConverterUtil.toSimple(s) : "";
    }

    public static boolean isAddressHaveChangePro(String a, String b) {
        if (!isStringHaveChange(a, b)) {
            return false;
        }
        return isStringHaveChangePro(StrUtil.removeAll(a, ' ', '\n', '\r'), StrUtil.removeAll(b, ' ', '\n', '\r'));
    }

    /**
     * 往E链的明细品牌后面添加牌字
     *
     * @param brand E链明细品牌
     * @return brand牌
     */
    public static String addPaiToEscmBrand(String brand) {
        if (StrUtil.isNotBlank(brand)) {
            if (PudgeUtil.PAI.equals(String.valueOf(brand.charAt(brand.length() - 1)))) {
                return brand;
            }
            return brand + PudgeUtil.PAI;
        }
        return brand;
    }


    public static boolean gtZero(BigDecimal number) {
        return NumberUtil.isGreater(NumberUtil.nullToZero(number), BigDecimal.ZERO);
    }

    public static boolean gtZero(Integer number) {
        int x = number == null ? 0 : number;
        return x > 0;
    }

    public static int null2Zero(Integer number) {
        return number == null ? 0 : number;
    }

    public static BigDecimal addToPercent(int scale, BigDecimal... decimals) {
        return NumberUtil.div(NumberUtil.add(decimals), HUNDRED, scale, RoundingMode.HALF_UP);
    }

    /**
     * 合并集合
     *
     * @param c1     集合
     * @param c2     开合
     * @param mapper 去重function
     * @param <T>    原始类型
     * @param <R>    去重属性类型
     * @return 去重后的集合
     */
    public static <T, R> List<T> unionDistinct(List<T> c1, List<T> c2, Function<? super T, ? extends R> mapper) {
        if (CollUtil.isEmpty(c1)) {
            return c2;
        }
        if (CollUtil.isEmpty(c2)) {
            return c1;
        }
        return CollUtil.unionAll(c1, c2).stream().filter(distinctByKey(mapper)).collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static String buildForInSql(List<String> objectList) {
        if (CollUtil.isNotEmpty(objectList)) {
            StrBuilder strBuilder = StrBuilder.create();
            objectList.forEach(s -> strBuilder.append("'").append(s).append("',"));
            String s = strBuilder.toString();
            return StrUtil.removeSuffix(s, ",");
        }
        return null;
    }

    /**
     * 小于等于0.01的数字转为0
     *
     * @param number 原始值
     * @param scale  保留小数位
     * @return 数字
     */
    public static BigDecimal le0_01To0(BigDecimal number, int scale) {
        if (number == null) {
            return null;
        }
        if (NumberUtil.isLessOrEqual(NumberUtil.round(number.abs(), 2), DOT_01)) {
            return BigDecimal.ZERO;
        }
        return NumberUtil.round(number, scale);
    }

    /**
     * 获取MinIO文件网络路径
     *
     * @param bucket   桶名
     * @param filePath 存储路径
     * @return 网络路径
     */
    public static String getFileUrl(String bucket, String filePath) {
        return StrBuilder.create(HFY_DOMAIN_URL, "/files/", StrUtil.blankToDefault(bucket, "hfy-cloud"), filePath).toString();
    }

    public static String getMinIoFileBase64(String bucket, String filePath) {
        URL url = UrlBuilder.of(StrBuilder.create(MINIO_INNER_DOMAIN_URL, bucket, filePath).toString(), Charset.defaultCharset()).toURL();
        try {
            log.info("获取文件[{}][{}]base64内容", bucket, filePath);
            return Base64.encode(FileUtil.readString(url, Charset.defaultCharset()));
        } catch (IORuntimeException e) {
            log.info("获取[{}]文件转base64异常[{}]", url.toString(), e.getLocalizedMessage());
            throw new SystemCustomException("文件系统异常,稍后重试");
        }
    }

    public static String blankToSpace(String str) {
        return StrUtil.blankToDefault(str, StrUtil.SPACE);
    }

    public static String nullToSpace(BigDecimal number) {
        if (number == null) {
            return StrUtil.SPACE;
        }
        return NumberUtil.toStr(number);
    }

    /**
     * 根据请求获取用户ip 取第一个非unknown的ip,穿透代理
     *
     * @param request 请求
     */
    public static String getIp(HttpServletRequest request) {
        String ip = null;
        for (String head : HEADERS_TO_TRY) {
            ip = request.getHeader(head);
            if (StrUtil.isNotEmpty(ip) && !UN_KNOWN.equalsIgnoreCase(ip)) {
                break;
            }
        }
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        //ip可能形如 117.1.1.1,192.168.0.01, 取第一个
        if (StrUtil.isBlank(ip) && ip.contains(StrUtil.COMMA)) {
            ip = ip.split(StrUtil.COMMA)[0];
        }
        //本机IP
        if (LOCALHOST_IP_LIST.contains(ip)) {
            //获取真正的本机内网IP
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
        }
        return ip;
    }

    /**
     * 将字符串中连续2个及以上的空格变为1个, 海关型号要求
     *
     * @param str 字符串
     * @return 转化后结果
     */
    public static String over2SpaceTo1(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        return str.replaceAll(" {2,}", " ");
    }

    public static String null2EmptyWithTrimNew(Object s) {
        return s != null && !"NULL".equalsIgnoreCase(s.toString()) ? s.toString().trim() : "";
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    public static String sqlLike(String str) {
        return StrUtil.concat(true, "%", str, "%");
    }

    public static String sqlStartWith(String str) {
        return StrUtil.concat(true, str, "%");
    }

    public static String sqlEndWith(String str) {
        return StrUtil.concat(true, "%", str);
    }

    public static String removeAllSpecialChar(String str) {
        if (StrUtil.isBlank(str)) {
            return str;
        }
        return str.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]", "");
    }

    /**
     * 获取指定文本中所有匹配正则字段,转为json对象
     *
     * @param pattern 子模式别名正则,形如 \d+(?<time>\d{4}-\d{2}-\d{2} \d{2}:\d{2})(?<invoiceNo>\d{20})
     * @param text    待匹配文本
     * @return 结果集
     */
    public static List<JSONObject> getAllGroups(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        List<JSONObject> result = new ArrayList<>();
        // 通过反射获取 namedGroups 方法
        final Map<String, Integer> map = ReflectUtil.invoke(pattern, "namedGroups");
        //正则没写好会导致死循环,添加最大匹配限制
        int limit = 1993;
        while (matcher.find() && limit > 0) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Integer> pe : map.entrySet()) {
                jsonObject.put(pe.getKey(), matcher.group(pe.getValue()));
            }
            result.add(jsonObject);
            limit--;
        }
        return result;
    }

    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return null;
        }
        return sra.getRequest();
    }
}
