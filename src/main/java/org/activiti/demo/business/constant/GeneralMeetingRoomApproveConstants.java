package org.activiti.demo.business.constant;

public class GeneralMeetingRoomApproveConstants {


    /**
     * 流程唯一Id
     */
    public static  final String processDefinitionKey ="meetingApproveProcess";
    /**
     * 首次申请
     */
    public static final String firstApprove = "firstApprove";
    /**
     * 申请人
     */
    public static  final String approveUser= "approveUser";

    /**
     * 审核人 - 秘书
     */
    public static  final  String roleSecretary ="roleSecretary";
    /**
     * 秘书审核 1 ,2,3
     */
    public static  final String secretaryApprove = "secretaryApprove";

    /**
     * 秘书审批意见
     */
    public static final String secretaryAgreement="secretaryAgreement";

    /**
     * 审核人 - 经理
     */
    public static  final  String roleManager ="roleManager";

    /**
     * 经理审核 1,2,3
     */
    public static final String  managerApprove = "managerApprove";
    /**
     * 经理审批意见
     */
    public static final String managerAgreement="managerAgreement";


}
