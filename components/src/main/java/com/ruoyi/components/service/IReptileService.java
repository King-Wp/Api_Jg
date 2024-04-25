package com.ruoyi.components.service;


import com.ruoyi.components.domain.ReceiveParameters.KeyWordsParams;

/**
 * @author: 11653
 * @createTime: 2024/03/21 15:04
 * @package: com.ruoyi.system.service
 * @description: 爬虫接口 AI接口
 */

public interface IReptileService {

    /**
     * 客户洞察 优先级如下：招投标标题金额top10、业绩合同名称、经营范围。
     * @param keyWordsParams 关键词获取参数
     * @return 关键词
     */
    String[] keyToLevel(KeyWordsParams keyWordsParams);

    /**
     * 获取访问关键字(生态)
     * @param baseId 可以没有
     * @param company 公司名称
     * @param bidTitles 单位名称查询招投标标题金额top10
     * @param contracts 单位名称查询业绩合同名称
     * @param business 单位名称查询经营范围
     * @return
     */
    String[] getVisitKeyWord(KeyWordsParams keyWordsParams);

    /**
     * 商机关键词 需要传入商机ID
     * @return 生态伙伴列表
     */
    String getReportKeyword(KeyWordsParams keyWordsParams);

    /**
     * 免费获取工商信息
     * @param companyName 企业名称
     * @param userName 创建用户
     */
    void addBusinessFreeByTyc(String companyName, String userName);

    String getCompanyProfile(String companyId);
}
