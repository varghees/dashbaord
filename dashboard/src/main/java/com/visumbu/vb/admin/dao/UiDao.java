/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.dao;

import com.visumbu.vb.admin.dao.bean.ProductBean;
import com.visumbu.vb.bean.ColumnDef;
import com.visumbu.vb.dao.BaseDao;
import com.visumbu.vb.model.Account;
import com.visumbu.vb.model.AdwordsCriteria;
import com.visumbu.vb.model.Agency;
import com.visumbu.vb.model.AgencyProduct;
import com.visumbu.vb.model.Currency;
import com.visumbu.vb.model.Dashboard;
import com.visumbu.vb.model.DashboardTabs;
import com.visumbu.vb.model.DashboardTemplate;
import com.visumbu.vb.model.DataSet;
import com.visumbu.vb.model.DataSource;
import com.visumbu.vb.model.DataSetColumns;
import com.visumbu.vb.model.DataSourceFilter;
import com.visumbu.vb.model.DataSourceSetting;
import com.visumbu.vb.model.DefaultFieldProperties;
import com.visumbu.vb.model.JoinDataSet;
import com.visumbu.vb.model.JoinDataSetCondition;
import com.visumbu.vb.model.Product;
import com.visumbu.vb.model.Report;
import com.visumbu.vb.model.ReportType;
import com.visumbu.vb.model.ReportWidget;
import com.visumbu.vb.model.TabWidget;
import com.visumbu.vb.model.TemplateTabs;
import com.visumbu.vb.model.Timezone;
import com.visumbu.vb.model.UserAccount;
import com.visumbu.vb.model.UserPermission;
import com.visumbu.vb.model.UserPreferences;
import com.visumbu.vb.model.VbUser;
import com.visumbu.vb.model.WidgetColumn;
import com.visumbu.vb.model.WidgetTag;
import java.util.Iterator;
import java.util.List;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

/**
 *
 * @author netphenix
 */
@Transactional
@Repository("uiDao")
public class UiDao extends BaseDao {

    public List<Dashboard> getDashboards(VbUser user) {
        String queryStr = "select d from Dashboard d where d.userId = :user";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("user", user);
        return query.list();
    }

    public List<UserAccount> getUserAccountByUser(VbUser user) {
        System.out.println(user);
        String queryStr = "select d from UserAccount d where (d.userId.status is null or d.userId.status != 'Deleted') and d.accountId.agencyId = d.userId.agencyId and d.userId.id = :userId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", user.getId());
        return query.list();
    }

    public List<DashboardTabs> getAgencyProductTab(Integer agencyProductId, Integer accountId, Integer userId) {
        String queryStr = "select d from DashboardTabs d where (d.status is null or d.status != 'Deleted') and d.agencyProductId.id = :agencyProductId and d.accountId.id = :accountId and d.userId.id = :userId order by tabOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyProductId", agencyProductId);
        query.setParameter("accountId", accountId);
        query.setParameter("userId", userId);
        return query.list();
    }

