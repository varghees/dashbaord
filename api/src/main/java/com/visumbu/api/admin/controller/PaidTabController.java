/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.api.admin.controller;

import com.visumbu.api.admin.service.AdwordsService;
import com.visumbu.api.admin.service.BingService;
import com.visumbu.api.admin.service.UserService;
import com.visumbu.api.adwords.report.xml.bean.AccountDeviceReport;
import com.visumbu.api.adwords.report.xml.bean.AccountDeviceReportRow;
import com.visumbu.api.adwords.report.xml.bean.AdGroupReportRow;
import com.visumbu.api.adwords.report.xml.bean.AddGroupReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignDeviceReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignDeviceReportRow;
import com.visumbu.api.adwords.report.xml.bean.CampaignReport;
import com.visumbu.api.adwords.report.xml.bean.CampaignReportRow;
import com.visumbu.api.adwords.report.xml.bean.GeoReport;
import com.visumbu.api.adwords.report.xml.bean.GeoReportRow;
import com.visumbu.api.bean.ColumnDef;
import com.visumbu.api.bean.LoginUserBean;
import com.visumbu.api.bing.report.xml.bean.AccountDevicePerformanceReport;
import com.visumbu.api.bing.report.xml.bean.AccountDevicePerformanceRow;
import com.visumbu.api.bing.report.xml.bean.AdGroupPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.AdGroupPerformanceRow;
import com.visumbu.api.bing.report.xml.bean.CampaignDevicePerformanceReport;
import com.visumbu.api.bing.report.xml.bean.CampaignDevicePerformanceRow;
import com.visumbu.api.bing.report.xml.bean.CampaignPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.CampaignPerformanceRow;
import com.visumbu.api.bing.report.xml.bean.GeoCityLocationPerformanceReport;
import com.visumbu.api.bing.report.xml.bean.GeoCityLocationPerformanceRow;
import com.visumbu.api.dashboard.bean.AdGroupPerformanceReportBean;
import com.visumbu.api.dashboard.bean.CampaignDevicePerformanceReportBean;
import com.visumbu.api.dashboard.bean.DevicePerformanceReportBean;
import com.visumbu.api.dashboard.bean.CampaignPerformanceReportBean;
import com.visumbu.api.dashboard.bean.GeoPerformanceReportBean;
import com.visumbu.api.utils.DateUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
@RequestMapping("paid")
public class PaidTabController {

    @Autowired
    private UserService userService;

    @Autowired
    private BingService bingService;

    @Autowired
    private AdwordsService adwordsService;

    @RequestMapping(value = "geoPerformance", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getGeoPerformance(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));
        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source"));
        columnDefs.add(new ColumnDef("country", "string", "Country"));
        columnDefs.add(new ColumnDef("state", "string", "State"));
        columnDefs.add(new ColumnDef("city", "string", "City"));
        columnDefs.add(new ColumnDef("zip", "string", "Zip"));
        columnDefs.add(new ColumnDef("impressions", "string", "Impressions"));
        columnDefs.add(new ColumnDef("clicks", "string", "Clicks"));
        columnDefs.add(new ColumnDef("ctr", "string", "CTR"));
        columnDefs.add(new ColumnDef("cpc", "string", "CPC"));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cpa", "string", "CPA"));
        columnDefs.add(new ColumnDef("impressionShare", "string", "Impression Share"));
        columnDefs.add(new ColumnDef("avgPosition", "string", "Average Position"));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        GeoReport adWordsGeoReport = adwordsService.getGeoReport(startDate, endDate, "581-484-4675", "SEARCH");
        GeoCityLocationPerformanceReport bingGeoReport = bingService.getGeoCityLocationPerformanceReport(startDate, endDate);
        List<GeoReportRow> adWordsGeoPerformanceRow = adWordsGeoReport.getGeoReportRow();
        List<GeoCityLocationPerformanceRow> bingGeoPerformanceRows = bingGeoReport.getGeoCityLocationPerformanceRows();
        List<GeoPerformanceReportBean> performanceReportBeans = new ArrayList<>();
        
