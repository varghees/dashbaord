/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.api.admin.controller;

import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.visumbu.api.admin.service.AdwordsService;
import com.visumbu.api.admin.service.BingService;
import com.visumbu.api.admin.service.FacebookService;
import com.visumbu.api.admin.service.GaService;
import com.visumbu.api.admin.service.UserService;
import com.visumbu.api.adwords.report.xml.bean.AccountDeviceReport;
import com.visumbu.api.adwords.report.xml.bean.AccountDeviceReportRow;
import com.visumbu.api.adwords.report.xml.bean.AccountReport;
import com.visumbu.api.adwords.report.xml.bean.AccountReportRow;
import com.visumbu.api.adwords.report.xml.bean.AdGroupReportRow;
import com.visumbu.api.adwords.report.xml.bean.AdReport;
import com.visumbu.api.adwords.report.xml.bean.AdReportRow;
import com.visumbu.api.adwords.report.xml.bean.AddGroupReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignDeviceReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignDeviceReportRow;
import com.visumbu.api.adwords.report.xml.bean.CampaignPerformanceReportRow;
import com.visumbu.api.adwords.report.xml.bean.CampaignReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignReportRow;
import com.visumbu.api.adwords.report.xml.bean.GeoReport;
import com.visumbu.api.adwords.report.xml.bean.GeoReportRow;
import com.visumbu.api.bean.ColumnDef;
import com.visumbu.api.bean.LoginUserBean;
import com.visumbu.api.bing.report.xml.bean.AccountDevicePerformanceReport;
import com.visumbu.api.bing.report.xml.bean.AccountDevicePerformanceRow;
import com.visumbu.api.bing.report.xml.bean.AccountPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.AccountPerformanceRow;
import com.visumbu.api.bing.report.xml.bean.AdGroupPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.AdGroupPerformanceRow;
import com.visumbu.api.bing.report.xml.bean.CampaignDevicePerformanceReport;
import com.visumbu.api.bing.report.xml.bean.CampaignDevicePerformanceRow;
import com.visumbu.api.bing.report.xml.bean.CampaignPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.CampaignPerformanceRow;
import com.visumbu.api.bing.report.xml.bean.GeoCityLocationPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.GeoCityLocationPerformanceRow;
import com.visumbu.api.dashboard.bean.AccountPerformanceReportBean;
import com.visumbu.api.dashboard.bean.AdGroupPerformanceReportBean;
import com.visumbu.api.dashboard.bean.AdPerformanceReportBean;
import com.visumbu.api.dashboard.bean.CampaignDevicePerformanceReportBean;
import com.visumbu.api.dashboard.bean.DevicePerformanceReportBean;
import com.visumbu.api.dashboard.bean.CampaignPerformanceReportBean;
import com.visumbu.api.dashboard.bean.ClicksImpressionsGraphBean;
import com.visumbu.api.dashboard.bean.ClicksImpressionsHourOfDayBean;
import com.visumbu.api.dashboard.bean.GeoPerformanceReportBean;
import com.visumbu.api.utils.DateUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Varghees
 */
@Controller
@RequestMapping("paidSocial")
public class PaidSocialTabController {

    @Autowired
    private UserService userService;

    @Autowired
    private BingService bingService;

    @Autowired
    private AdwordsService adwordsService;

    @Autowired
    private FacebookService facebookService;

    @RequestMapping(value = "accountPerformance", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getAccountPerformance(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));

        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("visits", "string", "Visits", 1));
        columnDefs.add(new ColumnDef("sessions", "string", "Sessions", 1));
        columnDefs.add(new ColumnDef("newSessions", "string", "New Sessions Percentage", 1));
        columnDefs.add(new ColumnDef("newUsers", "string", "New Users", 1));
        columnDefs.add(new ColumnDef("pageViews", "string", "Page Views", 1));
        columnDefs.add(new ColumnDef("pagesOraganicVisits", "string", "Pages Organic Visits", 1));
        columnDefs.add(new ColumnDef("bounceRate", "string", "Bounce Rage", 1));
        columnDefs.add(new ColumnDef("avgTimeOnSite", "string", "Average Time On Site", 1));
        columnDefs.add(new ColumnDef("directionsPageView", "number", "Directions Page View", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("inventoryPageViews", "number", "Inventory Page Views", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("leadSubmission", "number", "Lead Submission", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("specialsPageView", "number", "Specials Page View", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("timeOnSiteGt2Mins", "number", "Time On Site > 2Mins", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("vdpViews", "number", "VDP Views", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("engagements", "number", "Engagements", ColumnDef.Aggregation.AVG));

        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        Object fbPublishedPosts = facebookService.getAccountPerformance();
        returnMap.put("data", fbPublishedPosts);
        return returnMap;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        e.printStackTrace();
    }
}