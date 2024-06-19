package org.group1418.easy.escm.common.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.group1418.easy.escm.common.config.properties.EasyEscmTenantProp;

import java.util.List;

/**
 * 自定义租户处理器,即自动为sql添加 租户条件
 *
 * @author yq 2024年6月17日 10:50:35
 */
@Slf4j
@AllArgsConstructor
public class EasyEscmTenantLineHandler implements TenantLineHandler {

    private final EasyEscmTenantProp easyEscmTenantProp;

    @Override
    public String getTenantIdColumn() {
        return easyEscmTenantProp.getTenantIdColumn();
    }

    @Override
    public Expression getTenantId() {
        String tenantId = TenantHelper.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            log.error("当前上下文无有效租户编码");
            return new NullValue();
        }
        log.info("tenantId [{}]",tenantId);
        // 返回固定租户
        return new StringValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        String tenantId = TenantHelper.getTenantId();
        boolean ignore = true;
        // 判断是否有租户
        if (StrUtil.isNotBlank(tenantId)) {
            // 不需要按租户过滤的表
            List<String> excludes = easyEscmTenantProp.getExcludes();
            // 非业务表
            List<String> tables = ListUtil.toList();
            if(CollUtil.isNotEmpty(excludes)){
                tables.addAll(excludes);
            }
            ignore = tables.contains(tableName);
        }
        log.info("ignoreTable [{}],[{}],ignore[{}]",tableName,tenantId,ignore);
        return ignore;
    }

}
