package br.lsdi.digialphenotyping.appviews.ui.home;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

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

        VirtualSensorInterface onNewMessageListener = activationCode -> {
            System.out.println(activationCode);
            if (!TextUtils.isEmpty(activationCode)) {
                //editText.setText(String.valueOf(activationCode));
                System.out.println("################ ActivationCode...");
                System.out.println(activationCode);
            }
        };

        smsListener = new SmsBroadcastReceiver(onNewMessageListener);
        if (getContext() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContext().registerReceiver(smsListener, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
            }
        }

        //Cria sensor SMS
        //SMSSensor smsSensor = new SMSSensor();
        // Pega uma instância de SmsRetrieverClient, usada para começar a ouvir uma correspondência
        // mensagem SMS.
        SmsRetrieverClient client = SmsRetriever.getClient(Objects.requireNonNull(getContext()) /* context */);
        // Inicia SmsRetriever, que espera por UMA mensagem SMS correspondente até o tempo limite
        // (5 minutos). A mensagem SMS correspondente será enviada por meio de uma intenção de transmissão com
        // ação SmsRetriever # SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();
        // Listener sucesso/falha da tarefa de início. Se em um background thread, este
        // pode ser feito o bloqueio usando Tasks.await (task, [timeout]);
        task.addOnSuccessListener(aVoid -> {
            // Iniciado retriever com sucesso, espera intent broadcast
            System.out.println("################ Sucesso ao detectar broadcast...");
        });
        // Se falhou
        task.addOnFailureListener(e -> {
            // Failed to start retriever, inspect Exception for more details
            System.out.println("################ Erro ao detectar SMS...");
        });

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