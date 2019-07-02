package org.activiti.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
public class DemoApplicationTests {

	/**
	 * 部署流程
	 */
	@Test
	public void contextLoads() {

		ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();

		RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();

		repositoryService.createDeployment().name("会议审批流程").addClasspathResource("GeneralMeetingRoomApprove.bpmn")
				.addClasspathResource("GeneralMeetingRoomApprove.png").deploy();
	}


}
