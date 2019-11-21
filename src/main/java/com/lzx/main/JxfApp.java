package com.lzx.main;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author 1Zx.
 * @data 2019/11/19 14:59
 */
public class JxfApp {

    public static void main(String[] args) {
//        final String filePath = args[0];
//
//        final String idFileName = args[1];
        final String filePath = "H:\\jxf\\";

        final String idFileName = "id.txt";
        File path = new File(filePath);
        if (path.exists()) {
            Map<String, List<String>> txtData = new LinkedHashMap<>();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath + idFileName), "GBK"));
                String line,key = null;
                while ( null != (line = br.readLine())) {
                    line = line.trim();
                    if (!StringUtils.isNumeric(line) && StringUtils.isNotBlank(line)) {
                        txtData.put(line,new ArrayList<>());
                        key = line;
                    } else {
                        if (StringUtils.isNumeric(line) && txtData.size() > 0) {
                            txtData.get(key).add(line);
                        }
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                System.out.println(args[1] + " not find");
            } catch (IOException e) {
                System.out.println("readTxtDataError");
            }
            Map<String, StringBuffer> outData = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> entry : txtData.entrySet()) {
                StringBuffer data = new StringBuffer();
                for (int i = 1; i <= entry.getValue().size(); i++) {
                    if (!outData.containsKey(String.valueOf(i))) {
                        outData.put(String.valueOf(i),new StringBuffer());
                    }
                    try {
                        BufferedReader br =
                                new BufferedReader(new InputStreamReader(new FileInputStream(filePath + entry.getKey() + "\\" + entry.getKey()+i+".txt"),"GBK"));
                        String line,num = entry.getValue().get(i-1);
                        while ( null != (line = br.readLine())) {
                            data.append(num).append("$$").append(line).append(System.getProperty("line.separator"));
                        }
                        data.append(System.getProperty("line.separator"));
                        br.close();
                    } catch (FileNotFoundException e) {
                        //System.out.println("FileNotFoundException");
                    } catch (IOException e) {
                        System.out.println("IOException");
                    }
                    if (StringUtils.isNotBlank(data.toString())) {
                        outData.get(String.valueOf(i)).append(data);
                    }
                }
            }
            for (Map.Entry<String,StringBuffer> entry: outData.entrySet()) {
                exportTxt(entry.getValue(),filePath + UUID.randomUUID().toString() + "_" + entry.getKey());
            }
        } else {
            System.out.println("The specified directory does not exist :" + filePath);
        }
    }


    private static void exportTxt(StringBuffer data,String path) {
        File file = new File(path+".txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(data.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
