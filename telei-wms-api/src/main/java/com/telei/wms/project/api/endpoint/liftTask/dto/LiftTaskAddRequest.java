package com.telei.wms.project.api.endpoint.liftTask.dto;

import com.telei.wms.datasource.wms.vo.LiftTaskPageQueryResponseVo;
import lombok.Data;

import java.util.List;

/**
 * wms_lift_work 升降任务
 */
@Data
public class LiftTaskAddRequest {

    public List<LiftTaskPageQueryResponseVo> addLiftWorkList;

}