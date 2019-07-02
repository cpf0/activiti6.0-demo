package org.activiti.demo.business.dto;

import lombok.Data;

@Data
public class TaskApproveDTO {

    private String taskId; //任务Id

    private Integer approve; //审核结果

    private String approveMessage;//审核意见
}
