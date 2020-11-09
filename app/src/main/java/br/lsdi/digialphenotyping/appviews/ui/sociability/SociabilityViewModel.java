package br.lsdi.digialphenotyping.appviews.ui.sociability;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class SociabilityViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SociabilityViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}