package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.entity.DbTableGroup;

import java.util.List;

/**
 * 库表分组服务接口
 */
public interface IDbTableGroupService extends IService<DbTableGroup> {

    /**
     * 获取分组树
     */
    List<DbTableGroup> getGroupTree();

    /**
     * 创建分组
     */
    Long createGroup(DbTableGroup group);

    /**
     * 更新分组
     */
    void updateGroup(Long id, DbTableGroup group);

    /**
     * 删除分组（将分组下的表设为未分组）
     */
    void deleteGroup(Long id);
}
