package br.lsdi.ufma.cddldemoapp.ui.fenotipagem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import br.lsdi.ufma.cddldemoapp.R;

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