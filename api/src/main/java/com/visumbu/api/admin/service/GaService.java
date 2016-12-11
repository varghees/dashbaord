/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.api.admin.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import com.visumbu.api.utils.DateUtils;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Varghees
 */
@Service("gaService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GaService {

    private static final String CLIENT_SECRET_JSON_RESOURCE = "F:\\GaToken\\client_secret_384381056232-sqrgb2u8j26gbkqi6dis682ojapsf85a.apps.googleusercontent.com.json";

    // Replace with your view ID.
    private static final String VIEW_ID = "82176546";

    // The directory where the user's credentials will be stored.
    private static final File DATA_STORE_DIR = new File(
            System.getProperty("user.home"), "store/hello_analytics");

    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static NetHttpTransport httpTransport;
    private static FileDataStoreFactory dataStoreFactory;
    private static AnalyticsReporting analyticsReporting = initializeAnalyticsReporting();

    /**
     * Initializes an authorized Analytics Reporting service object.
     *
     * @return The analytics reporting service object.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static AnalyticsReporting initializeAnalyticsReporting() {

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

            // Load client secrets.
            /*GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
             new InputStreamReader(HelloAnalytics.class
             .getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));
             */
            // Set up authorization code flow for all authorization scopes.
            /*GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
             AnalyticsReportingScopes.all()).setDataStoreFactory(dataStoreFactory)
             .build();
             */
            GoogleAuthorizationCodeFlow flow1 = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, "384381056232-sqrgb2u8j26gbkqi6dis682ojapsf85a.apps.googleusercontent.com", "1nJygCmZKdFCOykaGmbjBpKy",
                    Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setDataStoreFactory(
                            dataStoreFactory).build();
            // Authorize.
            Credential credential = new AuthorizationCodeInstalledApp(flow1,
                    new LocalServerReceiver()).authorize("user");
            // Construct the Analytics Reporting service object.
            return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(GaService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GaService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public GetReportsResponse getGenericData(Date startDate1, Date endDate1, String metrics, String dimentions) {
        return getGenericData(startDate1, endDate1, null, null, metrics, dimentions);
    }

    public GetReportsResponse getSeoPerformance(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
         Date startDate = DateUtils.get30DaysBack();
        Date endDate = new Date();
        String metricsList = "ga:visits,Visits;ga:sessions,Sessions;ga:percentNewSessions,PercentNewSessions;"
                + "ga:newUsers,NewUsers;ga:pageViews,PageViews;ga:bounceRate,BounceRate;ga:averageTimeOnPage,AverageTimeOnPage;"
                + "ga:goalOneCompletions,GoalOneCompletions;ga:goalTwoCompletions,GoalTwoCompletions;ga:goalThreeCompletions,GoalThreeCompletions;"
                + "ga:goalFourCompletions,GoalFourCompletions;ga:goalFiveCompletions,GoalFiveCompletions;ga:goalSixCompletions,GoalSixCompletions;";
        String dimensions = "ga:medium";
        String filter = "ga:medium==organic";
        return getGenericData(startDate1, endDate1,  startDate2,  endDate2, metricsList, dimensions, filter);
    }
    
    
    public GetReportsResponse getGenericData(Date startDate1, Date endDate1, Date startDate2, Date endDate2, String metrics, String dimentions, String filter) {
        try {
            List<DateRange> dateRangeList = new ArrayList<>();
            DateRange dateRange = new DateRange();
            dateRange.setStartDate(DateUtils.getGaStartDate(startDate1));
            dateRange.setEndDate(DateUtils.getGaEndDate(endDate1));
            dateRangeList.add(dateRange);

            if (startDate2 != null) {
                DateRange dateRange1 = new DateRange();
                dateRange1.setStartDate(DateUtils.getGaStartDate(startDate2));
                if (endDate2 != null) {
                    dateRange1.setEndDate(DateUtils.getGaEndDate(endDate2));
                } else {
                    dateRange1.setEndDate(DateUtils.getGaEndDate(startDate1));
                }
                dateRangeList.add(dateRange1);
            }
            String[] metricsArray = metrics.split(";");
            List<Metric> metricList = new ArrayList<>();
            for (int i = 0; i < metricsArray.length; i++) {
                String metricStr = metricsArray[i];
                String[] nameAliasArray = metricStr.split(",");
                if (nameAliasArray.length >= 2) {
                    Metric metric = new Metric()
                            .setExpression(nameAliasArray[0])
                            .setAlias(nameAliasArray[1]);
                    metricList.add(metric);
                } else if (nameAliasArray.length >= 1) {
                    Metric metric = new Metric()
                            .setExpression(nameAliasArray[0]);
                    metricList.add(metric);
                }
            }

            String[] dimensionArray = dimentions.split(";");
            List<Dimension> dimensionList = new ArrayList<>();
            for (int i = 0; i < dimensionArray.length; i++) {
                String dimensionStr = dimensionArray[i];
                Dimension dimension = new Dimension()
                        .setName(dimensionStr);
                dimensionList.add(dimension);

            }

            ReportRequest request = new ReportRequest()
                    .setViewId(VIEW_ID)
                    .setDateRanges(dateRangeList)
                    .setDimensions(dimensionList)
                    .setFiltersExpression(filter)
                    .setMetrics(metricList);
            ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
            requests.add(request);
            // Create the GetReportsRequest object.
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);

            // Call the batchGet method.
            GetReportsResponse response = analyticsReporting.reports().batchGet(getReport).execute();

            // Return the response.
            return response;
        } catch (IOException ex) {
            Logger.getLogger(GaService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public GetReportsResponse getGenericData(Date startDate1, Date endDate1, Date startDate2, Date endDate2, String metrics, String dimentions) {
        try {
            List<DateRange> dateRangeList = new ArrayList<>();
            DateRange dateRange = new DateRange();
            dateRange.setStartDate(DateUtils.getGaStartDate(startDate1));
            dateRange.setEndDate(DateUtils.getGaEndDate(endDate1));
            dateRangeList.add(dateRange);

            if (startDate2 != null) {
                DateRange dateRange1 = new DateRange();
                dateRange1.setStartDate(DateUtils.getGaStartDate(startDate2));
                if (endDate2 != null) {
                    dateRange1.setEndDate(DateUtils.getGaEndDate(endDate2));
                } else {
                    dateRange1.setEndDate(DateUtils.getGaEndDate(startDate1));
                }
                dateRangeList.add(dateRange1);
            }
            String[] metricsArray = metrics.split(";");
            List<Metric> metricList = new ArrayList<>();
            for (int i = 0; i < metricsArray.length; i++) {
                String metricStr = metricsArray[i];
                String[] nameAliasArray = metricStr.split(",");
                if (nameAliasArray.length >= 2) {
                    Metric metric = new Metric()
                            .setExpression(nameAliasArray[0])
                            .setAlias(nameAliasArray[1]);
                    metricList.add(metric);
                } else if (nameAliasArray.length >= 1) {
                    Metric metric = new Metric()
                            .setExpression(nameAliasArray[0]);
                    metricList.add(metric);
                }
            }

            String[] dimensionArray = dimentions.split(";");
            List<Dimension> dimensionList = new ArrayList<>();
            for (int i = 0; i < dimensionArray.length; i++) {
                String dimensionStr = dimensionArray[i];
                Dimension dimension = new Dimension()
                        .setName(dimensionStr);
                dimensionList.add(dimension);

            }

            ReportRequest request = new ReportRequest()
                    .setViewId(VIEW_ID)
                    .setDateRanges(dateRangeList)
                    .setDimensions(dimensionList)
                    .setMetrics(metricList);
            ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
            requests.add(request);
            // Create the GetReportsRequest object.
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);

            // Call the batchGet method.
            GetReportsResponse response = analyticsReporting.reports().batchGet(getReport).execute();

            // Return the response.
            return response;
        } catch (IOException ex) {
            Logger.getLogger(GaService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Query the Analytics Reporting API V4. Constructs a request for the
     * sessions for the past seven days. Returns the API response.
     *
     * @param service
     * @return GetReportResponse
     * @throws IOException
     */
    private static GetReportsResponse getReport(AnalyticsReporting service) throws IOException {
        // Create the DateRange object.
        List<DateRange> dateRangeList = new ArrayList<>();
// The start date for the query in the format `YYYY-MM-DD`.
        DateRange dateRange = new DateRange();
        dateRange.setStartDate("7DaysAgo");
        dateRange.setEndDate("today");

        dateRangeList.add(dateRange);
        DateRange dateRange1 = new DateRange();
        dateRange1.setStartDate("14DaysAgo");
        dateRange1.setEndDate("7DaysAgo");
        dateRangeList.add(dateRange1);
        // Create the Metrics object.
        Metric sessions = new Metric()
                .setExpression("ga:sessions")
                .setAlias("sessions");

        //Create the Dimensions object.
        Dimension browser = new Dimension()
                .setName("ga:browser");

        // Create the ReportRequest object.
        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(dateRangeList)
                .setDimensions(Arrays.asList(browser))
                .setMetrics(Arrays.asList(sessions));

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = service.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }

    public static void main(String[] args) {
        try {
            AnalyticsReporting service = initializeAnalyticsReporting();

            GetReportsResponse response = getReport(service);
            printResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses and prints the Analytics Reporting API V4 response.
     *
     * @param response the Analytics Reporting API V4 response.
     */
    private static void printResponse(GetReportsResponse response) {

        for (Report report : response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows == null) {
                System.out.println("No data found for " + VIEW_ID);
                return;
            }

            for (ReportRow row : rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();
                for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    System.out.println(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }

                for (int j = 0; j < metrics.size(); j++) {
                    System.out.print("Date Range (" + j + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                        System.out.println(metricHeaders.get(k).getName() + ": " + values.getValues().get(k));
                    }
                }
            }
        }
    }
}