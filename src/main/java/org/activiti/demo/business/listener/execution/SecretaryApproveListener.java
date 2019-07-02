package org.activiti.demo.business.listener.execution;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class SecretaryApproveListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("秘书审批");

    }
}