//    public List<DashboardTabs> getDashboardTabs(Integer dbId) {
//        String queryStr = "select d from DashboardTabs d where (d.status is null or d.status != 'Deleted') and d.dashboardId.id = :dashboardId order by tabOrder";
//        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("dashboardId", dbId);
//        return query.list();
//    }
    public List<DashboardTabs> getDashboardTabsByProduct(Integer productId, Integer userId) {
        String queryStr = "SELECT d FROM DashboardTabs d WHERE (d.status is null or d.status != 'Deleted') and d.dashboardId.productId.id = :productId and d.dashboardId.userId.id = :userId order by tabOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("productId", productId);
        query.setParameter("userId", userId);
        return query.list();
    }

    public List<DashboardTabs> getDashboardTabsByDbId(Integer dashboardId, Integer userId) {
        String queryStr = "SELECT d FROM DashboardTabs d WHERE (d.status is null or d.status != 'Deleted') and d.dashboardId.id = :dashboardId and d.dashboardId.userId.id = :userId order by tabOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dashboardId", dashboardId);
        query.setParameter("userId", userId);
        return query.list();
    }

    public DashboardTabs getDashboardTabById(Integer tabId) {
        DashboardTabs dashboardTabs = (DashboardTabs) sessionFactory.getCurrentSession().get(DashboardTabs.class, tabId);
        return dashboardTabs;
    }

    public DashboardTabs deleteDashboardTab(Integer id) {
        String queryStr = "update DashboardTabs d set status = 'Deleted'  where d.id = :dashboardTabId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dashboardTabId", id);
        query.executeUpdate();
        return null;
    }

    public List<TabWidget> getWidgetsByTab(Integer tabId) {
        String queryStr = "select d from TabWidget d where d.tabId.id = :tabId and(status is null or status != 'Deleted') order by widgetOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("tabId", tabId);
        List<TabWidget> tabWidgets = query.list();
        for (Iterator<TabWidget> iterator = tabWidgets.iterator(); iterator.hasNext();) {
            TabWidget widget = iterator.next();
            widget.setColumns(getColumns(widget));
        }

        return tabWidgets;
    }

    public List<TabWidget> getTabWidget(Integer tabId, Integer accountId) {
        String queryStr = "select d from TabWidget d where d.tabId.id = :tabId and (d.accountId.id=:accountId or d.accountId IS NULL) and (status is null or status != 'Deleted') order by widgetOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("tabId", tabId);
        query.setParameter("accountId", accountId);

        List<TabWidget> tabWidgets = query.list();
        for (Iterator<TabWidget> iterator = tabWidgets.iterator(); iterator.hasNext();) {
            TabWidget widget = iterator.next();
            widget.setColumns(getColumns(widget));
        }

        return tabWidgets;
    }

    public List<TabWidget> getWidget(Integer widgetId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("TabWidget.findById");
        query.setParameter("id", widgetId);
        List<TabWidget> tabWidgets = query.list();
        return tabWidgets;
    }

    public List<TabWidget> getReportWidgetByWidgetId(Integer widgetId) {
        String queryStr = "select d from TabWidget d where (status is null or status != 'Deleted') and d.id = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("widgetId", widgetId);

        List<TabWidget> tabWidgets = query.list();
        for (Iterator<TabWidget> iterator = tabWidgets.iterator(); iterator.hasNext();) {
            TabWidget widget = iterator.next();
            widget.setColumns(getColumns(widget));
        }

        return tabWidgets;
    }

    public TabWidget getTabWidgetById(Integer widgetId) {
        if (widgetId == null) {
            return null;
        }
        TabWidget tabWidget = (TabWidget) sessionFactory.getCurrentSession().get(TabWidget.class, widgetId);
        System.out.println(tabWidget);
        tabWidget.setColumns(getColumns(tabWidget));
        return tabWidget;
    }

    public Dashboard getDashboardById(Integer dashboardId) {
        return (Dashboard) sessionFactory.getCurrentSession().get(Dashboard.class, dashboardId);
    }

    public AgencyProduct getAgencyProductById(Integer agencyProductId) {
        return (AgencyProduct) sessionFactory.getCurrentSession().get(AgencyProduct.class, agencyProductId);
    }

    private List<WidgetColumn> getColumns(TabWidget widget) {
        String queryStr = "select d from WidgetColumn d where d.widgetId = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("widgetId", widget);
        return query.list();
    }

    public List<WidgetColumn> getColumnsById(Integer widgetId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("WidgetColumn.findById");
        query.setParameter("id", widgetId);
        return query.list();
    }

    public List<WidgetColumn> getWidgetColumnsByWidgetId(Integer widgetId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("WidgetColumn.findByWidgetId");
        query.setParameter("id", widgetId);
        return query.list();
    }

    public List<WidgetColumn> getDerivedWidgetColumnsByWidgetId(Integer widgetId) {
        String queryStr = "select w from WidgetColumn w where w.widgetId.id = :id and w.expression IS NOT NULL";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", widgetId);
        return query.list();
    }

    public List<WidgetTag> getWidgetTagsByWidgetId(Integer widgetId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("WidgetTag.findByWidgetId");
        query.setParameter("id", widgetId);
        return query.list();
    }

    public WidgetColumn addWidgetColumn(Integer widgetId, WidgetColumn widgetColumn) {
        widgetColumn.setWidgetId(getTabWidgetById(widgetId));
        create(widgetColumn);
        return widgetColumn;
    }

    public WidgetColumn updateWidgetColumn(Integer widgetId, WidgetColumn widgetColumn) {
        if (widgetColumn.getId() != null) {
            widgetColumn.setWidgetId(getTabWidgetById(widgetId));
            update(widgetColumn);
        }
        return widgetColumn;
    }

    public void deleteWidgetColumns(Integer widgetId) {
        String queryStr = "delete from WidgetColumn d where d.widgetId.id = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("widgetId", widgetId);
        query.executeUpdate();
    }

    public TabWidget deleteTabWidget(Integer id) {
        String removeWidget = "delete from DataSetColumns d where d.widgetId.id = :widgetId";
        Query findDataSet = sessionFactory.getCurrentSession().createQuery(removeWidget);
        findDataSet.setParameter("widgetId", id);
        findDataSet.executeUpdate();

        String queryReport = "delete from ReportWidget d where d.widgetId.id = :widgetId";
        Query querySess = sessionFactory.getCurrentSession().createQuery(queryReport);
        querySess.setParameter("widgetId", id);
        querySess.executeUpdate();

        String queryStr = "delete from WidgetColumn d where d.widgetId.id = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("widgetId", id);
        query.executeUpdate();

        String queryTag = "delete from WidgetTag d where d.widgetId.id = :widgetId";
        Query querys = sessionFactory.getCurrentSession().createQuery(queryTag);
        querys.setParameter("widgetId", id);
        querys.executeUpdate();

        delete(getTabWidgetById(id));
        return null;
    }

    public WidgetColumn deleteWidgetColumn(Integer id) {
        delete(getTabWidgetColumnById(id));
        return null;
    }

    private WidgetColumn getTabWidgetColumnById(Integer id) {
        WidgetColumn widgetColumn = (WidgetColumn) sessionFactory.getCurrentSession().get(WidgetColumn.class, id);
        return widgetColumn;
    }

    public DashboardTabs getTabById(Integer tabId) {
        DashboardTabs dashboardTab = (DashboardTabs) sessionFactory.getCurrentSession().get(DashboardTabs.class, tabId);
        return dashboardTab;
    }

    public TabWidget saveTabWidget(TabWidget tabWidget) {
        sessionFactory.getCurrentSession().saveOrUpdate(tabWidget);
        return tabWidget;
    }

    public WidgetColumn getWidgetColunmById(Integer id) {
        WidgetColumn widgetColumn = (WidgetColumn) sessionFactory.getCurrentSession().get(WidgetColumn.class, id);
        return widgetColumn;
    }

    public Integer getWidgetCount(Integer id) {
        System.out.println("id ---> " + id);
        Query query = sessionFactory.getCurrentSession().getNamedQuery("TabWidget.findByTabId");
        query.setParameter("tabId", id);
//        Integer count = (Integer) query.list().get(0);
//        String widgetCountQuery = ("SELECT count(t.id) from TabWidget t where t.tabId.id = :tabId");
//        Query query = sessionFactory.getCurrentSession().createQuery(widgetCountQuery);
//        query.setParameter("tabId", id);
//        Integer count =at com.visumbu.vb.admin.dao.UiDao.getWidgetCount(UiDao.java:231) (Integer) query.list().get(0);
        int count = (int) (long) query.list().get(0);
        System.out.println("count ---> " + count);
        return count;
    }

    public void saveOrUpdate(Object object) {
        sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public List readReportType(Integer reportTypeId) {
        ReportType reportTypes = (ReportType) sessionFactory.getCurrentSession().get(ReportType.class, reportTypeId);
        return (List) reportTypes;
    }

    public Report addReport(Report report, Integer reportTypeId) {
        report.setReportTypeId(getReportTypeById(reportTypeId));
        create(report);
        return report;
    }

    private ReportType getReportTypeById(Integer reportTypeId) {
        ReportType reportType = (ReportType) sessionFactory.getCurrentSession().get(ReportType.class, reportTypeId);
        return reportType;
    }

//    public Report getReportById(Integer reportId) {
//        Report report = (Report) sessionFactory.getCurrentSession().get(Report.class, reportId);
//        return report;
//    }
//    public ReportWidget getReportWidgetById(Integer reportId) {
//        ReportWidget reportWidget = (ReportWidget) sessionFactory.getCurrentSession().get(ReportWidget.class, reportId);
//        return reportWidget;
//    }
    public ReportWidget saveReportWidget(ReportWidget reportWidget) {
        sessionFactory.getCurrentSession().saveOrUpdate(reportWidget);
        return reportWidget;
    }

    public void deleteReportColumns(Integer reportId) {
        String queryStr = "delete from ReportColumn d where d.reportId.id = :reportId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("reportId", reportId);
        query.executeUpdate();
    }

//    public List<ReportWidget> getReportWidget(Integer reportId) {
//        String queryStr = "select d from ReportWidget d where d.reportId.id = :reportId";
//        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("reportId", reportId);
//
//        List<ReportWidget> tabWidgets = query.list();
//        for (Iterator<ReportWidget> iterator = tabWidgets.iterator(); iterator.hasNext();) {
//            TabWidget widget = iterator.next().getWidgetId();
//            widget.setColumns(getColumns(widget));
//        }
//        return tabWidgets;
//    }
//    private List<WidgetColumn> getColumns(ReportWidget widget) {
//        String queryStr = "select d from WidgetColumn d where d.widgetId = :widgetId";
//        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("widgetId", widget);
//        return query.list();
//    }
//    public ReportWidget deleteReportWidget(Integer id) {
//        delete(getReportWidgetById(id));
//        return null;
//    }
    public String updateWidgetUpdateOrder(Integer tabId, String widgetOrder) {
        String[] widgetOrderArray = widgetOrder.split(",");
        for (int i = 0; i < widgetOrderArray.length; i++) {
            Integer widgetId = Integer.parseInt(widgetOrderArray[i]);
            TabWidget tabWidget = getTabWidgetById(widgetId);
            tabWidget.setWidgetOrder(i);
            update(tabWidget);
        }
        return "Success";
    }

    public String updateTabOrder(Integer dashboardId, String tabOrder) {
        String[] tabOrderArray = tabOrder.split(",");
        for (int i = 0; i < tabOrderArray.length; i++) {
            Integer tabId = Integer.parseInt(tabOrderArray[i]);
            DashboardTabs dashboardTabs = getDashboardTabById(tabId);
            dashboardTabs.setTabOrder(i);
            update(dashboardTabs);
        }
        return null;
    }

//    public String updateReportOrder(Integer reportId, String widgetOrder) {
//        System.out.println(widgetOrder);
//        String[] reportOrderArray = widgetOrder.split(",");
//        for (int i = 0; i < reportOrderArray.length; i++) {
//            Integer reportWidgetId = Integer.parseInt(reportOrderArray[i]);
//            ReportWidget reportWidget = getReportWidgetById(reportWidgetId);
//            // reportWidget.setWidgetOrder(i);
//        System.out.println(reportWidget);
//            update(reportWidget);
//        }
//        return null;
//    }
//    public Report deleteReport(Integer reportId) {
//        String queryString = "update Report d set status = 'Deleted' where d.id = :reportId";
//        Query querySess = sessionFactory.getCurrentSession().createQuery(queryString);
//        querySess.setParameter("reportId", reportId);
//        querySess.executeUpdate();
//        return null;
//    }
    public DefaultFieldProperties getDefaultFieldProperties(String fieldName) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("DefaultFieldProperties.findByFieldName");
        query.setParameter("fieldName", fieldName);
        List fieldProperties = query.list();
        if (fieldProperties.size() > 0) {
            return (DefaultFieldProperties) fieldProperties.get(0);
        }
        return null;
    }

    public AdwordsCriteria getAdwordsCriteria(Integer criteriaId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("AdwordsCriteria.findById");
        query.setParameter("id", criteriaId);
        List data = query.list();
        if (data.size() > 0) {
            return (AdwordsCriteria) data.get(0);
        }
        return null;
    }

    public List<Product> getDealerProduct(Integer dealerId) {
        String queryStr = "select p from DealerProduct dp, Product p where (p.productName = dp.productName or (dp.productName='PPC' and p.productName = 'Paid Search')"
                + " or (p.productName like 'You%Tube%' and dp.productName like 'Video')) and dp.dealerId.id = :dealerId";

        queryStr = "select distinct p.* from product p,  "
                + "(SELECT distinct case when service_name is null then product_name else service_name end service_name "
                + " FROM dealer_product_source dps join  "
                + " dealer_product dp join  "
                + " dealer d  left join  "
                + " dealer_product_service ps on ps.dealer_product_id = dp.id  "
                + " where dp.id = dps.dealer_product_id   "
                + " and dp.dealer_id = d.id  and d.id = :dealerId "
                + " ) a where (a.service_name = p.product_name or (a.service_name like 'YouTube%' and p.product_name='Video') "
                + " or (a.service_name like 'PPC' and p.product_name='Paid Search') "
                + " or (a.service_name like 'Digital Ad Package%' and p.product_name='Paid Search') "
                + " or (a.service_name like 'Facebook%' and p.product_name='Paid Social') "
                + " or (a.service_name like 'Call Tracking' and p.product_name='Paid Search') "
                + " or (a.service_name like '%SEO%' and p.product_name='SEO') "
                + " ) order by p.id";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(queryStr)
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("product_name", StringType.INSTANCE)
                .addScalar("remarks", StringType.INSTANCE)
                .addScalar("icon", StringType.INSTANCE)
                .addScalar("show_product", StringType.INSTANCE)
                .addScalar("product_order", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(ProductBean.class));
        query.setParameter("dealerId", dealerId);
        return query.list();

    }

    public DataSource getDataSourceById(Integer dataSourceId) {
        DataSource dataSource = (DataSource) sessionFactory.getCurrentSession().get(DataSource.class, dataSourceId);
        return dataSource;
    }

    public DataSet getDataSetById(Integer dataSetId) {
        DataSet dataSet = (DataSet) sessionFactory.getCurrentSession().get(DataSet.class, dataSetId);
        return dataSet;
    }

    public TabWidget getWidgetById(Integer widgetId) {
        if (widgetId == null) {
            return null;
        }
        TabWidget tabWidget = (TabWidget) sessionFactory.getCurrentSession().get(TabWidget.class, widgetId);
        return tabWidget;
    }

    public List<DataSet> getDataSetByDataSourceId(Integer id) {
        String queryStr = "select d from DataSet d where d.dataSourceId.id = :dataSourceId";
//        String queryStr = "update DataSet d set data_source_id=NULL  where d.dataSourceId = :dataSourceId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceId", id);
        return query.list();
    }

    public void removeDsFromDataSet(Integer id) {
        String queryStr = "delete DataSet d where d.dataSourceId.id = :dataSourceId";
//        String queryStr = "update DataSet d set data_source_id=NULL  where d.dataSourceId = :dataSourceId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceId", id);
        query.executeUpdate();
    }

    public List<DataSource> getJoinDataSource(VbUser user) {
        String queryStr = "select d from DataSource d where d.dataSourceType = 'join' and d.userId =:userId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("name", name);
        query.setParameter("userId", user);
        return query.list();
    }

    public void removeDsFromWidget(Integer id) {
        String queryStr = "update TabWidget d set dataSourceId=NULL, dataSetId=NULL  where d.dataSourceId.id = :dataSourceId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceId", id);
        query.executeUpdate();
    }
    public void removeTokenDetails(Integer id){
        String stringQuery = "delete from TokenDetails t where t.dataSourceId.id = :dataSourceId";
        Query query = sessionFactory.getCurrentSession().createQuery(stringQuery);
        query.setParameter("dataSourceId", id);
        query.executeUpdate();
    }

    public DataSource deleteDataSource(Integer id) {
        removeTokenDetails(id);
        removeDsFromWidget(id);
        List<DataSet> dataSetList = getDataSetByDataSourceId(id);
        for (Iterator<DataSet> iterator = dataSetList.iterator(); iterator.hasNext();) {
            DataSet dataSet = iterator.next();
            deleteDataSet(dataSet.getId());
        }
//        removeDsFromDataSet(id);

        String queryStr = "delete DataSource d where d.id = :dataSourceId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceId", id);
        query.executeUpdate();
        return null;
    }

    public void removeDataSetFromWidget(Integer id) {
        String queryStr = "update TabWidget d set dataSetId=NULL  where d.dataSetId.id = :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
    }

    public void removeDataSetColumns(Integer id) {
        String queryStr = "delete DataSetColumns d where d.dataSetId.id = :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
    }

    public List<JoinDataSet> getJoinDataSetByDataSetId(Integer id) {
        String queryStr = "select d from JoinDataSet d where d.dataSetIdFirst.id = :dataSetId or d.dataSetIdSecond.id = :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        return query.list();
    }

    public DataSet getDataSetByJoinDataSetId(Integer joinDataSetId) {
        String queryStr = "select d from DataSet d where d.joinDataSetId.id = :joinDataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("joinDataSetId", joinDataSetId);
        return (DataSet) query.uniqueResult();
    }

    public void removeJoinDataSet(Integer id) {
        List<JoinDataSet> joinDataSetList = getJoinDataSetByDataSetId(id);
        for (Iterator<JoinDataSet> iterator = joinDataSetList.iterator(); iterator.hasNext();) {
            JoinDataSet joinDataSet = iterator.next();
            removeJoinDataSetCondition(joinDataSet.getId());
            System.out.println("JoinDataSetId ---> " + joinDataSet.getId());
            DataSet dataSet = getDataSetByJoinDataSetId(joinDataSet.getId());
            if (dataSet != null) {
                deleteDataSet(dataSet.getId());
            }
        }

        String queryStr = "delete JoinDataSet d where d.dataSetIdFirst.id = :dataSetId or d.dataSetIdSecond.id = :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
    }

    public void removeJoinDataSetCondition(Integer joinDataSetId) {
        String queryStr = "delete JoinDataSetCondition d where d.joinDataSetId.id = :joinDataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("joinDataSetId", joinDataSetId);
        query.executeUpdate();
    }
    public void removeMongoScheduler(Integer id){
        String queryStr = "delete from MongoScheduler m where m.dataSetId.id= :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
    }

    public void deleteDataSet(Integer id) {
        removeDataSetFromWidget(id);
        removeDataSetColumns(id);
        removeJoinDataSet(id);
        removeMongoScheduler(id);
        String queryStr = "delete DataSet d where d.id = :dataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
    }

    public DataSetColumns deleteDataSetColumns(Integer id) {
        String queryStr = "delete DataSetColumns d where d.dataSetId.id = :dataSetId and d.widgetId IS NULL";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", id);
        query.executeUpdate();
        return null;
    }

    public List<UserAccount> getUserAccountById(Integer userId) {
        String queryStr = "select d from UserAccount d where d.userId.id = :userId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", userId);
        return query.list();
    }

    public UserAccount findUserAccountById(UserAccount accountId) {
        UserAccount userAccount = (UserAccount) sessionFactory.getCurrentSession().get(UserAccount.class, accountId.getId());
        return userAccount;
    }

    public UserAccount deleteUserAccount(Integer userAccountId) {
        String queryString = "delete UserAccount d where d.id = :userAccountId";
        Query querySess = sessionFactory.getCurrentSession().createQuery(queryString);
        querySess.setParameter("userAccountId", userAccountId);
        querySess.executeUpdate();
        return null;
    }

