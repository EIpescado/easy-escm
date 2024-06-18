package org.group1418.easy.escm.common.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.group1418.easy.escm.common.base.BaseService;
import org.group1418.easy.escm.common.base.CommonMapper;
import org.group1418.easy.escm.common.base.obj.BaseEntity;

import java.util.List;
import java.util.function.Consumer;


/**
 * service 基础实现
 *
 * @author yq  2018/11/15 11:39
 */
@Slf4j
public class BaseServiceImpl<M extends CommonMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

    /**
     * mybatis 使用 lambda更新时 FastjsonTypeHandler
     */
    protected final String FAST_JSON_TYPE_HANDLER_MAPPING = "typeHandler=com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler";

    @Override
    public boolean haveFieldValueEq(SFunction<T, ?> function, Object value) {
        return baseMapper.haveFieldValueEq(function, value);
    }

    @Override
    public boolean haveMatchData(Consumer<LambdaQueryWrapper<T>> consumer) {
        return baseMapper.haveMatchData(consumer);
    }

    @Override
    public T getOneByFieldValueEq(SFunction<T, ?> function, Object value) {
        return baseMapper.getOneByFieldValueEq(function, value);
    }

    @Override
    public T getOneByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        return baseMapper.getOneByWrapper(consumer);
    }

    @Override
    public List<T> getByFieldValueEq(SFunction<T, ?> function, Object value) {
        return baseMapper.getByFieldValueEq(function, value);
    }

    @Override
    public List<T> getByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        return baseMapper.getByWrapper(consumer);
    }

    @Override
    public Integer deleteByFieldEq(SFunction<T, ?> function, Object value) {
        return baseMapper.deleteByFieldEq(function, value);
    }

    @Override
    public Integer deleteByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        return baseMapper.deleteByWrapper(consumer);
    }

    @Override
    public void updateByWrapper(Consumer<LambdaUpdateWrapper<T>> consumer) {
        baseMapper.updateByWrapper(consumer);
    }

    @Override
    public Long countByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        return baseMapper.countByWrapper(consumer);
    }

    @Override
    public <F> List<F> getFieldByWrapper(Consumer<LambdaQueryWrapper<T>> consumer, SFunction<T, F> function) {
        return baseMapper.getFieldByWrapper(consumer,function);
    }
}
