package br.lsdi.digialphenotyping.appviews.ui.home;

import android.Manifest;
import androidx.lifecycle.ViewModelProviders;;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.lsdi.digialphenotyping.appviews.AppMenu;
import br.lsdi.digialphenotyping.appviews.R;
import br.lsdi.digialphenotyping.virtualsensors.SMSSensor;
import br.lsdi.digialphenotyping.virtualsensors.SmsBroadcastReceiver;
import br.lsdi.digialphenotyping.virtualsensors.VirtualSensorInterface;

public class HomeFragment extends Fragment {
    public HomeViewModel homeViewModel;
    View root;
    private SmsBroadcastReceiver smsListener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setPermissions();
        else {
            smsListener = new SmsBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            getActivity().registerReceiver(smsListener, intentFilter);
        }

        //setPermissions();
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            smsListener= new SmsBroadcastReceiver();
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            getContext().registerReceiver(smsListener, intentFilter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (getContext() != null && smsListener != null) {
                getContext().unregisterReceiver(smsListener);
                smsListener = null;
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppMenu appMenu = AppMenu.getInstance();
        appMenu.setMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppMenu appMenu = AppMenu.getInstance();
        appMenu.setMenuItem(getActivity(), item);
        return super.onOptionsItemSelected(item);
    }

    public void setPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }
}