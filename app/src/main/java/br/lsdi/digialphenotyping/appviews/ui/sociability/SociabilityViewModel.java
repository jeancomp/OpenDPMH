package br.lsdi.digialphenotyping.appviews.ui.sociability;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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