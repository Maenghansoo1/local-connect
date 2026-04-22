package com.local.connect.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 공공API 응답에서 꺼내는 축제 항목 데이터
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventItem {
    private String contentid;
    private String title;
    private String eventstartdate;
    private String eventenddate;
    private String addr1;
    private String firstimage;
    private String tel;
    private String mapy;
    private String mapx;

    public String getContentid() { return contentid; }
    public String getTitle() { return title; }
    public String getEventstartdate() { return eventstartdate; }
    public String getEventenddate() { return eventenddate; }
    public String getAddr1() { return addr1; }
    public String getFirstimage() { return firstimage; }
    public String getTel() { return tel; }
    public String getMapy() { return mapy; }
    public String getMapx() { return mapx; }
}
