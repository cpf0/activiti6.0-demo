package org.activiti.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * 流程模型service
 * @author xuzhipeng
 * @date 2018-11-21 11:28:38
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ActModelService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建模型
     * @param actModelVO
     * @return
     * @throws UnsupportedEncodingException
     */
    @Transactional(readOnly = false)
    public Model createModel(ActModelVO actModelVO) throws UnsupportedEncodingException{
        //初始化一个空模型
        Model model = repositoryService.newModel();
        model.setKey(actModelVO.getKey());
        model.setName(actModelVO.getName());
        model.setCategory(actModelVO.getCategory());
        model.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(model.getKey()).count()+1)));

        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, model.getVersion());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, actModelVO.getDescription());

        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);

        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");

        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);

        repositoryService.addModelEditorSource(model.getId(),editorNode.toString().getBytes("utf-8"));
        return model;
    }

    /**
     * 根据模型id查询
     * @param modelId 模型id
     * @return
     */
    public Model getModelById(String modelId){
        return repositoryService.getModel(modelId);
    }

    /**
     * 查询模型列表
     * @return
     */
    public List<Model> getAllModels(){
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
       // return new PageImpl<>(modelQuery.listPage(pageable.getPageNumber()*pageable.getPageSize(), pageable.getPageSize()), pageable, modelQuery.count());
        return modelQuery.list();
    }

    /**
     * 部署模型
     * @param modelId 模型id
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = false)
    public List<ProcessDefinition> deployModel(String modelId) throws IOException{
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        ObjectNode editorNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
                .addInputStream(modelData.getKey()+".bpmn20.xml", in).enableDuplicateFiltering().deploy();
        log.info("deploy:{}",deployment.getId());
        log.info("deploy:{}",deployment.getName());
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
    }

    /**
     * 删除模型
     * @param modelId 模型id
     */
    @Transactional(readOnly = false)
    public void deleteModel(String modelId){
        repositoryService.deleteModel(modelId);
    }

    /**
     * 导出模型xml文件
     * @param modelId 模型id
     * @param response HttpServletResponse
     * @throws IOException
     */
    public void exportModel(String modelId, HttpServletResponse response) throws IOException{
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        ObjectNode editorNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        IOUtils.copy(in, response.getOutputStream());
        String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
        response.flushBuffer();
    }

}