//    public List getUserAccountId(Integer userId) {
//        String queryStr = "select d from AccountUser d where d.userId.id = :userId";
//        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("userId", userId);           
//        return query.list();
//    }
    public List getUserPermission(Integer userId) {
        String queryStr = "select d from UserPermission d where (d.userId.status is null or d.userId.status != 'Deleted') and d.userId.id = :userId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", userId);
        return query.list();
    }

    public UserPermission deleteUserPermission(Integer userPermissionId) {
        String queryString = "delete UserPermission d where d.id = :userPermissionId";
        Query querySess = sessionFactory.getCurrentSession().createQuery(queryString);
        querySess.setParameter("userPermissionId", userPermissionId);
        querySess.executeUpdate();
        return null;
    }

    public List<VbUser> getUsersByAgencyUser(VbUser user) {
        String queryStr = "select d from VbUser d where (d.agencyId.status is null or d.agencyId.status != 'Deleted') and (d.status is null or d.status != 'Deleted') and d.agencyId.id = :agencyId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyId", user.getAgencyId().getId());
        return query.list();
    }

    public List<VbUser> getUsersByAgency(Agency agency) {
        String queryStr = "select d from VbUser d where (d.agencyId.status is null or d.agencyId.status != 'Deleted') and (d.status is null or d.status != 'Deleted') and d.agencyId.id = :agencyId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyId", agency.getId());
        return query.list();
    }

    public VbUser deleteUser(Integer id) {
        String queryString = "update VbUser d set status = 'Deleted' where d.id = :userId";
        Query querySess = sessionFactory.getCurrentSession().createQuery(queryString);
        querySess.setParameter("userId", id);
        querySess.executeUpdate();
        return null;
    }

    public List<DataSet> getDataSetByUser(VbUser user) {
        String queryStr = "select d from DataSet d where d.agencyId = :agencyId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("userId", user.getId());
        query.setParameter("agencyId", user.getAgencyId());
        return query.list();
    }

    public List<DataSet> getPublishDataSetByUser(VbUser user) {
        String queryStr = "select d from DataSet d where d.agencyId = :agencyId and d.publish = 'Active' ";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("userId", user.getId());
        query.setParameter("agencyId", user.getAgencyId());
        return query.list();
    }

    public List<DataSource> getDataSourceByUser(VbUser user) {
        String queryStr = "select d from DataSource d where d.agencyId = :agencyId";
//        String queryStr = "select d from DataSource_ d where d.userId.id = :userId and d.agencyId = :agencyId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
//        query.setParameter("userId", user.getId());
        query.setParameter("agencyId", user.getAgencyId());
        return query.list();
    }

    public List<WidgetTag> getTagWidgetByWidgetId(Integer widgetId) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery("WidgetTag.findByWidgetId");
        query.setParameter("id", widgetId);
        return query.list();
    }

    public List<DataSetColumns> getDataSetColumnsByDataSetId(Integer dataSetId, Integer userId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.dataSetId.id = :id and (d.userId IS NULL or d.userId.id = :userId) and d.widgetId is null)";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", dataSetId);
        query.setParameter("userId", userId);
        return query.list();
    }

    public List<DataSetColumns> getDataSetColumnsByWidgetId(Integer dataSetId, Integer widgetId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.dataSetId.id = :dataSetId and d.widgetId.id = :widgetId)";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSetId", dataSetId);
        query.setParameter("widgetId", widgetId);
        return query.list();
    }

    public List<Currency> getCurrenciesTypes() {
        String queryStr = "SELECT c FROM Currency c";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        return query.list();
    }

    public List<Timezone> getTimezoneTypes() {
        String queryStr = "SELECT t FROM Timezone t";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        return query.list();

    }

    public TabWidget getWidgetByIdAndDataSetId(Integer widgetId, Integer datasetId) {
        String queryStr = "Select t FROM TabWidget t where t.dataSetId.id = :datasetId and id = :id";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", widgetId);
        query.setParameter("datasetId", datasetId);
        List tabWidgetData = query.list();
        if (tabWidgetData == null || tabWidgetData.isEmpty()) {
            return null;
        }
        System.out.println("tabWidgetData ---> " + tabWidgetData);
        TabWidget tabWidget = (TabWidget) tabWidgetData.get(0);
        tabWidget.setColumns(getColumns(tabWidget));
        return tabWidget;
    }

    public List getDataSetColumnByDatasetId(Integer dataSetId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.dataSetId.id = :id";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", dataSetId);
        return query.list();
    }

    public Account getAccountById(Integer id) {
        Account account = (Account) sessionFactory.getCurrentSession().get(Account.class, id);
        return account;
    }

    public List<JoinDataSetCondition> getJoinDataSetConditionById(Integer id) {
        String queryStr = "select d from JoinDataSetCondition d where d.joinDataSetId.id = :joinDataSetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("joinDataSetId", id);
        return query.list();
    }

    public JoinDataSet getJoinDataSetById(Integer id) {
        JoinDataSet joinDataSet = (JoinDataSet) sessionFactory.getCurrentSession().get(JoinDataSet.class, id);
        return joinDataSet;
    }

    public List<JoinDataSetCondition> deleteJoinDataSetConditionById(Integer conditionId, Integer joinDataSetId) {
        System.out.println("delete join data set method");
        String deleteStr = "delete from JoinDataSetCondition d where d.id = :conditionId";
        Query query = sessionFactory.getCurrentSession().createQuery(deleteStr);
        query.setParameter("conditionId", conditionId);
        query.executeUpdate();
        return getJoinDataSetConditionById(joinDataSetId);
    }

    public List<DashboardTemplate> getTemplateId(Integer accountId, Integer productId, Integer userId) {
        String queryStr = "select d from DashboardTemplate d where d.accountId.id = :accountId and d.agencyProductId.id=:productId and d.userId.id=:userId and (d.status is null or d.status !='Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("accountId", accountId);
        query.setParameter("productId", productId);
        query.setParameter("userId", userId);
        return query.list();
    }

    public List<DashboardTabs> getDashboardTabsByProductId(Integer userId, Integer accountId, Integer productId) {
        String queryStr = "select d from DashboardTabs d where d.accountId.id = :accountId and d.agencyProductId.id=:productId and d.userId.id=:userId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("accountId", accountId);
        query.setParameter("productId", productId);
        query.setParameter("userId", userId);
        return query.list();
    }

    public List<DashboardTemplate> getTemplateByAgencyId(Integer agencyId) {
        String queryStr = "select d from DashboardTemplate d where d.agencyId.id=:agencyId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        return query.list();
    }

    public DataSetColumns getDataSetColumn(String fieldName, Integer userId, Integer dataSetId, Integer widgetId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.fieldName = :fieldName and d.dataSetId.id = :id and ( d.widgetId IS NULL or d.widgetId.id = :widgetId) and (d.userId IS NULL or d.userId.id = :userId)";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", dataSetId);
        query.setParameter("userId", userId);
        query.setParameter("fieldName", fieldName);
        query.setParameter("widgetId", widgetId);
        List<DataSetColumns> list = query.list();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<DataSetColumns> getDataSetColumn(Integer dataSetId, Integer widgetId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.dataSetId.id = :id and d.widgetId.id = :widgetId ";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", dataSetId);
        query.setParameter("widgetId", widgetId);
        List<DataSetColumns> list = query.list();
        return list;
    }

    public List<DataSetColumns> getDataSetColumnOfAll(Integer dataSetId, Integer widgetId) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.dataSetId.id = :id and (d.widgetId is null or d.widgetId.id = :widgetId) ";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", dataSetId);
        query.setParameter("widgetId", widgetId);
        List<DataSetColumns> list = query.list();
        return list;
    }

    public DataSetColumns createDataSetColumn(ColumnDef columnDef, Integer dataSetId, Integer userId, Integer widgetId) {
        DataSetColumns dataSetColumn = new DataSetColumns();
        dataSetColumn.setFieldName(columnDef.getFieldName());
        dataSetColumn.setFieldType(columnDef.getFieldType());
        dataSetColumn.setExpression(columnDef.getExpression());
        dataSetColumn.setDataSetId(getDataSetById(dataSetId));
        dataSetColumn.setWidgetId(getWidgetById(widgetId));
        dataSetColumn.setUserId(findUserById(userId));
        dataSetColumn.setDisplayName(columnDef.getDisplayName());
        dataSetColumn.setDisplayFormat(columnDef.getDisplayFormat());
        return (DataSetColumns) create(dataSetColumn);
    }

    public VbUser findUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        VbUser user = (VbUser) sessionFactory.getCurrentSession().get(VbUser.class, userId);
        return user;
    }

    public List<TemplateTabs> getTabByTemplateId(Integer templateId) {
        String queryStr = "SELECT d FROM TemplateTabs d where d.templateId.id = :templateId order by d.tabId.tabOrder";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("templateId", templateId);
        return query.list();
    }

    public List<DashboardTemplate> getDefaultTemplate(Integer agencyId) {
        String queryStr = "SELECT d FROM DashboardTemplate d where d.agencyId.id = :agencyId and d.shared = 'Active' and (d.status is null or d.status !='Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        return query.list();
    }

    public UserPreferences getChartColorByUserId(VbUser userId) {
        String queryStr = "SELECT u FROM UserPreferences u WHERE u.userId = :userId and u.optionName = 'Chart_Color_Options'";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", userId);
        List<UserPreferences> userPreferencesList = query.list();
        if (userPreferencesList != null && !userPreferencesList.isEmpty()) {
            return userPreferencesList.get(0);
        }
        return null;
    }

    public DashboardTemplate getDashboardTemplateById(Integer templateId) {
        DashboardTemplate dashboardTemplate = (DashboardTemplate) sessionFactory.getCurrentSession().get(DashboardTemplate.class, templateId);
        return dashboardTemplate;
    }

    public void deleteTemplateTabs(Integer templateId) {
        String queryStr = "delete FROM TemplateTabs d where d.templateId.id = :templateId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("templateId", templateId);
        query.executeUpdate();
    }

    public List<DashboardTemplate> getTemplates(VbUser user, Agency agency, AgencyProduct agencyProduct) {
        String queryStr = "SELECT d from DashboardTemplate d where d.agencyId = :agency and d.agencyProductId = :agencyProduct and ( d.userId is null or d.userId = :userId or d.shared = 'Active') and (d.status IS NULL or d.status != 'Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyProduct", agencyProduct);
        query.setParameter("agency", agency);
        query.setParameter("userId", user);
        return query.list();
    }

    public void deleteTabFromTemplate(Integer id) {
        String queryStr = "DELETE from TemplateTabs t where t.tabId.id = :id";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public List<DashboardTemplate> getUserTemplate(VbUser user) {
        String queryStr = "SELECT d from DashboardTemplate d where d.userId = :userId and (d.status is null or d.status !='Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", user);
        return query.list();
    }

    public DashboardTemplate checkTemplateIsShared(Integer templateId) {
        String queryStr = "SELECT d from DashboardTemplate d where d.shared = 'Active' and d.id = :templateId and (d.status is null or d.status !='Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("templateId", templateId);
        return (DashboardTemplate) query.uniqueResult();
    }

    public void deleteUserTemplate(Integer templateId) {
        DashboardTemplate dashboardTemplate = checkTemplateIsShared(templateId);
        System.out.println("dashboardTemplate " + dashboardTemplate);
        if (dashboardTemplate == null) {
            String queryStr = "UPDATE DashboardTemplate t set status = 'Deleted' where t.id = :templateId";
            Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
            query.setParameter("templateId", templateId);
            query.executeUpdate();
        }
    }

    public DataSetColumns getDataSetColumn(String fieldName, DataSet dataSet) {
        String queryStr = "SELECT d FROM DataSetColumns d where d.fieldName = :fieldName and d.dataSetId = :dataSet";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("fieldName", fieldName);
        query.setParameter("dataSet", dataSet);
        List<DataSetColumns> list = query.list();
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public UserPreferences getThemeByUserId(VbUser user) {
        String queryStr = "SELECT u FROM UserPreferences u where u.userId = :user and u.optionName != 'Chart_Color_Options'";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("user", user);
        return (UserPreferences) query.uniqueResult();
    }

    public List<DataSourceSetting> getDataSourceSettings(String dataSource, String dataSetReportName) {
        String queryStr = "SELECT d FROM DataSourceSetting d where d.dataSourceName = :dataSourceName and d.reportName = :reportName";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceName", dataSource);
        query.setParameter("reportName", dataSetReportName);
        List<DataSourceSetting> list = query.list();
        return list;
    }

    public List<DataSourceFilter> getDataSourceFilters(String dataSource, String dataSetReportName) {
        String queryStr = "SELECT d FROM DataSourceFilter d where d.dataSourceName = :dataSourceName and d.reportName = :reportName";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceName", dataSource);
        query.setParameter("reportName", dataSetReportName);
        List<DataSourceFilter> list = query.list();
        return list;
    }

    public List getDataSetReport(String dataSourceName) {
        String queryStr = "SELECT distinct d.reportName FROM DataSourceSetting d where d.dataSourceName = :dataSourceName";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceName", dataSourceName);
        return query.list();
    }

    public List getDataSetReportSegments(String dataSourceName, String reportName, String segmentName) {
        String queryStr = "SELECT distinct d. FROM DataSourceSetting d where d.dataSourceName = :dataSourceName";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("dataSourceName", dataSourceName);
        return query.list();
    }

    public void deleteDerivedColumn(String fieldName, Integer widgetId) {
        String queryStr = "DELETE from DataSetColumns d where d.fieldName = :fieldName and d.widgetId.id = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("fieldName", fieldName);
        query.setParameter("widgetId", widgetId);
        query.executeUpdate();
    }

    public void deleteWidgetColumn(String fieldName, Integer widgetId) {
        String queryStr = "DELETE from WidgetColumn w where w.fieldName = :fieldName and w.widgetId.id = :widgetId";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("fieldName", fieldName);
        query.setParameter("widgetId", widgetId);
        query.executeUpdate();
    }

    public UserPreferences getChartColorById(Integer userId) {
        String queryStr = "SELECT u FROM UserPreferences u WHERE u.userId.id = :userId and u.optionName = 'Chart_Color_Options'";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("userId", userId);
        List<UserPreferences> userPreferencesList = query.list();
        if (userPreferencesList != null && !userPreferencesList.isEmpty()) {
            return userPreferencesList.get(0);
        }
        return null;
    }

    public List<DashboardTemplate> getTemplates(Integer userId, Integer agencyId, AgencyProduct agencyProduct) {
        String queryStr = "SELECT d from DashboardTemplate d where d.agencyId.id = :agencyId and d.agencyProductId = :agencyProduct and ( d.userId.id is null or d.userId.id = :userId or d.shared = 'Active') and (d.status IS NULL or d.status != 'Deleted')";
        Query query = sessionFactory.getCurrentSession().createQuery(queryStr);
        query.setParameter("agencyProduct", agencyProduct);
        query.setParameter("agencyId", agencyId);
        query.setParameter("userId", userId);
        return query.list();
    }
}
