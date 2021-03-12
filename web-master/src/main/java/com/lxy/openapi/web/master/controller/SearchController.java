package com.lxy.openapi.web.master.controller;

import com.lxy.openapi.web.master.feign.LogIndexService;
import com.lxy.openapi.web.master.bean.TableData;
import com.lxy.openapi.web.master.pojo.AppInfo;
import com.lxy.openapi.web.master.service.AppInfoService;
import com.lxy.openapi.web.master.util.JsonUtil;
import com.lxy.openapi.web.master.util.SearchPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 搜索日志服务
 */
@RestController
@RequestMapping("/sys/search")
public class SearchController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private LogIndexService searchService;

    @RequestMapping("/table")
    public TableData search(SearchPojo criteria,int page ,int limit) {
        criteria.setHighLightPostTag("</B></font>");
        criteria.setHighLightPreTag("<font style='color:red'><B>");
        criteria.setStart((page - 1) * limit);
        criteria.setRows(limit);
        String str = JsonUtil.getJSON(criteria);
        Long count = searchService.searchLogCount(str);
        if (count != null && count > 0) {
            List<Map> list = searchService.searchLog(str);
            return new TableData(count, list);
        }
        return new TableData(0, null);
    }

    /**
     * 公司下拉列表
     */
    @RequestMapping("/app")
    public List<AppInfo> appInfo() {
        return appInfoService.getSimpleInfoList();
    }
}
