package com.moese.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moese.file.entity.Doc;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zxc
 * @since 2018-06-22
 */
public interface DocMapper extends BaseMapper<Doc> {

    List<Doc> findDoc(Page page, @Param("docName")String docName, @Param("docType")String docType, @Param("docUserId")Integer docUserId);

    Map queryDocTypeNumber(@Param("docUserId") Integer docUserId);
}
