package br.lsdi.digialphenotyping.virtualsensors;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.List;

public class SMSSensor {
    //Construtor
    public SMSSensor() {
        SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver();
    }

//    public boolean isAppRunning(Context context, String packageName) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
//        if (procInfos != null) {
//            for (ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
//                if (processInfo.processName.equals(packageName)) {
//                    return true;
//                }
//                System.out.println("######################### Processos em execução: ");
//                System.out.println(processInfo.processName);
//                System.out.println(processInfo);
//                System.out.println(procInfos);
//            }
//        }
//        return false;
//    }
}
