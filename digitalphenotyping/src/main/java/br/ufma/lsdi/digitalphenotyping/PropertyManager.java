package br.ufma.lsdi.digitalphenotyping;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
    private static final String TAG = PropertyManager.class.getName();
    private Context context;
    private Properties properties;

    public PropertyManager() {}

    public PropertyManager(String file, Context context) {
        try {
            this.context = context;
            properties = new Properties();

            AssetManager assetManager = this.context.getAssets();
            InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "#### Error: " + e.getMessage().toString());
        }
    }

    public void setProperty(Context c, String key, String value) {

    }

    public static void writeFile(Context c, final String fileContents, String fileName) {
        try {
            FileWriter out = new FileWriter(new File(c.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public static String readFile(Context c, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(c.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return stringBuilder.toString();
    }

    public static File dirPath(String FolderName){
        File dir = null;

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success)
            {
                dir = null;
            }
        }
        return dir;
    }
}