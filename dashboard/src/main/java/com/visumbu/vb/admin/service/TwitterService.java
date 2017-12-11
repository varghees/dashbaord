/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.admin.service;

import com.visumbu.vb.utils.DateUtils;
import static com.visumbu.vb.utils.DateUtils.dateToTimeStamp;
//import com.visumbu.vb.utils.ExampleConfig;
import com.visumbu.vb.utils.Rest;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.MultiValueMap;
import test.Twitter;

/**
 *
 * @author deldot
 */
@Service("twitterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TwitterService {

    public static void main(String[] args) {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("oauth_consumer_key", "3oaqt0QK31AGCAzHlX3c9xPbL");
        properties.put("consumerSecret", "AuVaSXpGPJWLYMCzDiera1avN8MfChN2mGysCmWeDXPzVlDVZC");
    }
    public List<Map<String, Object>> get(String reportName, Map<String, String> properties,
            Date startDate, Date endDate, String timeSegment, String productSegment) {
        if (reportName.equalsIgnoreCase("pagePerformance")) {
            return getPagePerformanceReport(properties, startDate, endDate, timeSegment, productSegment);
        }

        if (reportName.equalsIgnoreCase("screenName")) {
            return getScreenName(properties);
        }

        if (reportName.equalsIgnoreCase("userTimeLine")) {
            return getUserTimeLineMetrics(properties);
        }
        return null;
    }

    private static List<Map<String, Object>> getScreenName(Map<String, String> properties) {

//      String url = "https://api.twitter.com/1.1/account/settings.json?oauth_consumer_key=DC0sePOBbQ8bYdC8r4Smg&oauth_token=780021988039335936-vO7cttPuJ84WByUjEGFySScV1BVGsW5&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1506527941&oauth_nonce=-400194625&oauth_version=1.0&oauth_signature=qDZoWATd7XwCuXuMNzNxZHPGAog%3D";
        properties.put("baseUrl", "https://api.twitter.com/1.1/account/settings.json");
        properties.put("oauth_signature_method", "HMAC-SHA1");
        properties.put("oauth_version", "1.0");
        properties.put("httpMethod", "GET");
        Twitter.getAuthentionData(properties, "");
        String url = "https://api.twitter.com/1.1/account/settings.json?"
                + "oauth_consumer_key=" + properties.get("oauth_consumer_key")
                + "&oauth_token_key=" + properties.get("oauth_token")
                + "&oauth_version=1.0"
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_timestamp=" + properties.get("oauth_timestamp")
                + "&oauth_nonce=" + properties.get("oauth_nonce")
                + "&oauth_signature=" + properties.get("oauth_signature");

        System.out.println("page performance url is -->" + url);

        MultiValueMap<String, String> valueMap = null;
        String data = Rest.getData(url, valueMap);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = (JSONObject) parser.parse(data);
        } catch (ParseException ex) {
            Logger.getLogger(TwitterService.class.getName()).log(Level.SEVERE, null, ex);
        }
        String screenName = jsonObject.get("screen_name") + "";
        List<Map<String, Object>> returnMap = new ArrayList<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("screen_name", screenName);
        returnMap.add(dataMap);
        return returnMap;
    }

    private List<Map<String, Object>> getPagePerformanceReport(Map<String, String> properties,
            Date startDate, Date endDate, String timeSegment, String productSegment) {
        properties.put("baseUrl", "https://api.twitter.com/1.1/users/lookup.json");
        properties.put("queryString", "&screen_name=" + properties.get("screen_name") + "&user_id=" + properties.get("user_id"));
        System.out.println("queryString-----" + properties.get("queryString"));
        properties.put("oauth_signature_method", "HMAC-SHA1");
        properties.put("oauth_version", "1.0");
        properties.put("httpMethod", "GET");

        try {
            String startDateStr = DateUtils.dateToString(startDate, "YYYY-MM-dd");
            String endDateStr = DateUtils.dateToString(endDate, "YYYY-MM-dd");
            Long timeStamp = dateToTimeStamp(endDateStr);//to be added in feature 
            Twitter.getAuthentionData(properties, "");
            String twitterUrl = "https://api.twitter.com/1.1/users/lookup.json?"
                    + "screen_name=" + properties.get("screen_name")
                    + "&user_id=" + properties.get("user_id")
                    + "&oauth_consumer_key=" + properties.get("oauth_consumer_key")
                    + "&oauth_token=" + properties.get("oauth_token")
                    + "&oauth_signature_method=HMAC-SHA1"
                    + "&oauth_timestamp=" + properties.get("oauth_timestamp")
                    + "&oauth_nonce=" + properties.get("oauth_nonce")
                    + "&oauth_version=1.0"
                    + "&oauth_signature=" + properties.get("oauth_signature");

            System.out.println("Twitter Url =====> " + twitterUrl);

            String twitterData = Rest.getData(twitterUrl);
            JSONParser parser = new JSONParser();
            Object object = parser.parse(twitterData);
            JSONArray jsonArray = (JSONArray) object;

            if (twitterUrl == null) {
                return null;
            }
            Map<String, Object> twitterObject = new HashMap<>();
            twitterObject.put("data", jsonArray);

            List<Map<String, Object>> obj = (List<Map<String, Object>>) twitterObject.get("data");
            List<Map<String, Object>> returnMap = new ArrayList();
            for (Iterator<Map<String, Object>> iterator = obj.iterator(); iterator.hasNext();) {
                Map<String, Object> next = iterator.next();
                Map twitterOrganicData = new HashMap();
                System.out.println("screen name-->" + next.get("screen_name"));
                twitterOrganicData.put("screen_name", next.get("screen_name"));
                twitterOrganicData.put("followers_count", next.get("followers_count"));
                twitterOrganicData.put("friends_count", next.get("friends_count"));
                twitterOrganicData.put("statuses_count", next.get("statuses_count"));
                twitterOrganicData.put("favourites_count", next.get("favourites_count"));
                twitterOrganicData.put("retweet_count", next.get("retweet_count"));
                twitterOrganicData.put("favorite_count", next.get("favorite_count"));
                returnMap.add(twitterOrganicData);
            };
            System.out.println("*************************** TWITTER SERVICE*******************");
            System.out.println(returnMap);
            return returnMap;
        } catch (ParseException ex) {
            Logger.getLogger(TwitterService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Map<String, Object>> getUserTimeLineMetrics(Map<String, String> properties) {
        int ownTweet = 0, retweet = 0;
        long ownTweetLikes = 0, retweetLikes = 0;
//        String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=SivanesanGovind&user_id=2526475147&oauth_consumer_key=FH0z2IiKd46IHVrftBXhyyGjY&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1506528376&oauth_nonce=EwlN94AhGNf&oauth_version=1.0&oauth_token=2526475147-SJeXiGSn6P9Fg1N4AZtACrzdANEz0y9wQ32pncu&oauth_signature=3zFPKetFk6O0Hzyo5ClCVksLW5Q%3D";
        properties.put("baseUrl", "https://api.twitter.com/1.1/statuses/user_timeline.json");
        properties.put("queryString", "&screen_name=" + properties.get("screen_name") + "&user_id=" + properties.get("user_id"));
        properties.put("oauth_signature_method", "HMAC-SHA1");
        properties.put("oauth_version", "1.0");
        properties.put("httpMethod", "GET");
        Twitter.getAuthentionData(properties, "");
        String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?"
                + "screen_name=" + properties.get("screen_name")
                + "&user_id=" + properties.get("user_id")
                + "&oauth_consumer_key=" + properties.get("oauth_consumer_key")
                + "&oauth_token=" + properties.get("oauth_token")
                + "&oauth_signature_method=HMAC-SHA1"
                + "&oauth_timestamp=" + properties.get("oauth_timestamp")
                + "&oauth_nonce=" + properties.get("oauth_nonce")
                + "&oauth_version=1.0"
                + "&oauth_signature=" + properties.get("oauth_signature");
        MultiValueMap<String, String> valueMap = null;
        String data = Rest.getData(url, valueMap);

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) parser.parse(data);
        } catch (ParseException ex) {
            Logger.getLogger(TwitterService.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
            JSONObject jsonObject = (JSONObject) iterator.next();

            if ((boolean) jsonObject.get("is_quote_status")) {
                retweet = retweet + 1;
                retweetLikes = retweetLikes + (long) jsonObject.get("favorite_count");
            } else {
                ownTweet = ownTweet + 1;
                ownTweetLikes = ownTweetLikes + (long) jsonObject.get("favorite_count");
            }
        }
        List<Map<String, Object>> returnMap = new ArrayList<>();
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("own_tweets", ownTweet);
        dataMap.put("own_tweet_likes", ownTweetLikes);
        dataMap.put("retweets", retweet);
        dataMap.put("retweet_likes", retweetLikes);
        returnMap.add(dataMap);
        return returnMap;
    }  
}
