/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.service;

import com.visumbu.vb.utils.DateUtils;
import com.visumbu.vb.utils.Rest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author lino
 */
@Service("googleMyBusinessService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GoogleMyBusinessService {

    private static HashMap<String, String> locationListArray;

    private static String gmbAccessToken;

    public List<Map<String, Object>> get(String dataSetReportName, String gmbRefreshToken, String gmbAccountId, String clientId, String clientSecret,
            Date startDate, Date endDate, String timeSegment, String productSegment) {
        gmbAccessToken = getAccessToken(gmbAccountId, gmbRefreshToken, clientId, clientSecret);
        if ("reportInsights".equalsIgnoreCase(dataSetReportName)) {
            return getInsightsReport(gmbRefreshToken, gmbAccountId, clientId, clientSecret, startDate, endDate);
        } else {
            return getPhoneCalls(gmbRefreshToken, gmbAccountId, clientId, clientSecret, startDate, endDate, timeSegment);
        }
    }

    private String getAccessToken(String gmbAccountId, String gmbRefreshToken, String clientId, String clientSecret) {
        try {
            String url = "https://www.googleapis.com/oauth2/v4/token?refresh_token=" + gmbRefreshToken + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=refresh_token";
            String tokenData = Rest.postRawForm(url, "{}");
            JSONParser parser = new JSONParser();
            Object jsonObj = parser.parse(tokenData);
            JSONObject array = (JSONObject) jsonObj;
            String accessToken = (String) array.get("access_token");
            return accessToken;
        } catch (ParseException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error----->" + ex);
        }
        return null;
    }

    public void getLocations(String gmbRefreshToken, String gmbAccountId, String gmbClientId, String gmbClientSecret) {
        JSONObject array = null;
        try {
            ArrayList<String> locationList = new ArrayList();
            gmbAccessToken = getAccessToken(gmbAccountId, gmbRefreshToken, gmbClientId, gmbClientSecret);
            String url = "https://mybusiness.googleapis.com/v4/accounts/" + gmbAccountId + "/locations?access_token=" + gmbAccessToken;
            String locationData = Rest.getData(url);
            JSONParser parser = new JSONParser();
            Object jsonObj = parser.parse(locationData);
            array = (JSONObject) jsonObj;
            List<Map<String, Object>> LocationsArray = (List<Map<String, Object>>) array.get("locations");
            HashMap<String, String> locationMap = new HashMap<>();
            for (Object singleLocation : LocationsArray) {
                Map<String, Object> locationObject = (Map<String, Object>) singleLocation;
                Map<String, String> locationAddress = (Map<String, String>) locationObject.get("address");
                String locationName = "\"" + (String) locationObject.get("name") + "\"";
                locationMap.put(locationName, locationAddress.get("postalCode"));
            }
            locationListArray = locationMap;

        } catch (ParseException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Map<String, Object>> getPhoneCalls(String gmbRefreshToken, String gmbAccountId, String gmbClientId, String gmbClientSecret, Date startDate, Date endDate, String timeSegment) {
        try {
            String url = "https://mybusiness.googleapis.com/v4/accounts/" + gmbAccountId + "/locations:reportInsights?access_token=" + gmbAccessToken;
            System.out.println("url--->" + url);
            List<Map<String, Object>> returnMap = new ArrayList<>();
            String startDateStr = DateUtils.dateToString(startDate, "YYYY-MM-dd");
            String endDateStr = DateUtils.dateToString(endDate, "YYYY-MM-dd");
            String requestBody = ",\"basicRequest\":{\"metricRequests\":{\"metric\":\"ACTIONS_PHONE\",\"options\":[\"" + timeSegment + "\"]},\"timeRange\":{\"startTime\":\"" + startDateStr + "T00:00:00.001Z\",\"endTime\":\"" + endDateStr + "T23:59:00.000Z\"}}}";
            String responseString = getAppendedResponse(requestBody, url);
            JSONParser parser = new JSONParser();
            Object jsonObject = parser.parse(responseString);
            JSONObject array = (JSONObject) jsonObject;
            Map<String, Object> arrangeData = new HashMap<>();
            List<Map<String, Object>> locationMetrics = (List<Map<String, Object>>) array.get("locationMetrics");
            if ("BREAKDOWN_DAY_OF_WEEK".equals(timeSegment)) {
                int mondayCalls = 0, tuesdayCalls = 0, wednesdayCalls = 0, thursdayCalls = 0, fridayCalls = 0, saturdayCalls = 0, sundayCalls = 0;
                for (Map<String, Object> location : locationMetrics) {
                    List<Map<String, Object>> MatricValues = (List<Map<String, Object>>) location.get("metricValues");
                    Map<String, Object> metricValue = MatricValues.get(0);
                    System.out.println("metricValue----------->" + metricValue);
                    if (metricValue.size() != 1) {
                        List<Map<String, Object>> dimensionalValues = (List<Map<String, Object>>) metricValue.get("dimensionalValues");
                        for (Map<String, Object> dimensionalValue : dimensionalValues) {
                            System.out.println("diemnsionValue------------>" + dimensionalValue);
                            Map<String, String> metricName = (Map<String, String>) dimensionalValue.get("timeDimension");
                            if ("MONDAY".equals(metricName.get("dayOfWeek"))) {
                                mondayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("TUESDAY".equals(metricName.get("dayOfWeek"))) {
                                tuesdayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("WEDNESDAY".equals(metricName.get("dayOfWeek"))) {
                                wednesdayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("THURSDAY".equals(metricName.get("dayOfWeek"))) {
                                thursdayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("FRIDAY".equals(metricName.get("dayOfWeek"))) {
                                fridayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("SATURDAY".equals(metricName.get("dayOfWeek"))) {
                                saturdayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            } else if ("SUNDAY".equals(metricName.get("dayOfWeek"))) {
                                sundayCalls = +Integer.parseInt((String) dimensionalValue.get("value"));
                            }
                        }
                    }
                }
                arrangeData.put("MONDAY", mondayCalls);
                arrangeData.put("TUESDAY", tuesdayCalls);
                arrangeData.put("WEDNESDAY", wednesdayCalls);
                arrangeData.put("THURSDAY", thursdayCalls);
                arrangeData.put("FRIDAY", fridayCalls);
                arrangeData.put("SATURDAY", saturdayCalls);
                arrangeData.put("SUNDAY", sundayCalls);
                for (Map.Entry<String, Object> entry : arrangeData.entrySet()) {
                    Map<String, Object> callData = new HashMap<>();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    callData.put("day", key);
                    callData.put("noOfCalls", value);
                    returnMap.add(callData);
                }
            } else {
                for (Map<String, Object> location : locationMetrics) {
                    List<Map<String, Object>> MatricValues = (List<Map<String, Object>>) location.get("metricValues");
                    Map<String, Object> metricValue = MatricValues.get(0);
                    System.out.println("metricValue----------->" + metricValue);
                    if (metricValue.size() != 1) {
                        List<Map<String, Object>> dimensionalValues = (List<Map<String, Object>>) metricValue.get("dimensionalValues");
                        for (Map<String, Object> dimensionalValue : dimensionalValues) {
                            System.out.println("diemnsionValue------------>" + dimensionalValue);
                            Map<String, Object> metricName = (Map<String, Object>) dimensionalValue.get("timeDimension");
                            System.out.println("testmetrics---------->"+metricName);
                            Map<String, Object> hourValue = (Map<String, Object>) metricName.get("timeOfDay");
                            Long hour = (Long)hourValue.get("hours");
                            int intHour = hour.intValue();
                            String stringValue = (String) dimensionalValue.get("value");
                            int intValue = (int) Integer.parseInt(stringValue);
                            System.out.println("testvalue---------->");
                            for (int i = 1; i < 25; i++) {
                                System.out.println("intvalue---------->"+i);
                                if (intHour == i) {
                                    String key = Integer.toString(i);
                                    if (arrangeData.containsKey(key)) {
                                        arrangeData.replace(key, (int) arrangeData.get(key) + intValue);
                                    } else {
                                        arrangeData.put(key, intValue);
                                    }
                                }
                            }
                        }
                    }
                }
                for (Map.Entry<String, Object> entry : arrangeData.entrySet()) {
                    Map<String, Object> callData = new HashMap<>();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    callData.put("Hour", key);
                    callData.put("noOfCalls", value);
                    returnMap.add(callData);
                }
            }
            System.out.println("returnMap ------------>" + returnMap);
            return returnMap;
        } catch (ParseException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<Map<String, Object>> getInsightsReport(String gmbRefreshToken, String gmbAccountId, String gmbClientId, String gmbClientSecret, Date startDate, Date endDate) {
        try {
            System.out.println("locationlist data for every call------->" + locationListArray.toString());
            String url = "https://mybusiness.googleapis.com/v4/accounts/" + gmbAccountId + "/locations:reportInsights?access_token=" + gmbAccessToken;
            System.out.println("url--->" + url);
            List<Map<String, Object>> returnMap = new ArrayList<>();
            String startDateStr = DateUtils.dateToString(startDate, "YYYY-MM-dd");
            String endDateStr = DateUtils.dateToString(endDate, "YYYY-MM-dd");
            String requestBody = ",\"basicRequest\":{\"metricRequests\":{\"metric\":\"ALL\",\"options\":[\"AGGREGATED_TOTAL\"]},\"timeRange\":{\"startTime\":\"" + startDateStr + "T00:00:00.001Z\",\"endTime\":\"" + endDateStr + "T23:59:00.000Z\"}}}";
            String responseString = getAppendedResponse(requestBody, url);
            JSONParser parser = new JSONParser();
            Object jsonObject = parser.parse(responseString);
            JSONObject array = (JSONObject) jsonObject;
            List<Map<String, Object>> locationMetrics = (List<Map<String, Object>>) array.get("locationMetrics");
            for (Map<String, Object> location : locationMetrics) {
                Map<String, Object> data = new HashMap<>();
                List<Map<String, Object>> MatricValues = (List<Map<String, Object>>) location.get("metricValues");
                for (Map<String, Object> metrics : MatricValues) {
                    String metricName = (String) metrics.get("metric");
                    if (metricName != null) {
                        Map<String, Object> value = (Map<String, Object>) metrics.get("totalValue");
                        String metricValue = (String) value.get("value");
                        data.put(metricName, metricValue);
                    }
                }
                data.put("location", locationListArray.get("\"" + location.get("locationName") + "\""));
                returnMap.add(data);
            }
            return returnMap;
        } catch (ParseException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String getAppendedResponse(String requestBody, String url) {
        List<String> locations = new ArrayList<>();
        StringBuilder responseString = new StringBuilder();
        for (Map.Entry<String, String> entry : locationListArray.entrySet()) {
            String key = entry.getKey();
            locations.add(key);
        }
        int listSize = locations.size();
        for (int i = 0; i < listSize;) {
            StringBuilder locationString = new StringBuilder();
            if (listSize < 10) {
                locationString.append(locations.toString());
            } else {
                List<String> limitedLocations = new ArrayList<>();
                limitedLocations = locations.subList(i, i + 9);
                locationString.append(limitedLocations.toString());
            }
            String payload = "{\"locationNames\":" + locationString.toString() + requestBody;
            System.out.println("payload----------->" + payload);
            responseString.append(getResponse(url, null, payload));
            i = i + 9;
        }
        System.out.println("responseString-------->" + responseString.toString());
        return responseString.toString();
    }

    public String getResponse(String url, String authorizationHeaders, String params) {
        StringEntity postEntity = new StringEntity(params,
                ContentType.APPLICATION_JSON);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            post.setEntity(postEntity);
            response = httpClient.execute(post);
            String responseJSON = EntityUtils.toString(response.getEntity());
            EntityUtils.consume(response.getEntity());
            System.out.println("entity------------>" + responseJSON);
            return responseJSON;
        } catch (IOException | org.apache.http.ParseException ex) {
            Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.close();
            } catch (IOException ex) {
                Logger.getLogger(GoogleMyBusinessService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

}
