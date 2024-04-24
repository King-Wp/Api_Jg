package com.ruoyi.components.utils;


import org.jsoup.nodes.Element;

public class TableElement {

    //html页面的Element信息
    private Element element;

    //是否横向，true为横向，false为竖向
    private boolean isCross;

    private int wordNum;

    public TableElement(){
        isCross = true;
    }

    public Element getElement() {
        return element;
    }
    public void setElement(Element element) {
        this.element = element;
    }
    public boolean isCross() {
        return isCross;
    }
    public void setCross(boolean isCross) {
        this.isCross = isCross;
    }
    public int getWordNum() {
        return wordNum;
    }
    public void setWordNum(int wordNum) {
        this.wordNum = wordNum;
    }

}