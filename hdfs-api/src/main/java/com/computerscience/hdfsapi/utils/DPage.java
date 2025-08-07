package com.computerscience.hdfsapi.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页工具类
 * @param <T>
 */
public class DPage<T> {

    private Integer pageNum;  // 当前页
    private Integer total;    // 总记录数
    private Integer pages;    // 总页数
    private List<T> list;     // 当前页数据

    @JsonIgnore
    private Integer pageSize; // 每页显示条数

    /**
     * 用于接收已经由数据库分页好的数据
     * @param list  已经分页的列表
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     */
    public DPage(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = (int) total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 用于对一个完整的List在内存中进行分页
     * @param list 未分页的完整列表
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     */
    public DPage(List<T> list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = list.size();
        this.pages = (int) Math.ceil((double) total / pageSize);

        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = pageNum * pageSize;

        if (fromIndex >= list.size()) {
            this.list = new ArrayList<>();
        } else {
            if (toIndex > list.size()) {
                toIndex = list.size();
            }
            this.list = list.subList(fromIndex, toIndex);
        }
    }

    // Getters and Setters
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
} 