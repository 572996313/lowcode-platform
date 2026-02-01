package com.lowcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.SysDictItem;

import java.util.List;

/**
 * 字典明细服务接口
 *
 * @author lowcode
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    /**
     * 获取分类下的字典树
     *
     * @param categoryId 分类ID
     * @return 字典树
     */
    List<SysDictItem> getItemTree(Long categoryId);

    /**
     * 分页查询字典明细
     *
     * @param categoryId 分类ID
     * @param parentId 父项ID，null表示查询所有
     * @param page 分页参数
     * @return 分页结果
     */
    PageResult<SysDictItem> getItemPage(Long categoryId, Long parentId, Page<SysDictItem> page);

    /**
     * 创建字典明细
     *
     * @param item 字典明细
     * @return 明细ID
     */
    Long createItem(SysDictItem item);

    /**
     * 更新字典明细
     *
     * @param id 明细ID
     * @param item 字典明细
     */
    void updateItem(Long id, SysDictItem item);

    /**
     * 删除字典明细
     *
     * @param id 明细ID
     */
    void deleteItem(Long id);

    /**
     * 批量删除字典明细
     *
     * @param ids 明细ID列表
     */
    void batchDeleteItems(List<Long> ids);

    /**
     * 根据分类编码获取字典数据（给控件调用）
     *
     * @param categoryCode 分类编码
     * @return 字典项列表
     */
    List<SysDictItem> getItemsByCategoryCode(String categoryCode);
}
