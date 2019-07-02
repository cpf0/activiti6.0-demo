/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tijs Rademakers
 */
@RestController
@RequestMapping("/activiti")
public class ModelSaveRestResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  @Autowired
  private RepositoryService repositoryService;
  
  @Autowired
  private ObjectMapper objectMapper;

  @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId
          , String name, String description
          , String json_xml, String svg_xml) {
    try {

      Model model = repositoryService.getModel(modelId);

      ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

      modelJson.put(MODEL_NAME, name);
      modelJson.put(MODEL_DESCRIPTION, description);
      model.setMetaInfo(modelJson.toString());
      model.setName(name);

      repositoryService.saveModel(model);

      repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

      InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
      TranscoderInput input = new TranscoderInput(svgStream);

      PNGTranscoder transcoder = new PNGTranscoder();
      // Setup output
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);

      // Do the transformation
      transcoder.transcode(input, output);
      final byte[] result = outStream.toByteArray();
      repositoryService.addModelEditorSourceExtra(model.getId(), result);
      outStream.close();

    } catch (Exception e) {
      LOGGER.error("Error saving model", e);
      throw new ActivitiException("Error saving model", e);
    }
  }


  /**
   * 删除部署
   * @param deployId
   * @return
   */
  @DeleteMapping("/delete/{deployId}")
  public ResponseEntity<String> deleteProcess(@PathVariable("deployId")String deployId){

    ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();

    defaultProcessEngine.getRepositoryService().deleteDeployment(deployId,true);

    return ResponseEntity.ok("删除成功");
  }

  /**
   * 新建一个空模型
   * @return
   * @throws Exception
   */
  @GetMapping("/create")
  public void newModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //初始化一个空模型
    Model model = repositoryService.newModel();

    //设置一些默认信息
    String name = "new-process";
    String description = "";
    int revision = 1;
    String key = "process";

    ObjectNode modelNode = objectMapper.createObjectNode();
    modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
    modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
    modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

    model.setName(name);
    model.setKey(key);
    model.setMetaInfo(modelNode.toString());

    repositoryService.saveModel(model);
    String id = model.getId();

    //完善ModelEditorSource
    ObjectNode editorNode = objectMapper.createObjectNode();
    editorNode.put("id", "canvas");
    editorNode.put("resourceId", "canvas");
    ObjectNode stencilSetNode = objectMapper.createObjectNode();
    stencilSetNode.put("namespace",
            "http://b3mn.org/stencilset/bpmn2.0#");
    editorNode.set("stencilset", stencilSetNode);
    repositoryService.addModelEditorSource(id,editorNode.toString().getBytes("utf-8"));
    response.sendRedirect("/modeler.html?modelId="+id);
  }
}
