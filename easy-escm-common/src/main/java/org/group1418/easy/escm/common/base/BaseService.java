package org.group1418.easy.escm.common.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import org.group1418.easy.escm.common.base.obj.BaseEntity;

import java.util.List;
import java.util.function.Consumer;

/**
 * 基础service
 *
 * @author yq
 * @date 2021年4月14日 10:43:03
 */
public interface BaseService<T extends BaseEntity> extends IService<T> {

    /**
     * 是否存在字段为指定值的数据
     *
     * @param function 实体属性function
     * @param value    值
     * @return boolean
     */
    boolean haveFieldValueEq(SFunction<T, ?> function, Object value);

    /**
     * 是否存在满足条件的数据
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return boolean
     */
    boolean haveMatchData(Consumer<LambdaQueryWrapper<T>> consumer);

    /**
     * 获取单个字段为指定值的实体
     *
     * @param function 实体属性function
     * @param value    值
     * @return T
     */
    T getOneByFieldValueEq(SFunction<T, ?> function, Object value);

    /**
     * 获取单个字段为指定值的实体
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return T
     */
    T getOneByWrapper(Consumer<LambdaQueryWrapper<T>> consumer);

    /**
     * 获取所有字段为指定值的实体
     *
     * @param function 实体属性function
     * @param value    值
     * @return T
     */
    List<T> getByFieldValueEq(SFunction<T, ?> function, Object value);

    /**
     * 获取实体
     *
     * @param consumer LambdaQueryWrapper消费者
     * @return 实体集合
     */
    List<T> getByWrapper(Consumer<LambdaQueryWrapper<T>> consumer);

    /**
     * 删除所有指定字段为指定值的数据
     *
     * @param function 实体属性function
     * @param value    值
     * @return 删除行数
     */
    Integer deleteByFieldEq(SFunction<T, ?> function, Object value);


    /**
     * 删除所有指定字段为指定值的数据
     *
     * @param consumer LambdaUpdateWrapper消费者
     * @return 删除行数
     */
    Integer deleteByWrapper(Consumer<LambdaQueryWrapper<T>> consumer);

    /**
     * 更新实体
     *
     * @param consumer LambdaUpdateWrapper消费者
     */
    void updateByWrapper(Consumer<LambdaUpdateWrapper<T>> consumer);

    /**
     * 统计指定数量
     *
     * @param consumer LambdaUpdateWrapper消费者
     * @return 数量
     */
    Long countByWrapper(Consumer<LambdaQueryWrapper<T>> consumer);

    /**
     * 根据wrapper获取指定属性
     * @param consumer LambdaQueryWrapper消费者
     * @param function 实体属性function
     * @return 实体属性集合
     * @param <F> 属性类型
     */
    <F> List<F>  getFieldByWrapper(Consumer<LambdaQueryWrapper<T>> consumer, SFunction<T, F> function);
}
