package com.petboarding.modules.record.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.modules.record.entity.DailyRecord;
import com.petboarding.modules.record.mapper.DailyRecordMapper;
import com.petboarding.modules.record.service.DailyRecordService;
import org.springframework.stereotype.Service;

@Service
public class DailyRecordServiceImpl extends ServiceImpl<DailyRecordMapper, DailyRecord> implements DailyRecordService {
}
