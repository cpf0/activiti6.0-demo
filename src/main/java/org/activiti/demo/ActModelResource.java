package org.activiti.demo;

import io.github.jhipster.web.util.ResponseUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * 流程模型controller
 * @author xuzhipeng
 * @date 2018-11-21 13:39:47
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class ActModelResource {

    private final Logger log = LoggerFactory.getLogger(ActModelResource.class);

    @Autowired
    private ActModelService actModelService;

    /**
     * 创建模型
     *
     * @param actModelVO
     * @return the ResponseEntity with status 201 (Created) and with body the new model
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException           if the Location URI syntax is incorrect
     */
    @PostMapping("/models")
    public ResponseEntity<Model> createModel(@Valid @RequestBody ActModelVO actModelVO) throws UnsupportedEncodingException, URISyntaxException {
        log.debug("REST request to save Model : {}", actModelVO);
        Model result = actModelService.createModel(actModelVO);
        return ResponseEntity.created(new URI("/act/models/" + result.getId()))
                .body(result);
    }

    /**
     * 根据模型id查询
     *
     * @param modelId 模型id
     * @return
     */
    @GetMapping("/models/{modelId}")
    public ResponseEntity<Model> getModel(@PathVariable String modelId) {
        log.debug("REST request to get Model : {}", modelId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(actModelService.getModelById(modelId)));
    }

    /**
     * 查询模型列表
     *
     * @return the ResponseEntity with status 200 (OK) and with body all models
     */
    @GetMapping("/models")
    public ResponseEntity<List<Model>> getAllModels() {
        List<Model> allModels = actModelService.getAllModels();
        //   HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/act/models");
     //   return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        return new ResponseEntity<>(allModels,HttpStatus.OK);
    }

    /**
     * 部署模型
     *
     * @return
     */
    @PutMapping("/models/deploy/{modelId}")
    public ResponseEntity<Void> deployModel(@PathVariable("modelId") String modelId) {
        log.debug("REST request to deploy Model : {}", modelId);
        try {
            List<ProcessDefinition> processDefinitions = actModelService.deployModel(modelId);
            if (!CollectionUtils.isEmpty(processDefinitions)) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除模型
     *
     * @param modelId 模型id
     * @return
     */
    @DeleteMapping("/models/{modelId}")
    public ResponseEntity<Void> deleteModel(@PathVariable String modelId) {
        log.debug("REST request to delete Model: {}", modelId);
        actModelService.deleteModel(modelId);
        return ResponseEntity.ok().build();
    }

    /**
     * 导出模型xml文件
     *
     * @param modelId  模型id
     * @param response HttpServletResponse
     * @throws IOException
     */
    @GetMapping("/models/export")
    public void exportModel(@RequestParam("modelId") String modelId, HttpServletResponse response) throws IOException {
        actModelService.exportModel(modelId, response);
    }





}
