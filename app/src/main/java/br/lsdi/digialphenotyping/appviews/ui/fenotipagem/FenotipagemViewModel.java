package br.lsdi.digialphenotyping.appviews.ui.fenotipagem;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class FenotipagemViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FenotipagemViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}