package com.sfkrl.apperflypushevent;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMain {

    private static String startFile = "/Users/xiao/Downloads/com.hmmov.ruyp_organic-in-app-events_2023-07-01_2023-07-26_Asia_Manila.csv";//文件或文件夹

    public static void main(String[] args) throws Exception {
        listAllFile();
    }

    public static void listAllFile() throws Exception {
        File dir = new File(startFile);
        if (!dir.isDirectory()) { //文件
            file(startFile);
        } else {
            File[] files = dir.listFiles(); //文件夹
            for (int i = 0; i < files.length; i++) {
                if (!files[i].getPath().endsWith("csv"))
                    continue;
                String path = files[i].getPath();
                file(path);
            }
        }

    }

    /**
     * 单个文件
     */
    public static void file(String path) throws Exception {
        String[] names = path.split("/");
        String name = names[names.length - 1];
        InputStream filePath = new FileInputStream(path);
        OutputStream outPutPath = new FileOutputStream("/Users/xiao/Documents/qunx/mayun/apperfly-push-event/app/src/main/assets/" + name.split("_")[0] + ".json");
        File file = new File(path);
        CSVToJSon csvToJSon = new CSVToJSon();
        int count = csvToJSon.getTotalLines(file);
        csvToJSon.ConvertToJson(filePath, outPutPath, count);
    }


}
