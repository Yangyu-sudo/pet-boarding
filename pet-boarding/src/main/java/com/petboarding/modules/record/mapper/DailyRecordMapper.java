package com.petboarding.modules.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petboarding.modules.record.entity.DailyRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyRecordMapper extends BaseMapper<DailyRecord> {
}
