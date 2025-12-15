package com.computerscience.hdfsapi.service;

import java.util.List;
import java.util.Map;

public interface DataViewService {

    /**
     * 获取语料库统计总览
     */
    Map<String, Object> getCorpusOverview();

    /**
     * 获取国家/地区分布数据
     */
    List<Map<String, Object>> getCountryDistribution();

    /**
     * 获取语言分布数据
     */
    List<Map<String, Object>> getLanguageDistribution();

    /**
     * 获取上传者贡献分析数据
     */
    List<Map<String, Object>> getContributorAnalysis();

    /**
     * 获取时间序列分析数据
     */
    List<Map<String, Object>> getTimeSeriesAnalysis();

    /**
     * 获取主题领域分析数据
     */
    List<Map<String, Object>> getThematicAnalysis();

    /**
     * 按条件筛选语料数据
     */
    List<Map<String, Object>> filterCorpusData(Map<String, Object> filterParams);

    /**
     * 获取热门关键词
     */
    List<Map<String, Object>> getTopKeywords(Integer limit);
}