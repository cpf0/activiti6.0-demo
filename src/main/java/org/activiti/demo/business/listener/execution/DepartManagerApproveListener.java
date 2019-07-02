package org.activiti.demo.business.listener.execution;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class DepartManagerApproveListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("部门经理审批");
    }
}
