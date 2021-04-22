package br.lsdi.digialphenotyping.appviews.ui.fenotipagem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import br.lsdi.digialphenotyping.appviews.R;

public class FenotipagemFragment extends Fragment {

    private FenotipagemViewModel fenotipagemViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fenotipagemViewModel =
                ViewModelProviders.of(this).get(FenotipagemViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fenotipagem, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        fenotipagemViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}