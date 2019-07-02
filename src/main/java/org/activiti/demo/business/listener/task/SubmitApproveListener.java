package org.activiti.demo.business.listener.task;

import lombok.extern.slf4j.Slf4j;
import org.activiti.demo.business.constant.GeneralMeetingRoomApproveConstants;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 提交任务给秘书
 */
@Slf4j
public class SubmitApproveListener implements TaskListener{
    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("------------------提交会议任务监听器-------------");
        log.info("任务Id：{}",delegateTask.getId());
        log.info("任务名称：{}",delegateTask.getName());
        log.info("任务创建时间：{}",delegateTask.getCreateTime());
        log.info("任务处理人{}:",delegateTask.getAssignee());
        log.info("流程实例Id{}",delegateTask.getProcessInstanceId());

        log.info("---------------提交会议任务监听器结束----------------");
        Boolean fistApprove = (Boolean) delegateTask.getVariable(GeneralMeetingRoomApproveConstants.firstApprove);
        if(fistApprove){
            log.info("--------------首次申请,开始自动提交任务----------");
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            TaskService taskService = defaultProcessEngine.getTaskService();
            Map<String,Object> variables= new HashMap<>();
            variables.put("roleSecretary","秘书小琴");
            taskService.complete(delegateTask.getId(),variables);
            log.info("--------------首次申请，自动提交结束----------");

        }




    }
}
