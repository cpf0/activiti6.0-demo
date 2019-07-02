package org.activiti.demo.business;

public enum  MeetingApproveStatusEnum {

    AGREE(1,"同意","同意申请"),
    Reject(2,"拒绝","拒绝申请"),
    FallBack(3,"退回","退回申请");

    private Integer code;
    private String defaultApproveMessage;
    private String description;
    MeetingApproveStatusEnum(Integer code, String defaultApproveMessage, String description){
        this.code = code;
        this.defaultApproveMessage =defaultApproveMessage;
        this.description= description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDefaultApproveMessage() {
        return defaultApproveMessage;
    }

    public void setDefaultApproveMessage(String defaultApproveMessage) {
        this.defaultApproveMessage = defaultApproveMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
