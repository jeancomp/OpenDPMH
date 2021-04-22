package br.lsdi.digialphenotyping.appviews.ui.fenotipagem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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