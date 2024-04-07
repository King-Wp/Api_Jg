package com.comp.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.comp.components.domain.CusPersonaCompanyNode;
import com.comp.components.domain.CusPersonaCompanyRelationship;
import com.comp.components.domain.vo.CustomerBusinessVo;
import com.comp.components.mapper.CusPersonaCompanyNodeMapper;
import com.comp.components.mapper.CusPersonaCompanyRelationshipMapper;
import com.comp.components.service.ICusPersonaCompanyNodeService;
import com.ruoyi.common.utils.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author: 11653
 * @createTime: 2024/03/25 9:09
 * @package: com.ruoyi.system.service.impl
 * @description: 企业关系图谱节点Service业务层处理
 */

@Service
public class CusPersonaCompanyNodeServiceImpl implements ICusPersonaCompanyNodeService {

    private static final Logger logger = LoggerFactory.getLogger(CusPersonaCompanyNodeServiceImpl.class);

    @Resource
    private CusPersonaCompanyNodeMapper cusPersonaCompanyNodeMapper;
    @Resource
    private CusPersonaCompanyRelationshipMapper cusPersonaCompanyRelationshipMapper;

    @Override
    public void addCorporateRelationsByTyc(CustomerBusinessVo customerBusinessVo, String userName) {
//        List<CustomerBusinessVo> customerList = customersVoMapper.getHasCompanyIdCustomerList();
        String companyId = customerBusinessVo.getCompanyId();
        String companyName = customerBusinessVo.getCompany();
        int res = 0;
//        for (CustomerBusinessVo companyBusiness : customerList){
        long timeFromDate = new Date().getTime();
        if (companyId != null){
            String url = "https://dis.tianyancha.com/dis/getInfoById/"+companyId+".json";
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                //获取DefaultHttpClient请求;
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream inputStream = response.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line;
                while(null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }

                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result.toString());
                if (resultObj.getJSONObject("data") != null){
                    JSONArray nodes = resultObj.getJSONObject("data").getJSONArray("nodes");
                    JSONArray relationships = resultObj.getJSONObject("data").getJSONArray("relationships");
                    for (int i = 0; i < nodes.size(); i++){
                        CusPersonaCompanyNode cusPersonaCompanyNode = new CusPersonaCompanyNode();
                        JSONObject nodesJSONObject = nodes.getJSONObject(i);
                        cusPersonaCompanyNode.setCompanyId(companyId);
                        cusPersonaCompanyNode.setCompanyName(companyName);
                        cusPersonaCompanyNode.setNodeId(nodesJSONObject.getString("id"));
                        JSONArray labels = nodesJSONObject.getJSONArray("labels");
                        StringBuilder nodeLabel = new StringBuilder();
                        for (int l = 0; l < labels.size(); l++){
                            if (l == labels.size() - 1){
                                nodeLabel.append(labels.get(l));
                            }
                            else {
                                nodeLabel.append(labels.get(l)).append(",");
                            }
                        }
                        cusPersonaCompanyNode.setLabels(nodeLabel.toString());
                        JSONObject properties = nodesJSONObject.getJSONObject("properties");
                        cusPersonaCompanyNode.setNodeAias(properties.getString("aias"));
                        cusPersonaCompanyNode.setNodeLogo(properties.getString("logo"));
                        cusPersonaCompanyNode.setNodeName(properties.getString("name"));
                        cusPersonaCompanyNode.setNtype(properties.getString("ntype"));
                        cusPersonaCompanyNode.setCreateBy(userName);
                        cusPersonaCompanyNode.setCreateTime(DateUtils.getNowDate());

                        res = cusPersonaCompanyNodeMapper.insertCusPersonaCompanyNode(cusPersonaCompanyNode);
                    }
                    for (int j = 0; j < relationships.size(); j++){
                        CusPersonaCompanyRelationship cusPersonaCompanyRelationship = new CusPersonaCompanyRelationship();
                        JSONObject relationshipJSONObject = relationships.getJSONObject(j);
                        cusPersonaCompanyRelationship.setCompanyId(companyId);
                        cusPersonaCompanyRelationship.setCompanyName(companyName);
                        cusPersonaCompanyRelationship.setRelationshipId(relationshipJSONObject.getString("id"));
                        cusPersonaCompanyRelationship.setStartNode(relationshipJSONObject.getString("startNode"));
                        cusPersonaCompanyRelationship.setEndNode(relationshipJSONObject.getString("endNode"));
                        JSONObject properties = relationshipJSONObject.getJSONObject("properties");
                        JSONArray labels = properties.getJSONArray("labels");
                        StringBuilder relationshipLabel = new StringBuilder();
                        for (int l = 0; l < labels.size(); l++){
                            if (l == labels.size() - 1){
                                relationshipLabel.append(labels.get(l));
                            }
                            else {
                                relationshipLabel.append(labels.get(l)).append(",");
                            }
                        }
                        cusPersonaCompanyRelationship.setLabels(relationshipLabel.toString());
                        cusPersonaCompanyRelationship.setPercent(properties.getString("percent"));
                        cusPersonaCompanyRelationship.setCreateBy(userName);
                        cusPersonaCompanyRelationship.setCreateTime(DateUtils.getNowDate());
                        res = res + cusPersonaCompanyRelationshipMapper.insertCusPersonaCompanyRelationship(cusPersonaCompanyRelationship);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info(companyName + "入库" + res + "条企业关系记录");
    }
}
