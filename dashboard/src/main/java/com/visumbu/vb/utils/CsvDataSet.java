/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visumbu.vb.utils;

import com.visumbu.vb.bean.ColumnDef;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvDataSet {

    public static List<Map<String,Object>> CsvDataSet(String filename) throws FileNotFoundException, IOException {

        //Create the CSVFormat object
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        //initialize the CSVParser object
        CSVParser parser = new CSVParser(new FileReader(filename), format);
        Map<String, Integer> headerMap = parser.getHeaderMap();
        System.out.println("Heder map data");
        System.out.println(headerMap);
        List<ColumnDef> columnDefs = new ArrayList<>();
        for (Map.Entry<String, Integer> entrySet : headerMap.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            ColumnDef columnDef = new ColumnDef(key, "string", key);
            columnDefs.add(columnDef);

        }
        Map returnMap = new HashMap();
        returnMap.put("columnDefs", columnDefs);
        List<Map<String, Object>> data = new ArrayList<>();
        System.out.println(headerMap);
        for (CSVRecord record : parser) {
            Map<String, Object> dataMap = new HashMap<>();
            for (Map.Entry<String, Integer> entrySet : headerMap.entrySet()) {
                String key = entrySet.getKey();
                dataMap.put(key, record.get(key));
            }
            data.add(dataMap);
        }
        
        returnMap.put("data", data);
        //close the parser
        parser.close();
        return data;
    }
   
    public static void main(String[] argv) {
        try {
            System.out.println(CsvDataSet("/tmp/data.csv"));
        } catch (IOException ex) {
            Logger.getLogger(CsvDataSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
