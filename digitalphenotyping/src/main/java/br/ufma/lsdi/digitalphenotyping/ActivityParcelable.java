package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityParcelable implements Parcelable {
    private Activity activity;

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public Activity getActivity(){
        return this.activity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(id);
        //dest.writeString(name);
    }

    protected ActivityParcelable(Parcel in) {
        //id = in.readString();
        //name = in.readString();
    }

    public ActivityParcelable() {

    }

    public static final Creator<ActivityParcelable> CREATOR = new Creator<ActivityParcelable>() {
        @Override
        public ActivityParcelable createFromParcel(Parcel in) {
            return new ActivityParcelable(in);
        }

        @Override
        public ActivityParcelable[] newArray(int size) {
            return new ActivityParcelable[size];
        }
    };
}
