package com.sfkrl.apperflypushevent;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CSVToJSon {
    public static String removeJsonFromString(String input) {
        String regex = "\"\\{.*?\\}\""; // 匹配大括号中的内容
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        String s = matcher.replaceAll(""); // 将匹配到的内容替换为空字符串
        s = s.replace("}", "").replace("{", "").replace("\"" , "");
        return s;
    }

    private List<String> stringToList(String s, String sep) {

        if (s == null) {
            return null;
        }
        s = removeJsonFromString(s);
        String[] parts = s.split(sep);
        if (parts.length < 58) {
            String[] splitlength = new String[58];
            System.arraycopy(parts, 0, splitlength, 0, parts.length);
            System.out.println(splitlength.length + "," + s);
            return Arrays.asList(splitlength);
        }

//        for (int i = 0; i < split.length; i++) {
//            System.out.println(replacedString);
//        }
        return Arrays.asList(parts);
    }

    private String stringToJson(List<String> header, List<String> lineData) throws Exception {

        if (header == null || lineData == null) {
            throw new Exception("输入不能为null。");
        } else if (header.size() != lineData.size()) {
            throw new Exception("不相等。");
        }
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("{ ");
        for (int i = 0; i < header.size(); i++) {

            String mString = String.format("\"%s\": \"%s\"", header.get(i), lineData.get(i));
            sBuilder.append(mString);
            if (i != header.size() - 1) {
                sBuilder.append(", ");
            }
        }
        sBuilder.append(" },");
        return sBuilder.toString();
    }

    //获取行数
    public int getTotalLines(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        FileReader in = new FileReader(file);

        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        long endTime = System.currentTimeMillis();

        System.out.println("统计文件行数运行时间： " + (endTime - startTime) + "ms");
        return lines;
    }

    public void ConvertToJson(InputStream filePath, OutputStream outPutPath, int count) throws Exception {
        InputStreamReader isr = new InputStreamReader(filePath, "utf-8");
        BufferedReader reader = new BufferedReader(isr);
        OutputStreamWriter osw = new OutputStreamWriter(outPutPath, "utf-8");
        BufferedWriter writer = new BufferedWriter(osw);
        int counts = --count;
        try {

            String sep = ",";
            //将csv表格第一行构建成string
            String headerStr = reader.readLine();
            if (headerStr.trim().isEmpty()) {
                System.out.println("表格头不能为空");
                return;
            }
            // 进行切割输出成List
            List<String> header = stringToList(headerStr, sep);
            String line;
            int lineCnt = 0;
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            while ((line = reader.readLine()) != null) {
                lineCnt++;
                List<String> lineData = stringToList(line, sep);
                String jsonStr = stringToJson(header, lineData);
                if (counts == lineCnt) {
                    jsonStr = jsonStr.replaceAll(".$", "");
                }
                builder.append(jsonStr);

            }
//            builder.delete(builder.length()-1 , builder.length());
            builder.append("]");
            System.out.println(builder.toString());
            JsonElement element = new JsonParser().parse(builder.toString());
            JsonArray array = element.getAsJsonArray();
            JsonObject jsonObjectData = new JsonObject();
            JsonArray jsonArrayData = new JsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject dataItem = new JsonObject();
                JsonObject elementItem = array.get(i).getAsJsonObject();
                dataItem.add("installTime", elementItem.get("Install Time"));
                dataItem.add("installPackageName", elementItem.get("App ID"));
                dataItem.add("ip", elementItem.get("IP"));
                dataItem.add("countryCode", elementItem.get("Country Code"));
                dataItem.addProperty("androidId", "616161616161");
                dataItem.add("advertisingId", elementItem.get("Advertising ID"));
                dataItem.add("language", elementItem.get("Language"));
                dataItem.add("osVersion", elementItem.get("OS Version"));
                dataItem.add("deviceMode", elementItem.get("Device Model"));
                jsonArrayData.add(dataItem);
            }
            jsonObjectData.add("datas", new JsonParser().parse(jsonArrayData.toString()));
            writer.write(jsonObjectData.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}