package br.lsdi.digialphenotyping.appviews.ui.chat;

import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.lsdi.digialphenotyping.appviews.R;
import br.lsdi.digialphenotyping.appviews.ui.fenotipagem.FenotipagemViewModel;
import br.lsdi.digialphenotyping.virtualsensors.SMSSensor;
import br.lsdi.digialphenotyping.virtualsensors.VirtualSensorInterface;

public class ChatFragment extends Fragment {
    private FenotipagemViewModel fenotipagemViewModel;
    //VirtualSensorInterface vsi = new SMSSensor();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fenotipagemViewModel = ViewModelProviders.of(this).get(FenotipagemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fenotipagem, container, false);
        //final TextView textView = root.findViewById(R.id.text_slideshow);

        //monitoraSensorVirtual();

        //fenotipagemViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            //@Override
            //public void onChanged(@Nullable String s) {
                //textView.setText(s);
            //}
        //});
        return root;
    }

//    // Monitora os sensores virtuais (exemplo: sms, chamadas telefonicas, twitter)
//    public void monitoraSensorVirtual() {
//        if (vsi.isAppRunning(this.getContext(), "com.twitter.android")) {
//            // App is running
//        } else {
//            // App is not running
//        }
//        //ListaService listaService = new ListaService();
//        //listaService.lista();
//    }
}
