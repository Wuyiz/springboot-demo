package com.example.common.util;

/**
 * 分页工具类
 *
 * @author wuyizhang
 * @Date 2023-04-20
 */
public class PageUtils {
    // 首页页码
    private static final int HOME_PAGE_NUM = 1;

    /**
     * 根据当前页码和页大小计算起始偏移量（默认第一页作为首页页码）
     *
     * @param pageNum  当前页码
     * @param pageSize 页大小
     * @return 起始查询的偏移量
     */
    public static int calcStartOffset(int pageNum, int pageSize) {
        return calcStartOffset(HOME_PAGE_NUM, pageNum, pageSize);
    }

    /**
     * 根据当前页码和页大小计算起始偏移量
     *
     * @param homePageNum 首页页码
     * @param pageNum     当前页码
     * @param pageSize    页大小
     * @return 起始查询的偏移量
     */
    public static int calcStartOffset(int homePageNum, int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, homePageNum);
        pageSize = Math.max(pageSize, 0);
        return (pageNum - homePageNum) * pageSize;
    }
}
