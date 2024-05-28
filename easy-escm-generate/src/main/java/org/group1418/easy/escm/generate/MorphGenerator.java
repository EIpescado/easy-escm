package org.group1418.easy.escm.generate;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.group1418.easy.escm.common.base.BaseController;
import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.base.obj.BaseEntity;
import org.group1418.easy.escm.generate.support.EasyEscmGeneratorConfig;

import java.io.File;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author yq
 * @date 2019/07/10 16:19
 * @description
 * @since V1.0.0
 */
@Data
@Builder
public class MorphGenerator {

    private String author;
    private String url;
    private String username;
    private String password;
    private DataSourceConfig dataSourceConfig;
    private GlobalConfig globalConfig;
    private PackageConfig packageConfig;
    private InjectionConfig injectionConfig;
    private StrategyConfig strategyConfig;

    private void buildGlobalConfig(String moduleName) {
        //全局配置作者,文件输出路径
        String projectPath = System.getProperty("user.dir");
        String javaPath = projectPath + File.separator + moduleName + "/src/main/java";
        globalConfig = new GlobalConfig.Builder().outputDir(javaPath)
                .author(author)
                .dateType(DateType.TIME_PACK)
                .build();
    }

    private void buildPackageConfig(String parent, String moduleName, String moduleAlias) {
        String projectPath = System.getProperty("user.dir");
        String resourcesPath = projectPath + File.separator + moduleName + "/src/main/resources";
        //包配置
        packageConfig = new PackageConfig.Builder().parent(parent)
                .moduleName(moduleAlias)
                .entity("entity")
                .service("service")
                .serviceImpl("service.impl")
                .mapper("mapper")
                .controller("controller")
                .pathInfo(Collections.singletonMap(OutputFile.xml, resourcesPath + File.separator + moduleAlias))
                .build();
    }

    private void buildInjectionConfig(Consumer<EasyEscmGeneratorConfig> configConsumer) {
        EasyEscmGeneratorConfig config = new EasyEscmGeneratorConfig();
        configConsumer.accept(config);
        InjectionConfig.Builder injectionConfigBuilder = new InjectionConfig.Builder();
        config.otherInject(injectionConfigBuilder);
        injectionConfigBuilder.beforeOutputFile((tableInfo, objectMap) -> {
            config.init(tableInfo, objectMap);
            //额外模版参数
            objectMap.put("easyEscmConfig", config);
        });
        injectionConfig = injectionConfigBuilder.build();
    }


    private void buildStrategyConfig(String... tables) {
        StrategyConfig.Builder strategyConfigBuilder = new StrategyConfig.Builder();
        strategyConfigBuilder.addInclude(tables);
        //entity 策略配置
        strategyConfigBuilder.entityBuilder()
                .superClass(BaseEntity.class)
                .enableLombok()
                .enableRemoveIsPrefix()
                .enableTableFieldAnnotation()
                .versionColumnName("version")
                .logicDeleteColumnName("enabled")
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .addSuperEntityColumns("id", "enabled", "create_time", "update_time",
                        "version", "create_user_id", "create_user", "update_user_id", "update_user")
                .addTableFills(
                        new Column("enabled", FieldFill.INSERT),
                        new Column("create_time", FieldFill.INSERT),
                        new Column("create_user_id", FieldFill.INSERT),
                        new Column("create_user", FieldFill.INSERT),
                        new Column("updated_time", FieldFill.INSERT_UPDATE),
                        new Column("update_user_id", FieldFill.INSERT_UPDATE),
                        new Column("update_user", FieldFill.INSERT_UPDATE)
                )
                .idType(IdType.ASSIGN_ID);
        //Controller 策略配置
        strategyConfigBuilder.controllerBuilder()
                .superClass(BaseController.class)
                .enableRestStyle()
                .formatFileName("%sController");
        //service 策略配置
        strategyConfigBuilder.serviceBuilder()
                .superServiceClass(BaseService.class)
                .superServiceImplClass(BaseServiceImpl.class)
                .formatServiceFileName("I%sService")
                .formatServiceImplFileName("%sServiceImpl");
        //mapper 策略配置
        strategyConfigBuilder.mapperBuilder()
                .superClass(CommonMapper.class)
                .formatMapperFileName("%sMapper")
                .formatXmlFileName("%sMapper")
                .mapperAnnotation(Mapper.class)
                .build();
        strategyConfig = strategyConfigBuilder.build();
    }

    /**
     * 执行生成代码
     *
     * @param parent      父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
     * @param moduleName  模块名称
     * @param moduleAlias 模块别名,用于生成子目录
     * @param consumer    easy-escm额外配置
     */
    public void execute(String parent, String moduleName, String moduleAlias,
                        Consumer<EasyEscmGeneratorConfig> consumer,
                        String... tables) {
        dataSourceConfig = new DataSourceConfig.Builder(url, username, password).build();

        //全局配置作者,文件输出路径
        buildGlobalConfig(moduleName);
        //包配置
        buildPackageConfig(parent, moduleName, moduleAlias);
        //注入配置
        buildInjectionConfig(consumer);
        //策略配置
        buildStrategyConfig(tables);
        new AutoGenerator(dataSourceConfig)
                // 全局配置
                .global(globalConfig)
                // 包配置
                .packageInfo(packageConfig)
                // 策略配置
                .strategy(strategyConfig)
                // 注入配置
                .injection(injectionConfig)
                // 执行
                .execute(new FreemarkerTemplateEngine());
    }


    public static void main(String[] args) {
        MorphGenerator generator = MorphGenerator.builder().username("")
                .password("")
                .url("jdbc:mysql://localhost:3306/easy_escm?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&allowMultiQueries=true")
                .author("yq")
                .build();
        generator.execute("org.group1418.easy.escm",
                "easy-escm-generate",
                "generate",
                config -> config.setFo(true).setVo(true).addLikeField("username","password"),
                "system_test");
    }
}
