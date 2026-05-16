package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vo.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
}
