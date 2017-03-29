/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.service;

import com.visumbu.vb.admin.dao.UiDao;
import com.visumbu.vb.bean.ReportColumnBean;
import com.visumbu.vb.bean.ReportWidgetBean;
import com.visumbu.vb.bean.TabWidgetBean;
import com.visumbu.vb.bean.WidgetColumnBean;
import com.visumbu.vb.model.Dashboard;
import com.visumbu.vb.model.DashboardTabs;
import com.visumbu.vb.model.DataSet;
import com.visumbu.vb.model.DataSource;
import com.visumbu.vb.model.Product;
import com.visumbu.vb.model.Report;
import com.visumbu.vb.model.ReportColumn;
import com.visumbu.vb.model.ReportType;
import com.visumbu.vb.model.ReportWidget;
import com.visumbu.vb.model.TabWidget;
import com.visumbu.vb.model.VbUser;
import com.visumbu.vb.model.WidgetColumn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author netphenix
 */
@Service("uiService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UiService {

    @Autowired
    private UiDao uiDao;

    final static Logger log = Logger.getLogger(UiService.class);

    public List<Product> getProduct() {
        log.debug("Start function of getProduct in UiService class");
        List<Product> product = uiDao.read(Product.class);
        List<Product> returnList = new ArrayList<>();
        for (Iterator<Product> iterator = product.iterator(); iterator.hasNext();) {
            Product product1 = iterator.next();
            if (!product1.getProductName().equalsIgnoreCase("overall")) {
                returnList.add(product1);
            }
        }
        log.debug("End function of getProduct in UiService class");
        return returnList;
    }

    public List<Product> getDealerProduct(Integer dealerId) {
        log.debug("Start function of getDealerProduct in UiService class");
        log.debug("End function of getDealerProduct in UiService class");
        return uiDao.getDealerProduct(dealerId);
    }

    public List<Dashboard> getDashboards(VbUser user) {
        log.debug("Start function of getDashboards in UiService class");
        log.debug("End function of getDashboards in UiService class");
        return uiDao.getDashboards(user);
    }

    public DashboardTabs createDashboardTabs(DashboardTabs dashboardTabs) {
        log.debug("Start function of createDashboardTabs in UiService class");
        log.debug("End function of createDashboardTabs in UiService class");
        return (DashboardTabs) uiDao.create(dashboardTabs);
    }

    public DashboardTabs updateTab(DashboardTabs dashboardTab) {
        log.debug("Start function of updateTab in UiService class");
        log.debug("End function of updateTab in UiService class");
        return (DashboardTabs) uiDao.update(dashboardTab);
    }

    public String updateDashboardTab(Integer dashboardId, String tabOrder) {
        log.debug("Start function of updateDashboardTab in UiService class");
        log.debug("End function of updateDashboardTab in UiService class");
        return uiDao.updateTabOrder(dashboardId, tabOrder);
    }

    public List<DashboardTabs> getDashboardTabs(Integer dbId) {
        log.debug("Start function of getDashboardTabs in UiService class");
        log.debug("End function of getDashboardTabs in UiService class");
        return uiDao.getDashboardTabs(dbId);
    }

    public DashboardTabs deleteDashboardTab(Integer id) {
        log.debug("Start function of deleteDashboardTab in UiService class");
        log.debug("End function of deleteDashboardTab in UiService class");
        return uiDao.deleteDashboardTab(id);
    }

    public TabWidget createTabWidget(Integer tabId, TabWidget tabWidget) {
        log.debug("Start function of createTabWidget in UiService class");
        tabWidget.setTabId(uiDao.getTabById(tabId));
        if (tabWidget.getId() != null) {
            TabWidget tabWidgetDb = uiDao.getTabWidgetById(tabWidget.getId());
            if (tabWidget.getWidgetTitle() != null) {
                tabWidgetDb.setWidgetTitle(tabWidget.getWidgetTitle());
            }
            if (tabWidget.getDirectUrl() != null) {
                tabWidgetDb.setDirectUrl(tabWidget.getDirectUrl());
            }
            if (tabWidget.getChartType() != null) {
                tabWidgetDb.setChartType(tabWidget.getChartType());
            }
            log.debug("End function of createTabWidget in UiService class");
            return (TabWidget) uiDao.update(tabWidgetDb);
        }
        log.debug("End function of createTabWidget in UiService class");
        return (TabWidget) uiDao.create(tabWidget);
    }

    public String updateWidgetUpdateOrder(Integer tabId, String widgetOrder) {
        log.debug("Start function of updateWidgetUpdateOrder UiService class");
        log.debug("End function of updateWidgetUpdateOrder in UiService class");
        return uiDao.updateWidgetUpdateOrder(tabId, widgetOrder);
    }

    public TabWidget deleteTabWidget(Integer id) {
        log.debug("Start function of deleteTabWidget in UiService class");
        log.debug("End function of deleteTabWidget in UiService class");
        return uiDao.deleteTabWidget(id);
    }

    public List<TabWidget> getTabWidget(Integer tabId) {
        log.debug("Start function of getTabWidget in UiService class");
        log.debug("End function of getTabWidget in UiService class");
        return uiDao.getTabWidget(tabId);
    }

    public Dashboard getDashboardById(Integer dashboardId) {
        log.debug("Start function of getDashboardById in UiService class");
        log.debug("End function of getDashboardById in UiService class");
        return uiDao.getDashboardById(dashboardId);
    }

    public WidgetColumn addWidgetColumn(Integer widgetId, WidgetColumn widgetColumn) {
        log.debug("Start function of addWidgetColumn in UiService class");
        log.debug("End function of addWidgetColumn in UiService class");
        return uiDao.addWidgetColumn(widgetId, widgetColumn);
    }

    public WidgetColumn updateWidgetColumn(Integer widgetId, WidgetColumn widgetColumn) {
        log.debug("Start function of updateWidgetColumn in UiService class");
        log.debug("End function of updateWidgetColumn in UiService class");
        return (WidgetColumn) uiDao.updateWidgetColumn(widgetId, widgetColumn);
    }

    public WidgetColumn deleteWidgetColumn(Integer id) {
        log.debug("Start function of deleteWidgetColumn in UiService class");
        log.debug("End function of deleteWidgetColumn in UiService class");
        return uiDao.deleteWidgetColumn(id);
    }

    public TabWidget saveTabWidget(Integer tabId, TabWidgetBean tabWidgetBean) {
        log.debug("Start function of saveTabWidget in UiService class");
        TabWidget tabWidget = null;
        if (tabWidgetBean.getId() != null) {
            tabWidget = uiDao.getTabWidgetById(tabWidgetBean.getId());
        } else {
            tabWidget = new TabWidget();
        }
        tabWidget.setChartType(tabWidgetBean.getChartType());
        tabWidget.setDirectUrl(tabWidgetBean.getDirectUrl());
        tabWidget.setWidgetTitle(tabWidgetBean.getWidgetTitle());
        tabWidget.setProductName(tabWidgetBean.getProductName());
        tabWidget.setProductDisplayName(tabWidgetBean.getProductDisplayName());
        tabWidget.setTableFooter(tabWidgetBean.getTableFooter());
        tabWidget.setZeroSuppression(tabWidgetBean.getZeroSuppression());
        tabWidget.setDateDuration(tabWidgetBean.getDateDuration());
        tabWidget.setCustomRange(tabWidgetBean.getCustomRange());
        tabWidget.setFrequencyDuration(tabWidgetBean.getFrequencyDuration());
        tabWidget.setMaxRecord(tabWidgetBean.getMaxRecord());
        TabWidget savedTabWidget = uiDao.saveTabWidget(tabWidget);
        List<WidgetColumnBean> widgetColumns = tabWidgetBean.getWidgetColumns();
        uiDao.deleteWidgetColumns(tabWidget.getId());
        log.debug("Inserting or Updating values in WidgetColumn Table");
        for (Iterator<WidgetColumnBean> iterator = widgetColumns.iterator(); iterator.hasNext();) {
            WidgetColumnBean widgetColumnBean = iterator.next();
            WidgetColumn widgetColumn = new WidgetColumn();
            widgetColumn.setFieldName(widgetColumnBean.getFieldName());
            widgetColumn.setDisplayFormat(widgetColumnBean.getDisplayFormat());
            widgetColumn.setDisplayName(widgetColumnBean.getDisplayName());
            widgetColumn.setSortOrder(widgetColumnBean.getSortOrder());
            widgetColumn.setGroupPriority(widgetColumnBean.getGroupPriority());
            widgetColumn.setAgregationFunction(widgetColumnBean.getAgregationFunction());
            widgetColumn.setxAxis(widgetColumnBean.getxAxis());
            widgetColumn.setyAxis(widgetColumnBean.getyAxis());
            widgetColumn.setWidth(widgetColumnBean.getWidth());
            widgetColumn.setWrapText(widgetColumnBean.getWrapText());
            widgetColumn.setAlignment(widgetColumnBean.getAlignment());
            widgetColumn.setFieldType(widgetColumnBean.getFieldType());
            Integer columnHide = null;
            if (widgetColumnBean.getGroupPriority() != null && widgetColumnBean.getGroupPriority() != 0) {
                columnHide = 1;
            }
            if (widgetColumnBean.getColumnHide() != null) {
                columnHide = widgetColumnBean.getColumnHide();
            }
            widgetColumn.setColumnHide(columnHide);
            widgetColumn.setWidgetId(savedTabWidget);
            uiDao.saveOrUpdate(widgetColumn);
        }
        log.debug("End function of saveTabWidget in UiService class");
        return uiDao.getTabWidgetById(tabWidgetBean.getId());
    }

    public ReportType addReportType(ReportType reportTypes) {
        log.debug("Start function of addReportType in UiService class");
        log.debug("End function of addReportType in UiService class");
        return (ReportType) uiDao.create(reportTypes);
    }

    public ReportType updateReportType(ReportType reportTypes) {
        log.debug("Start function of updateReportType in UiService class");
        log.debug("End function of updateReportType in UiService class");
        return (ReportType) uiDao.update(reportTypes);
    }

    public ReportType deleteReportType(Integer reportTypeId) {
        log.debug("Start function of deleteReportType in UiService class");
        log.debug("End function of deleteReportType in UiService class");
        return (ReportType) uiDao.delete(reportTypeId);
    }

    public List getReportType(Integer reportTypeId) {
        log.debug("Start function of getReportType in UiService class");
        log.debug("End function of getReportType in UiService class");
        return uiDao.readReportType(reportTypeId);
    }

    public Report addReport(Report report, Integer reportTypeId) {
        log.debug("Start function of addReport in UiService class");
        log.debug("End function of addReport in UiService class");
        return uiDao.addReport(report, reportTypeId);
    }

    public Report updateReport(Report report) {
        log.debug("Start function of updateReport in UiService class");
        log.debug("End function of updateReport in UiService class");
        return (Report) uiDao.update(report);
    }

    public String updateReportOrder(Integer reportId, String widgetOrder) {
        log.debug("Start function of updateReportOrder in UiService class");
        log.debug("End function of updateReportOrder in UiService class");
        return uiDao.updateReportOrder(reportId, widgetOrder);
    }

    public Report deleteReport(Integer reportId) {
        log.debug("Start function of deleteReport in UiService class");
        log.debug("End function of deleteReport in UiService class");
        return (Report) uiDao.delete(reportId);
    }

    public List getReport() {
        log.debug("Start function of getReport in UiService class");
        List<Report> report = uiDao.read(Report.class);
        log.debug("End function of getReport in UiService class");
        return report;
    }

    public ReportWidget createReportWidget(Integer reportId, ReportWidget reportWidget) {
        log.debug("Start function of createReportWidget in UiService class");
        reportWidget.setReportId(uiDao.getReportById(reportId));
        if (reportWidget.getId() != null) {
            ReportWidget reportWidgetDb = uiDao.getReportWidgetById(reportWidget.getId());
            if (reportWidget.getWidgetTitle() != null) {
                reportWidgetDb.setWidgetTitle(reportWidget.getWidgetTitle());
            }
            if (reportWidget.getDirectUrl() != null) {
                reportWidgetDb.setDirectUrl(reportWidget.getDirectUrl());
            }
            if (reportWidget.getChartType() != null) {
                reportWidgetDb.setChartType(reportWidget.getChartType());
            }
            log.debug("End function of createReportWidget in UiService class");
            return (ReportWidget) uiDao.update(reportWidgetDb);
        }
        log.debug("End function of createReportWidget in UiService class");
        return (ReportWidget) uiDao.create(reportWidget);
    }

    public ReportWidget saveReportWidget(Integer reportId, ReportWidgetBean reportWidgetBean) {
        log.debug("Start function of saveReportWidget in UiService class");
        ReportWidget reportWidget = null;
        if (reportWidgetBean.getId() != null) {
            reportWidget = uiDao.getReportWidgetById(reportWidgetBean.getId());

        } else {
            reportWidget = new ReportWidget();
        }
        reportWidget.setChartType(reportWidgetBean.getChartType());
        reportWidget.setDirectUrl(reportWidgetBean.getDirectUrl());
        reportWidget.setWidgetTitle(reportWidgetBean.getWidgetTitle());
        reportWidget.setProductName(reportWidgetBean.getProductName());
        reportWidget.setProductDisplayName(reportWidgetBean.getProductDisplayName());
        ReportWidget savedReportWidget = uiDao.saveReportWidget(reportWidget);
        List<ReportColumnBean> reportColumns = reportWidgetBean.getReportColumns();
        uiDao.deleteReportColumns(reportWidget.getId());
        log.debug("Inserting or Updating values in ReportColumn table");
        for (Iterator<ReportColumnBean> iterator = reportColumns.iterator(); iterator.hasNext();) {
            ReportColumnBean reportColumnBean = iterator.next();
            ReportColumn reportColumn = new ReportColumn();
            reportColumn.setFieldName(reportColumnBean.getFieldName());
            reportColumn.setDisplayFormat(reportColumnBean.getDisplayFormat());
            reportColumn.setDisplayName(reportColumnBean.getDisplayName());
            reportColumn.setSortOrder(reportColumnBean.getSortOrder());
            reportColumn.setGroupPriority(reportColumnBean.getGroupPriority());
            reportColumn.setAgregationFunction(reportColumnBean.getAgregationFunction());
            reportColumn.setxAxis(reportColumnBean.getxAxis());
            reportColumn.setyAxis(reportColumnBean.getyAxis());
            reportColumn.setWidth(reportColumnBean.getWidth());
            reportColumn.setAlignment(reportColumnBean.getAlignment());
            reportColumn.setReportId(savedReportWidget);
            uiDao.saveOrUpdate(reportColumn);
        }
        log.debug("End function of saveReportWidget in UiService class");
        return savedReportWidget;
    }

    public List getReportWidget(Integer reportId) {
        log.debug("Start function of getReportWidget in UiService class");
        log.debug("End function of getReportWidget in UiService class");
        return uiDao.getReportWidget(reportId);
    }

    public ReportWidget deleteReportWidget(Integer reportId) {
        log.debug("Start function of deleteReportWidget in UiService class");
        log.debug("End function of deleteReportWidget in UiService class");
        return uiDao.deleteReportWidget(reportId);
    }

    public Report getReportById(Integer reportId) {
        log.debug("Start function of getReportById in UiService class");
        log.debug("End function of getReportById in UiService class");
        return uiDao.getReportById(reportId);
    }

    public DataSource create(DataSource dataSource) {
        log.debug("Start function of create in UiService class - DataSource");
        log.debug("End function of create in UiService class - DataSource");
        return (DataSource) uiDao.create(dataSource);
    }

    public DataSource read(Integer id) {
        log.debug("Start function of read in UiService class - DataSource");
        log.debug("End function of read in UiService class - DataSource");
        return (DataSource) uiDao.read(DataSource.class, id);
    }

    public DataSource delete(Integer id) {
        log.debug("Start function of delete in UiService class - DataSource");
        DataSource dataSource = read(id);
        log.debug("End function of delete in UiService class - DataSource");
        return (DataSource) uiDao.delete(dataSource);
    }

    public List<DataSource> getDataSource() {
        log.debug("Start function of getDataSource in UiService class");
        List<DataSource> dataSource = uiDao.read(DataSource.class);
        log.debug("End function of getDataSource in UiService class");
        return dataSource;
    }

    public DataSource update(DataSource dataSource) {
        log.debug("Start function of update in UiService class - DataSource");
        log.debug("End function of update in UiService class - DataSource");
        return (DataSource) uiDao.update(dataSource);
    }

    public List<DataSet> getDateSet() {
        log.debug("Start function of getDataSet in UiService class");
        log.debug("End function of getDataSet in UiService class");
        List<DataSet> dataSet = uiDao.read(DataSet.class);
        return dataSet;
    }

    public DataSet create(DataSet dataSet) {
        log.debug("Start function of create in UiService class - DataSet");
        log.debug("End function of create in UiService class - DataSet");
        return (DataSet) uiDao.create(dataSet);
    }

    public DataSet update(DataSet dataSet) {
        log.debug("Start function of update in UiService class - DataSet");
        log.debug("End function of update in UiService class - DataSet");
        return (DataSet) uiDao.update(dataSet);
    }

    public DataSet readDataSet(Integer id) {
        log.debug("Start function of readDataSet in UiService class - DataSet");
        log.debug("End function of readDataSet in UiService class - DataSet");
        return (DataSet) uiDao.read(DataSet.class, id);
    }

    public DataSet deleteDataSet(Integer id) {
        log.debug("Start function of deleteDataSet in UiService class - DataSet");
        DataSet dataSet = readDataSet(id);
        log.debug("End function of deleteDataSet in UiService class - DataSet");
        return (DataSet) uiDao.delete(dataSet);
    }
}
