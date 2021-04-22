package br.lsdi.digialphenotyping.appviews.ui.chat;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.lsdi.digialphenotyping.appviews.R;
import br.lsdi.digialphenotyping.appviews.ui.fenotipagem.FenotipagemViewModel;
import br.lsdi.digialphenotyping.virtualsensors.SMS;
import br.lsdi.digialphenotyping.virtualsensors.VirtualSensorInterface;

public class ChatFragment extends Fragment {
    private FenotipagemViewModel fenotipagemViewModel;
    SMS testeSMS;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fenotipagemViewModel = ViewModelProviders.of(this).get(FenotipagemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fenotipagem, container, false);
        //final TextView textView = root.findViewById(R.id.text_slideshow);

        //Cria uma interface virtual para o Sensor SMS
        testeSMS = new SMS();
        testeSMS.registerListener();
        testeSMS.startCollecting();

        return root;
    }
}
