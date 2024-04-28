package org.group1418.easy.escm.generate.support;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.obj.BaseFo;
import org.group1418.easy.escm.common.base.obj.BaseQo;
import org.group1418.easy.escm.common.base.obj.BaseTo;
import org.group1418.easy.escm.common.base.obj.BaseVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * easy-escm 额外配置
 *
 * @author yq 2024年4月28日 10:48:51
 */
@Getter
public class EasyEscmGeneratorConfig {

    /**
     * 是否生成Vo view object
     */
    private Boolean vo = true;
    /**
     * 是否生成Qo query object
     */
    private Boolean qo = true;
    /**
     * 是否生成Fo form object
     */
    private Boolean fo = true;
    /**
     * 是否生成To  table object
     */
    private Boolean to = true;

    /**
     * 实体对应 service 实现类 属性名称, 形如 entityService, 用于自动注入
     */
    private String serviceImplFieldName;

    /**
     * 需要导入的包
     */
    private final Set<String> controllerPackages = new TreeSet<>();
    private final Set<String> controllerAnnotations = new TreeSet<>();
    private final Set<String> servicePackages = new TreeSet<>();
    private final Set<String> serviceAnnotations = new TreeSet<>();
    private final Set<String> serviceImplPackages = new TreeSet<>();
    private final Set<String> serviceImplAnnotations = new TreeSet<>();
    private final Set<String> mapperPackages = new TreeSet<>();
    private final Set<String> mapperAnnotations = new TreeSet<>();

    private String pojoDirName = "pojo";
    private String pojoPackage;
    private String superFoName;
    private String superFoClassPackage;
    private String superQoName;
    private String superQoClassPackage;
    private String superToName;
    private String superToClassPackage;
    private String superVoName;
    private String superVoClassPackage;
    private Set<String> pojoPackages = new TreeSet<>();

    @SuppressWarnings("unchecked")
    public void init(TableInfo tableInfo, Map<String, Object> otherMap) {
        JSONObject otherJson = new JSONObject(otherMap);
        JSONObject packageMap = otherJson.getJSONObject("package");
        initPojo(tableInfo,otherJson,packageMap);
        initController(tableInfo,otherJson,packageMap);
    }

    private void initPojo(TableInfo tableInfo, JSONObject otherJson,  JSONObject packageMap){
        pojoPackage = packageMap.getString(ConstVal.PARENT) + "." + pojoDirName;
        superFoName = BaseFo.class.getSimpleName();
        superFoClassPackage = BaseFo.class.getName();

        superQoName = BaseQo.class.getSimpleName();
        superQoClassPackage = BaseQo.class.getName();

        superToName = BaseTo.class.getSimpleName();
        superToClassPackage = BaseTo.class.getName();

        superVoName = BaseVo.class.getSimpleName();
        superVoClassPackage = BaseVo.class.getName();
        pojoPackages = tableInfo.getImportPackages().stream().filter(s -> s.startsWith("java") && !"java.io.Serializable".equals(s)).collect(Collectors.toSet());
    }

    private void initController(TableInfo tableInfo, JSONObject otherJson,  JSONObject packageMap){
        controllerPackages.add(RequestMapping.class.getName());
        controllerAnnotations.add(StrUtil.format("RequestMapping(\"/{}\")",
                BooleanUtil.isTrue(otherJson.getBoolean("controllerMappingHyphenStyle")) ?
                        otherJson.getString("controllerMappingHyphen") : tableInfo.getEntityPath()));
        if (BooleanUtil.isTrue(otherJson.getBoolean("restControllerStyle"))) {
            controllerAddPackageAndAnnotation(RestController.class);
        } else {
            controllerAddPackageAndAnnotation(Controller.class);
        }
        String superControllerClassPackage = otherJson.getString("superControllerClassPackage");
        if (StrUtil.isNotBlank(superControllerClassPackage)) {
            controllerPackages.add(superControllerClassPackage);
        }
        controllerAddPackageAndAnnotation(RequiredArgsConstructor.class);
        controllerPackages.add(StrUtil.format("{}.{}", packageMap.get(ConstVal.SERVICE), tableInfo.getServiceName()));
        this.serviceImplFieldName = StrUtil.removeSuffix(StrUtil.lowerFirst(tableInfo.getServiceImplName()), "Impl");
    }

    public void otherInject(InjectionConfig.Builder injectionConfigBuilder){
        if (BooleanUtil.isTrue(fo)) {
            injectionConfigBuilder.customFile(buildCustomFile("fo"));
        }
        if (BooleanUtil.isTrue(to)) {
            injectionConfigBuilder.customFile(buildCustomFile("to"));
        }
        if (BooleanUtil.isTrue(qo)) {
            injectionConfigBuilder.customFile(buildCustomFile("qo"));
        }
        if (BooleanUtil.isTrue(vo)) {
            injectionConfigBuilder.customFile(buildCustomFile("vo"));
        }
    }

    private CustomFile buildCustomFile(String secondPack) {
        return new CustomFile.Builder().templatePath(StrUtil.format("/templates/{}.java.ftl",secondPack)).packageName(pojoDirName + "." + secondPack)
                .formatNameFunction(ti -> ti.getEntityName() + StrUtil.upperFirst(secondPack)).fileName(".java").build();
    }

    private void controllerAddPackageAndAnnotation(Class<?> clazz) {
        controllerPackages.add(clazz.getName());
        controllerAnnotations.add(clazz.getSimpleName());
    }

    public void setPojoDirName(String pojoDirName) {
        this.pojoDirName = pojoDirName;
    }

    public EasyEscmGeneratorConfig setVo(Boolean vo) {
        this.vo = vo;
        return this;
    }

    public EasyEscmGeneratorConfig setQo(Boolean qo) {
        this.qo = qo;
        return this;
    }

    public EasyEscmGeneratorConfig setFo(Boolean fo) {
        this.fo = fo;
        return this;
    }

    public EasyEscmGeneratorConfig setTo(Boolean to) {
        this.to = to;
        return this;
    }

}
