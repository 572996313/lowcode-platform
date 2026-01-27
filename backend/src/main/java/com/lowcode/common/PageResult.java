package com.lowcode.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页条数
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 从 MyBatis-Plus 分页对象转换
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return PageResult.<T>builder()
                .total(page.getTotal())
                .records(page.getRecords())
                .current(page.getCurrent())
                .size(page.getSize())
                .pages(page.getPages())
                .build();
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        long pages = total % size == 0 ? total / size : total / size + 1;
        return PageResult.<T>builder()
                .total(total)
                .records(records)
                .current(current)
                .size(size)
                .pages(pages)
                .build();
    }
}