        return null;

    }

    @RequestMapping(value = "deviceConversion", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getDeviceConversion(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));
        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source"));
        columnDefs.add(new ColumnDef("device", "string", "Device"));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        AccountDeviceReport adwordsAccountDeviceReport = adwordsService.getAccountDevicePerformanceReport(startDate, endDate, "581-484-4675", "SEARCH");
        AccountDevicePerformanceReport bingAccountDevicePerformanceReport = bingService.getAccountDevicePerformanceReport(startDate, endDate);
        List<AccountDeviceReportRow> adwordsAccountDeviceReportRow = adwordsAccountDeviceReport.getAccountDeviceReportRow();
        List<AccountDevicePerformanceRow> bingAccountDevicePerformanceRows = bingAccountDevicePerformanceReport.getAccountDevicePerformanceRows();
        List<DevicePerformanceReportBean> performanceReportBeans = new ArrayList<>();

        for (Iterator<AccountDeviceReportRow> reportRow = adwordsAccountDeviceReportRow.iterator(); reportRow.hasNext();) {
            AccountDeviceReportRow row = reportRow.next();
            DevicePerformanceReportBean performanceBean = new DevicePerformanceReportBean();
            performanceBean.setSource("Google");
            performanceBean.setDevice(row.getDevice());
            performanceBean.setConversions(row.getConversions());
            performanceReportBeans.add(performanceBean);
        }

        for (Iterator<AccountDevicePerformanceRow> reportRow = bingAccountDevicePerformanceRows.iterator(); reportRow.hasNext();) {
            AccountDevicePerformanceRow row = reportRow.next();
            DevicePerformanceReportBean performanceBean = new DevicePerformanceReportBean();
            performanceBean.setSource("Bing");
            performanceBean.setDevice(row.getDeviceType().getValue());
            performanceBean.setConversions(row.getConversions().getValue());
            performanceReportBeans.add(performanceBean);
        }
        returnMap.put("data", performanceReportBeans);
        return returnMap;
    }

    @RequestMapping(value = "campaignDevice", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getCampaignDevice(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));
        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source", 1));
        columnDefs.add(new ColumnDef("campaignName", "string", "Campaign Name"));
        columnDefs.add(new ColumnDef("device", "string", "Device"));
        columnDefs.add(new ColumnDef("impressions", "number", "Impressions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("clicks", "number", "Clicks", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("ctr", "number", "CTR", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cost", "number", "Cost", ColumnDef.Aggregation.SUM, ColumnDef.Format.CURRENCY));
        columnDefs.add(new ColumnDef("averageCpc", "number", "Average CPC", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("averagePosition", "number", "Average Position", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cpa", "number", "CPA", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("searchImpressionsShare", "number", "Search Impressions Share", ColumnDef.Aggregation.AVG));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }

        CampaignDeviceReport adwordsCampaignDeviceReport = adwordsService.getCampaignDeviceReport(startDate, endDate, "581-484-4675", "SEARCH");
        CampaignDevicePerformanceReport bingCampaignDevicePerformanceReport = bingService.getCampaignDevicePerformanceReport(startDate, endDate);
        List<CampaignDeviceReportRow> adwordsCampaignDeviceReportRow = adwordsCampaignDeviceReport.getCampaignDeviceReportRow();
        List<CampaignDevicePerformanceRow> bingCampaignDevicePerformanceRows = bingCampaignDevicePerformanceReport.getCampaignDevicePerformanceRows();
        List<CampaignDevicePerformanceReportBean> performanceReportBeans = new ArrayList<>();

        for (Iterator<CampaignDeviceReportRow> reportRow = adwordsCampaignDeviceReportRow.iterator(); reportRow.hasNext();) {
            CampaignDeviceReportRow row = reportRow.next();
            CampaignDevicePerformanceReportBean performanceBean = new CampaignDevicePerformanceReportBean();
            performanceBean.setSource("Google");
            performanceBean.setDevice(row.getDevice());
            performanceBean.setCampaignName(row.getCampaign());
            performanceBean.setImpressions(row.getImpressions());
            performanceBean.setClicks(row.getClicks());
            performanceBean.setCtr(row.getCtr());

            performanceBean.setCost(row.getCost());
            performanceBean.setAverageCpc(row.getAvgCPC());
            performanceBean.setCpa(row.getCostConv());

            performanceBean.setAveragePosition(row.getAvgPosition());
            performanceBean.setConversions(row.getConversions());
            performanceBean.setSearchImpressionsShare(row.getSearchImprShare());
            performanceReportBeans.add(performanceBean);
        }

        for (Iterator<CampaignDevicePerformanceRow> reportRow = bingCampaignDevicePerformanceRows.iterator(); reportRow.hasNext();) {
            CampaignDevicePerformanceRow row = reportRow.next();
            CampaignDevicePerformanceReportBean performanceBean = new CampaignDevicePerformanceReportBean();
            performanceBean.setSource("Bing");

            performanceBean.setCampaignName(row.getCampaignName().getValue());
            performanceBean.setDevice(row.getDeviceType().getValue());
            performanceBean.setImpressions(row.getImpressions().getValue());
            performanceBean.setClicks(row.getClicks().getValue());
            performanceBean.setCtr(row.getCtr().getValue());
            performanceBean.setCost(row.getSpend().getValue());
            performanceBean.setAverageCpc(row.getAverageCpc().getValue());
            performanceBean.setAveragePosition(row.getAveragePosition().getValue());
            performanceBean.setConversions(row.getConversions().getValue());
            performanceBean.setCpa(row.getCostPerConversion().getValue());
            performanceBean.setSearchImpressionsShare(row.getImpressionSharePercent().getValue());
            performanceReportBeans.add(performanceBean);

        }
        returnMap.put("data", performanceReportBeans);
        return returnMap;
    }

    @RequestMapping(value = "accountDevice", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getAccountDevice(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));

        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source", 1));
        columnDefs.add(new ColumnDef("device", "string", "Device"));
        columnDefs.add(new ColumnDef("impressions", "number", "Impressions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("clicks", "number", "Clicks", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("ctr", "number", "CTR", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cost", "number", "Cost", ColumnDef.Aggregation.SUM, ColumnDef.Format.CURRENCY));
        columnDefs.add(new ColumnDef("averageCpc", "number", "Average CPC", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("averagePosition", "number", "Average Position", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cpa", "number", "CPA", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("searchImpressionsShare", "number", "Search Impressions Share", ColumnDef.Aggregation.AVG));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        AccountDeviceReport adwordsAccountDeviceReport = adwordsService.getAccountDevicePerformanceReport(startDate, endDate, "581-484-4675", "SEARCH");
        AccountDevicePerformanceReport bingAccountDevicePerformanceReport = bingService.getAccountDevicePerformanceReport(startDate, endDate);
        List<AccountDeviceReportRow> adwordsAccountDeviceReportRow = adwordsAccountDeviceReport.getAccountDeviceReportRow();
        List<AccountDevicePerformanceRow> bingAccountDevicePerformanceRows = bingAccountDevicePerformanceReport.getAccountDevicePerformanceRows();
        List<DevicePerformanceReportBean> performanceReportBeans = new ArrayList<>();

        for (Iterator<AccountDeviceReportRow> reportRow = adwordsAccountDeviceReportRow.iterator(); reportRow.hasNext();) {
            AccountDeviceReportRow row = reportRow.next();
            DevicePerformanceReportBean performanceBean = new DevicePerformanceReportBean();
            performanceBean.setSource("Google");
            if (row.getDevice().contains("Tablet")) {
                performanceBean.setDevice("Tablet");
            }
            if (row.getDevice().contains("Mobile")) {
                performanceBean.setDevice("Smartphone");
            }
            if (row.getDevice().contains("Computer")) {
                performanceBean.setDevice("Computer");
            }
//            performanceBean.setDevice(row.getDevice());
            performanceBean.setImpressions(row.getImpressions());
            performanceBean.setClicks(row.getClicks());
            performanceBean.setCtr(row.getCtr());
            String cost = Integer.toString(Integer.parseInt(row.getCost()) / 1000000);
            performanceBean.setCost(cost);
            String cpc = Integer.toString(Integer.parseInt(row.getAvgCPC()) / 1000000);

            performanceBean.setAverageCpc(cpc);
            String cpa = Integer.toString(Integer.parseInt(row.getCostConv()) / 1000000);

            performanceBean.setCpa(cpa);

            performanceBean.setAveragePosition(row.getAvgPosition());
            performanceBean.setConversions(row.getConversions());
            performanceBean.setSearchImpressionsShare(row.getSearchImprShare());
            performanceReportBeans.add(performanceBean);
        }

        for (Iterator<AccountDevicePerformanceRow> reportRow = bingAccountDevicePerformanceRows.iterator(); reportRow.hasNext();) {
            AccountDevicePerformanceRow row = reportRow.next();
            DevicePerformanceReportBean performanceBean = new DevicePerformanceReportBean();
            performanceBean.setSource("Bing");
            performanceBean.setDevice(row.getDeviceType().getValue());
            performanceBean.setImpressions(row.getImpressions().getValue());
            performanceBean.setClicks(row.getClicks().getValue());
            performanceBean.setCtr(row.getCtr().getValue());
            performanceBean.setCost(row.getSpend().getValue());
            performanceBean.setAverageCpc(row.getAverageCpc().getValue());
            performanceBean.setAveragePosition(row.getAveragePosition().getValue());
            performanceBean.setConversions(row.getConversions().getValue());
            performanceBean.setCpa(row.getCostPerConversion().getValue());
            performanceBean.setSearchImpressionsShare(row.getImpressionSharePercent().getValue());
            performanceReportBeans.add(performanceBean);

        }

        returnMap.put("data", performanceReportBeans);
        return returnMap;
    }

    @RequestMapping(value = "adGroups", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getAdGroups(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));

        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source", 1));
        columnDefs.add(new ColumnDef("campaignName", "string", "Campaign Name"));
        columnDefs.add(new ColumnDef("adGroupName", "string", "Ad Group Name", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("impressions", "number", "Impressions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("clicks", "string", "Clicks", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("ctr", "number", "CTR", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cost", "number", "Cost", ColumnDef.Aggregation.SUM, ColumnDef.Format.CURRENCY));
        columnDefs.add(new ColumnDef("averageCpc", "number", "Average CPC", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("averagePosition", "number", "Average Position", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cpa", "number", "CPA", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("searchImpressionsShare", "number", "Search Impressions Share", ColumnDef.Aggregation.AVG));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        AddGroupReport adwordsAdGroupReport = adwordsService.getAdGroupReport(startDate, endDate, "581-484-4675", "SEARCH");
        AdGroupPerformanceReport bingAdGroupPerformanceReport = bingService.getAdGroupPerformanceReport(startDate, endDate);

        List<AdGroupReportRow> adwordsAdGroupReportRow = adwordsAdGroupReport.getAdGroupReportRow();
        List<AdGroupPerformanceRow> bingAdGroupPerformanceRows = bingAdGroupPerformanceReport.getAdGroupPerformanceRows();

        List<AdGroupPerformanceReportBean> performanceReportBeans = new ArrayList<>();

        for (Iterator<AdGroupReportRow> reportRow = adwordsAdGroupReportRow.iterator(); reportRow.hasNext();) {
            AdGroupReportRow row = reportRow.next();
            AdGroupPerformanceReportBean performanceBean = new AdGroupPerformanceReportBean();
            performanceBean.setSource("Google");
            performanceBean.setCampaignName(row.getCampaign());
            performanceBean.setAdGroupName(row.getAdGroupName());
            performanceBean.setImpressions(row.getImpressions());
            performanceBean.setClicks(row.getClicks());
            performanceBean.setCtr(row.getCtr());

            performanceBean.setCost(row.getCost());
            performanceBean.setAverageCpc(row.getAvgCPC());
            performanceBean.setCpa(row.getCostConv());

            performanceBean.setAveragePosition(row.getAvgPosition());
            performanceBean.setConversions(row.getConversions());
            performanceBean.setSearchImpressionsShare(row.getSearchImprShare());
            performanceReportBeans.add(performanceBean);
        }

        for (Iterator<AdGroupPerformanceRow> reportRow = bingAdGroupPerformanceRows.iterator(); reportRow.hasNext();) {
            AdGroupPerformanceRow row = reportRow.next();
            AdGroupPerformanceReportBean performanceBean = new AdGroupPerformanceReportBean();
            performanceBean.setSource("Bing");
            performanceBean.setCampaignName(row.getCampaignName().getValue());
            performanceBean.setAdGroupName(row.getAdGroupName().getValue());
            performanceBean.setImpressions(row.getImpressions().getValue());
            performanceBean.setClicks(row.getClicks().getValue());
            performanceBean.setCtr(row.getCtr().getValue());
            performanceBean.setCost(row.getSpend().getValue());
            performanceBean.setAverageCpc(row.getAverageCpc().getValue());
            performanceBean.setAveragePosition(row.getAveragePosition().getValue());
            performanceBean.setConversions(row.getConversions().getValue());
            performanceBean.setCpa(row.getCostPerConversion().getValue());
            performanceBean.setSearchImpressionsShare(row.getImpressionSharePercent().getValue());
            performanceReportBeans.add(performanceBean);

        }

        return performanceReportBeans;

    }

    @RequestMapping(value = "campaign", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Object getCampaign(HttpServletRequest request, HttpServletResponse response) {
        Date startDate = DateUtils.getStartDate(request.getParameter("startDate"));
        Date endDate = DateUtils.getEndDate(request.getParameter("endDate"));
        String fieldsOnly = request.getParameter("fieldsOnly");
        Map returnMap = new HashMap();
        List<ColumnDef> columnDefs = new ArrayList<>();
        columnDefs.add(new ColumnDef("source", "string", "Source", 1));
        columnDefs.add(new ColumnDef("campaignName", "string", "Campaign Name"));
        columnDefs.add(new ColumnDef("impressions", "number", "Impressions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("clicks", "number", "Clicks", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("ctr", "number", "CTR", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cost", "number", "Cost", ColumnDef.Aggregation.SUM, ColumnDef.Format.CURRENCY));
        columnDefs.add(new ColumnDef("averageCpc", "number", "Average CPC", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("averagePosition", "number", "Average Position", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("conversions", "number", "Conversions", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("cpa", "number", "CPA", ColumnDef.Aggregation.SUM));
        columnDefs.add(new ColumnDef("searchImpressionsShare", "number", "Search Impressions Share", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("searchImpressionsShareLostByBudget", "number", "Search Impressions Share Lost By Budget", ColumnDef.Aggregation.AVG));
        columnDefs.add(new ColumnDef("searchImpressionsShareLostByRank", "number", "Search Impressions Share Lost By Rank", ColumnDef.Aggregation.AVG));
        returnMap.put("columnDefs", columnDefs);
        if (fieldsOnly != null) {
            return returnMap;
        }
        CampaignPerformanceReport campaignPerformanceReport = bingService.getCampaignPerformanceReport(startDate, endDate);
        CampaignReport campaignReport = adwordsService.getCampaignReport(startDate, endDate, "581-484-4675", "SEARCH");
        List<CampaignPerformanceRow> bingCampaignPerformanceRows = campaignPerformanceReport.getCampaignPerformanceRows();
        List<CampaignPerformanceReportBean> performanceReportBeans = new ArrayList<>();

        List<CampaignReportRow> adwordsCampaignReportRow = campaignReport.getCampaignReportRow();
        for (Iterator<CampaignReportRow> reportRow = adwordsCampaignReportRow.iterator(); reportRow.hasNext();) {
            CampaignReportRow row = reportRow.next();
            CampaignPerformanceReportBean campaignBean = new CampaignPerformanceReportBean();
            campaignBean.setSource("Google");
            campaignBean.setCampaignName(row.getCampaign());
            campaignBean.setImpressions(row.getImpressions());
            campaignBean.setClicks(row.getClicks());
            campaignBean.setCtr(row.getCtr());
            campaignBean.setCost(row.getCost());
            campaignBean.setAverageCpc(row.getAvgCPC());
            campaignBean.setAveragePosition(row.getAvgPosition());
            campaignBean.setConversions(row.getConversions());
            campaignBean.setCpa(row.getCostConv());
            campaignBean.setSearchImpressionsShare(row.getSearchImprShare());
            campaignBean.setSearchImpressionsShareLostByBudget(row.getSearchLostISBudget());
            campaignBean.setSearchImpressionsShareLostByRank(row.getSearchLostISRank());
            performanceReportBeans.add(campaignBean);
        }

        for (Iterator<CampaignPerformanceRow> reportRow = bingCampaignPerformanceRows.iterator(); reportRow.hasNext();) {
            CampaignPerformanceRow row = reportRow.next();
            CampaignPerformanceReportBean campaignBean = new CampaignPerformanceReportBean();
            campaignBean.setSource("Bing");
            campaignBean.setCampaignName(row.getCampaignName().getValue());
            campaignBean.setImpressions(row.getImpressions().getValue());
            campaignBean.setClicks(row.getClicks().getValue());
            campaignBean.setCtr(row.getCtr().getValue());
            campaignBean.setCost(row.getSpend().getValue());
            campaignBean.setAverageCpc(row.getAverageCpc().getValue());
            campaignBean.setAveragePosition(row.getAveragePosition().getValue());
            campaignBean.setConversions(row.getConversions().getValue());
            campaignBean.setCpa(row.getCostPerConversion().getValue());
            campaignBean.setSearchImpressionsShare(row.getImpressionSharePercent().getValue());
            campaignBean.setSearchImpressionsShareLostByBudget(row.getImpressionLostToBudgetPercent().getValue());
            campaignBean.setSearchImpressionsShareLostByRank("-");
            performanceReportBeans.add(campaignBean);

        }

        return performanceReportBeans;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        e.printStackTrace();
    }
}
