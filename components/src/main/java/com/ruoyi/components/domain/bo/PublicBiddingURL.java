package com.ruoyi.components.domain.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PublicBiddingURL implements Serializable {

    private static final long serialVersionUID = 1L;

    public String parentId;
    public String parentChnlDesc;
    public String chnlDesc;
    public PublicBiddingURL(String parentId, String parentChnlDesc, String chnlDesc){
        this.parentId = parentId;
        this.parentChnlDesc = parentChnlDesc;
        this.chnlDesc = chnlDesc;
    }
}
