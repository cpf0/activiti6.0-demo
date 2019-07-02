package org.activiti.demo.business;

import lombok.extern.slf4j.Slf4j;
import org.activiti.demo.business.constant.GeneralMeetingRoomApproveConstants;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/business")
public class BusinessController {
    /**
     * 启动流程
     *
     * @return
     */
    @RequestMapping("/startTask")
    public ResponseEntity<String> startTask() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        //设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put(GeneralMeetingRoomApproveConstants.approveUser, "小毛");//设置申请人
        variables.put(GeneralMeetingRoomApproveConstants.firstApprove, true);//首次提交
        ProcessInstance process1 = runtimeService.startProcessInstanceByKey(GeneralMeetingRoomApproveConstants.processDefinitionKey, "meetingId", variables);
        log.info("processId:{}", process1.getId());


        return ResponseEntity.ok("流程已启动");
    }


    /**
     * 用户任务列表
     *
     * @return
     */
    @RequestMapping("/taskList")
    public ResponseEntity<List<String>> taskList() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list();
        log.info("----------查询用户任务列表-----------");
        tasks.forEach(task -> {
            log.info("任务Id:{}", task.getId());
            log.info("任务名称：{}", task.getName());
            log.info("任务处理人:{}", task.getAssignee());
            log.info("-------------------------------");

        });
        log.info("----------用户任务列表结束-----------");
        List<String> collect = tasks.stream().map(TaskInfo::getId).collect(Collectors.toList());

        return ResponseEntity.ok(collect);

    }


    /**
     * 秘书完成审核
     *
     * @param taskId 任务Id
     * @return
     */
    @RequestMapping("/secretary/{taskId}/finishApprove")
    public ResponseEntity<String> secretary(@PathVariable("taskId") String taskId) {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        boolean secretaryApprove = true;
        Map<String, Object> variables = new HashMap<>();
        variables.put(GeneralMeetingRoomApproveConstants.firstApprove, false);

        if (secretaryApprove) {
            variables.put(GeneralMeetingRoomApproveConstants.secretaryAgreement,"秘书同意");
            //将任务提交给经理
            variables.put("secretaryApprove", MeetingApproveStatusEnum.AGREE.getCode());
            variables.put("roleManager","张经理");

        } else {
            variables.put("secretaryApprove", MeetingApproveStatusEnum.AGREE.getCode());
            variables.put(GeneralMeetingRoomApproveConstants.secretaryAgreement,"秘书驳回，驳回理由：有其他人预定");
        }

        taskService.complete(taskId, variables); //秘书提交处理
        String approveMessage = secretaryApprove ? "秘书审核同意，任务分配给部门经理审核" : "秘书不同意审核,提交被退回";
        return ResponseEntity.ok(approveMessage);
    }

    /**
     * 经理完成审核
     * @param taskId
     * @return
     */
    @RequestMapping("/manager/{taskId}/finishApprove")
    public ResponseEntity<String> handleTask(@PathVariable("taskId") String taskId) {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        Map<String,Object> variables = new HashMap<>();
        MeetingApproveStatusEnum managerApprove = MeetingApproveStatusEnum.AGREE; //1 同意，2，拒绝，3 回退。
        String managerAgreement ="";

        //同意
        if(managerApprove.equals(MeetingApproveStatusEnum.AGREE)){
            variables.put(GeneralMeetingRoomApproveConstants.managerApprove,MeetingApproveStatusEnum.AGREE.getCode());
            if(StringUtils.isNotEmpty(managerAgreement)){
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,managerAgreement);
            }else{
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,MeetingApproveStatusEnum.AGREE.getDefaultApproveMessage());
            }
         //拒绝
        }else if(managerApprove.equals(MeetingApproveStatusEnum.Reject)){
            variables.put(GeneralMeetingRoomApproveConstants.managerApprove,MeetingApproveStatusEnum.Reject.getCode());
            if(StringUtils.isNotEmpty(managerAgreement)){
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,managerAgreement);
            }else{
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,MeetingApproveStatusEnum.Reject.getDefaultApproveMessage());
            }
        //回退
        }else{
            variables.put(GeneralMeetingRoomApproveConstants.managerApprove,MeetingApproveStatusEnum.FallBack.getCode());
            if(StringUtils.isNotEmpty(managerAgreement)){
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,managerAgreement);
            }else{
                variables.put(GeneralMeetingRoomApproveConstants.managerAgreement,MeetingApproveStatusEnum.FallBack.getDefaultApproveMessage());
            }
        }
        taskService.complete(taskId,variables);

        return ResponseEntity.ok(MeetingApproveStatusEnum.AGREE.getDefaultApproveMessage());
    }


    /**
     * 删除一个流程实例
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping("/process/delete/{processInstanceId}")
    public ResponseEntity<String> deleteProcess(@PathVariable("processInstanceId") String processInstanceId) {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        //删除当前正在运行的
//        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
//        runtimeService.deleteProcessInstance(processInstanceId, "重新开始测试");
       // 删除运行历史
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        historyService.deleteHistoricProcessInstance(processInstanceId);
        return ResponseEntity.ok("删除流程Id" + processInstanceId);
    }

    /**
     * 查询流程处理进度
     * @return
     */
    @RequestMapping("/history/query")
    public  ResponseEntity<String> queryUserHistoricTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> list = historicActivityInstanceQuery.processInstanceId("7f448a1e0677499b8d2fe30636abf816").activityType("userTask").orderByHistoricActivityInstanceStartTime().asc().list();

        for (HistoricActivityInstance hai:list){
            System.out.println("活动ID:"+hai.getId());
            System.out.println("流程实例ID:"+hai.getProcessInstanceId());
            System.out.println("活动名称："+hai.getActivityName());
            System.out.println("活动类型："+hai.getActivityType());
            System.out.println("办理人："+hai.getAssignee());
            System.out.println("开始时间："+hai.getStartTime());
            System.out.println("结束时间："+hai.getEndTime());
            System.out.println("================================= ");
        }
        return ResponseEntity.ok("获取历史流程");
    }


}
