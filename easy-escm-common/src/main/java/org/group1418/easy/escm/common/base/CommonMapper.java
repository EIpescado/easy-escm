package org.group1418.easy.escm.common.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.group1418.easy.escm.common.base.obj.BaseEntity;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 通用mapper
 *
 * @author yq
 * @date 2021年4月14日 10:43:20
 */
public interface CommonMapper<T extends BaseEntity> extends BaseMapper<T> {
    /**
     * 批量插入
     */
    default boolean insertBatch(Collection<T> entityList) {
        return Db.saveBatch(entityList);
    }

    /**
     * 批量更新
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    /**
     * 批量插入或更新
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return Db.saveOrUpdateBatch(entityList);
    }

    /**
     * 批量插入(包含限制条数)
     */
    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        return Db.saveBatch(entityList, batchSize);
    }


    /**
     * 批量更新(包含限制条数)
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        return Db.updateBatchById(entityList, batchSize);
    }

    /**
     * 批量插入或更新(包含限制条数)
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        return Db.saveOrUpdateBatch(entityList, batchSize);
    }

    /**
     * 插入或更新(包含限制条数)
     */
    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

    /**
     * 是否存在字段为指定值的数据
     *
     * @param function 实体属性function
     * @param value    值
     * @return boolean
     */
    default boolean haveFieldValueEq(SFunction<T, ?> function, Object value) {
        Long count = selectCount(Wrappers.<T>lambdaQuery().eq(function, value));
        return count != null && count > 0;
    }

    /**
     * 是否存在满足条件的数据
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return boolean
     */
    default boolean haveMatchData(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumer.accept(lambdaQueryWrapper);
        Long count = selectCount(lambdaQueryWrapper);
        return count != null && count > 0;
    }

    /**
     * 获取单个字段为指定值的实体
     *
     * @param function 实体属性function
     * @param value    值
     * @return T
     */
    default T getOneByFieldValueEq(SFunction<T, ?> function, Object value) {
        return selectOne(Wrappers.<T>lambdaQuery().eq(function, value));
    }

    /**
     * 获取单个字段为指定值的实体
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return T
     */
    default T getOneByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        consumer.accept(wrapper);
        return selectOne(wrapper);
    }

    /**
     * 获取所有字段为指定值的实体
     *
     * @param function 实体属性function
     * @param value    值
     * @return T
     */
    default List<T> getByFieldValueEq(SFunction<T, ?> function, Object value) {
        return selectList(Wrappers.<T>lambdaQuery().eq(function, value));
    }

    /**
     * 获取实体
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return 实体集合
     */
    default List<T> getByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        consumer.accept(wrapper);
        return selectList(wrapper);
    }

    /**
     * 删除所有指定字段为指定值的数据
     *
     * @param function 实体属性function
     * @param value    值
     * @return 删除行数
     */
    default Integer deleteByFieldEq(SFunction<T, ?> function, Object value) {
        return delete(Wrappers.<T>lambdaQuery().eq(function, value));
    }

    /**
     * 删除所有指定字段为指定值的数据
     *
     * @param consumer LambdaUpdateWrapper消费者
     * @return 删除行数
     */
    default Integer deleteByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        consumer.accept(wrapper);
        return delete(wrapper);
    }

    /**
     * 更新实体
     *
     * @param consumer LambdaUpdateWrapper消费者
     */
    default void updateByWrapper(Consumer<LambdaUpdateWrapper<T>> consumer) {
        LambdaUpdateWrapper<T> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        consumer.accept(lambdaUpdateWrapper);
        this.update(lambdaUpdateWrapper);
    }

    /**
     * 统计指定数量
     *
     * @param consumer LambdaUpdateWrapper消费者
     * @return 数量
     */
    default Long countByWrapper(Consumer<LambdaQueryWrapper<T>> consumer) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        consumer.accept(wrapper);
        return selectCount(wrapper);
    }

    /**
     * 根据wrapper获取指定属性
     * @param consumer LambdaQueryWrapper消费者
     * @param function 实体属性function
     * @return 实体属性集合
     * @param <F> 属性类型
     */
    default <F> List<F> getFieldByWrapper(Consumer<LambdaQueryWrapper<T>> consumer, SFunction<T, F> function) {
        return getByWrapper(consumer).stream().map(function).collect(Collectors.toList());
    }

}
