package br.ufma.lsdi.digitalphenotyping;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
    private static final String TAG = PropertyManager.class.getName();
    private Context context;
    private Properties properties;

    public PropertyManager(String file, Context context){
        try{
            this.context = context;
            properties = new Properties();

            AssetManager assetManager = this.context.getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);

        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.getMessage().toString());
        }
    }

    public Properties getMyProperties(){
        return properties;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value){
        properties.setProperty(key, value);
    }
}