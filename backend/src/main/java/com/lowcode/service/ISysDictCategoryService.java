package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.entity.SysDictCategory;

import java.util.List;

/**
 * 字典分类服务接口
 *
 * @author lowcode
 */
public interface ISysDictCategoryService extends IService<SysDictCategory> {

    /**
     * 获取字典分类树
     *
     * @return 分类树
     */
    List<SysDictCategory> getCategoryTree();

    /**
     * 创建字典分类
     *
     * @param category 分类信息
     * @return 分类ID
     */
    Long createCategory(SysDictCategory category);

    /**
     * 更新字典分类
     *
     * @param id 分类ID
     * @param category 分类信息
     */
    void updateCategory(Long id, SysDictCategory category);

    /**
     * 删除字典分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 根据分类编码获取分类
     *
     * @param categoryCode 分类编码
     * @return 分类信息
     */
    SysDictCategory getByCategoryCode(String categoryCode);
}
