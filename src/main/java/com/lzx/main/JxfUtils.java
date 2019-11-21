package com.lzx.main;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.httpclient.HttpClient;

import java.io.*;
import java.util.*;

/**
 * @author 1Zx.
 * @data 2019/11/21 9:45
 */
public final class JxfUtils {

    public static void createData(String filePath,String idFileName) {
        //        final String filePath = args[0];
//
//        final String idFileName = args[1];
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
                System.out.println(idFileName + " not find");
            } catch (IOException e) {
                System.out.println("readTxtDataError");
            }
            Map<String, StringBuffer> outData = new LinkedHashMap<>();
            StringBuffer sendEamilData = new StringBuffer();
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
                        sendEamilData.append(data);
                    }
                }
            }
            if (JxfApp.properties.getProperty(Constants.EMAILTYPE).equalsIgnoreCase("QQ")) {
                SendEmailByQQ sendEmailByQQ = new SendEmailByQQ();
                sendEmailByQQ.setContent(sendEamilData.toString());
                sendEmailByQQ.setAuthorizationCode(JxfApp.properties.getProperty(Constants.AuthorizationCode));
                sendEmailByQQ.setProtocol(JxfApp.properties.getProperty(Constants.EMAILPROTOCOL));
                sendEmailByQQ.setHost(JxfApp.properties.getProperty(Constants.EMAILHOST));
                sendEmailByQQ.setAuth(JxfApp.properties.getProperty(Constants.EMAILAUTH));
                sendEmailByQQ.setPort(Integer.valueOf(JxfApp.properties.getProperty(Constants.EMAILPORT)));
                sendEmailByQQ.setSslEnable(JxfApp.properties.getProperty(Constants.EMAILSSLENABLE));
                sendEmailByQQ.setDebug(JxfApp.properties.getProperty(Constants.EMAILDEBUG));
                sendEmailByQQ.setReceiveEmail(JxfApp.properties.getProperty(Constants.EMAILRECEIVEURL));
                sendEmailByQQ.setFromEmail(JxfApp.properties.getProperty(Constants.EMAILFROMURL));
                new Thread(sendEmailByQQ).run();
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

    public static String getRequest(String url) {
        // 输入流
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        // 创建httpClient实例
        HttpClient httpClient = new HttpClient();
        // 设置http连接主机服务超时时间：15000毫秒
        // 先获取连接管理器对象，再获取参数对象,再进行参数的赋值
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建一个Get方法实例对象
        GetMethod getMethod = new GetMethod(url);
        // 设置get请求超时为60000毫秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        // 设置请求重试机制，默认重试次数：3次，参数设置为true，重试机制可用，false相反
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
        try {
            // 执行Get方法
            int statusCode = httpClient.executeMethod(getMethod);
            // 判断返回码
            if (statusCode != HttpStatus.SC_OK) {
                // 如果状态码返回的不是ok,说明失败了,打印错误信息
                System.err.println("Method faild: " + getMethod.getStatusLine());
            } else {
                // 通过getMethod实例，获取远程的一个输入流
                is = getMethod.getResponseBodyAsStream();
                // 包装输入流
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuffer sbf = new StringBuffer();
                // 读取封装的输入流
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp).append("\r\n");
                }

                result = sbf.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放连接
            getMethod.releaseConnection();
        }
        return result;
    }
}
