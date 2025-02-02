package com.ruoyi.components.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.components.domain.CusPersonaCompanyNode;
import com.ruoyi.components.domain.CusPersonaCompanyRelationship;
import com.ruoyi.components.domain.vo.CustomerBusinessVo;
import com.ruoyi.components.mapper.CusPersonaCompanyNodeMapper;
import com.ruoyi.components.mapper.CusPersonaCompanyRelationshipMapper;
import com.ruoyi.components.service.ICusPersonaCompanyNodeService;
import org.apache.commons.collections4.CollectionUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    public Integer addCorporateRelationsByTyc(String companyId, String companyName, String userName) {
        int res = 0;
        if (companyId != null) {
            String url = "https://dis.tianyancha.com/dis/getInfoById/" + companyId + ".json";
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            try {
                //获取DefaultHttpClient请求;
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream inputStream = response.getEntity().getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }

                //处理返回参数
                JSONObject resultObj = JSONObject.parseObject(result.toString());
                if (resultObj.getJSONObject("data") != null) {
                    JSONArray nodes = resultObj.getJSONObject("data").getJSONArray("nodes");
                    JSONArray relationships = resultObj.getJSONObject("data").getJSONArray("relationships");
                    for (int i = 0; i < nodes.size(); i++) {
                        CusPersonaCompanyNode cusPersonaCompanyNode = new CusPersonaCompanyNode();
                        JSONObject nodesJSONObject = nodes.getJSONObject(i);
                        cusPersonaCompanyNode.setCompanyId(companyId);
                        cusPersonaCompanyNode.setCompanyName(companyName);
                        cusPersonaCompanyNode.setNodeId(nodesJSONObject.getString("id"));
                        JSONArray labels = nodesJSONObject.getJSONArray("labels");
                        StringBuilder nodeLabel = new StringBuilder();
                        for (int l = 0; l < labels.size(); l++) {
                            if (l == labels.size() - 1) {
                                nodeLabel.append(labels.get(l));
                            } else {
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
                    for (int j = 0; j < relationships.size(); j++) {
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
                        for (int l = 0; l < labels.size(); l++) {
                            if (l == labels.size() - 1) {
                                relationshipLabel.append(labels.get(l));
                            } else {
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
                logger.error(e.getMessage());
            }
        }
        logger.info("{}入库{}条企业关系记录", companyName, res);
        return res;
    }

    @Override
    public Integer addCorporateRelationsByTyc(List<CustomerBusinessVo> customerList, String userName) {
        int res = 0;

        if (CollectionUtils.isNotEmpty(customerList)) {
            for (CustomerBusinessVo companyBusiness : customerList) {
                if (companyBusiness.getCompanyId() != null) {
                    String url = "https://dis.tianyancha.com/dis/getInfoById/" + companyBusiness.getCompanyId() + ".json";
                    StringBuilder result = new StringBuilder();
                    BufferedReader bufferedReader = null;

                    try {
                        //获取DefaultHttpClient请求;
                        HttpClient client = HttpClientBuilder.create().build();
                        HttpGet request = new HttpGet(url);
                        HttpResponse response = client.execute(request);
                        InputStream inputStream = response.getEntity().getContent();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        String line;
                        while (null != (line = bufferedReader.readLine())) {
                            result.append(line);
                        }

                        //处理返回参数
                        JSONObject resultObj = JSONObject.parseObject(result.toString());
                        if (resultObj.getJSONObject("data") != null) {
                            JSONArray nodes = resultObj.getJSONObject("data").getJSONArray("nodes");
                            JSONArray relationships = resultObj.getJSONObject("data").getJSONArray("relationships");
                            for (int i = 0; i < nodes.size(); i++) {
                                CusPersonaCompanyNode cusPersonaCompanyNode = new CusPersonaCompanyNode();
                                JSONObject nodesJSONObject = nodes.getJSONObject(i);
                                cusPersonaCompanyNode.setCompanyId(companyBusiness.getCompanyId());
                                cusPersonaCompanyNode.setCompanyName(companyBusiness.getCompany());
                                cusPersonaCompanyNode.setNodeId(nodesJSONObject.getString("id"));
                                JSONArray labels = nodesJSONObject.getJSONArray("labels");
                                StringBuilder nodeLabel = new StringBuilder();
                                for (int l = 0; l < labels.size(); l++) {
                                    if (l == labels.size() - 1) {
                                        nodeLabel.append(labels.get(l));
                                    } else {
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
                            for (int j = 0; j < relationships.size(); j++) {
                                CusPersonaCompanyRelationship cusPersonaCompanyRelationship = new CusPersonaCompanyRelationship();
                                JSONObject relationshipJSONObject = relationships.getJSONObject(j);
                                cusPersonaCompanyRelationship.setCompanyId(companyBusiness.getCompanyId());
                                cusPersonaCompanyRelationship.setCompanyName(companyBusiness.getCompany());
                                cusPersonaCompanyRelationship.setRelationshipId(relationshipJSONObject.getString("id"));
                                cusPersonaCompanyRelationship.setStartNode(relationshipJSONObject.getString("startNode"));
                                cusPersonaCompanyRelationship.setEndNode(relationshipJSONObject.getString("endNode"));
                                JSONObject properties = relationshipJSONObject.getJSONObject("properties");
                                JSONArray labels = properties.getJSONArray("labels");
                                StringBuilder relationshipLabel = new StringBuilder();
                                for (int l = 0; l < labels.size(); l++) {
                                    if (l == labels.size() - 1) {
                                        relationshipLabel.append(labels.get(l));
                                    } else {
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
                        logger.error(e.getMessage());
                    }
                }
            }
        }

        return res;
    }


}
