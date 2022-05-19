package com.trendingrepositories.data;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class OfflineData
{
    public static void StoreJsondata(String content,String fileName)
    { File extStore = Environment.getExternalStorageDirectory();
        String pathdir = extStore.getAbsolutePath()+"/TrendingRepositoriesOffline/";
        String path = pathdir + fileName+".json";
        try
        {
            File dir = new File(pathdir);
            dir.mkdir();
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);
            myOutWriter.close();
            fOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String ReadJsondata(String fileName)
    {
        File extStore = Environment.getExternalStorageDirectory();
        String pathdir = extStore.getAbsolutePath()+"/TrendingRepositoriesOffline/";
        String path = pathdir + fileName+".json";
        String s = "";
        String fileContent = "";
        StringBuilder builder = new StringBuilder();
        try
        {
            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));

            while ((s = myReader.readLine()) != null)
            {
                builder.append(s);
            }
            myReader.close();
            fileContent = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}
