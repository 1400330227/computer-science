package com.computerscience.hdfsapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.computerscience.hdfsapi.dto.CorpusWithComputedCapacity;
import com.computerscience.hdfsapi.model.Corpus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Corpus> {

    @Select("<script>" +
            "SELECT c.*, (SELECT SUM(f.size) FROM files f WHERE f.corpus_id = c.corpus_id) / (1024*1024*1024) AS computedCapacityGb " +
            "FROM corpus c " +
            "LEFT JOIN users u ON c.creator_id = u.user_id " +
            "<where>" +
            "<if test='collectionName != null and collectionName != \"\"'>" +
            "AND c.collection_name LIKE CONCAT('%', #{collectionName}, '%')" +
            "</if>" +
            "<if test='creatorAccount != null and creatorAccount != \"\"'>" +
            "AND u.account LIKE CONCAT('%', #{creatorAccount}, '%')" +
            "</if>" +
            "</where>" +
            "ORDER BY c.created_at DESC" +
            "</script>")
    Page<CorpusWithComputedCapacity> findCorpusWithComputedCapacity(Page<?> page, @Param("collectionName") String collectionName, @Param("creatorAccount") String creatorAccount);
} 