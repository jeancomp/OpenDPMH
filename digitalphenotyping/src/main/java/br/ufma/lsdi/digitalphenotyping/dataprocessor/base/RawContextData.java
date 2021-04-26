package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import android.os.Parcel;
import android.os.Parcelable;

public class RawContextData implements Parcelable {
    String nameProcessor;
    Object rawData;

    public RawContextData() { }

    public RawContextData(Parcel in) {
        nameProcessor = in.readString();
    }

    public void setNameProcessor(String nameProcessor) {
        this.nameProcessor = nameProcessor;
    }

    public String getNameProcessor(String nameProcessor){
        return this.nameProcessor;
    }

    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }

    public Object getRawData(){
        return rawData;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameProcessor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RawContextData> CREATOR = new Creator<RawContextData>() {
        @Override
        public RawContextData createFromParcel(Parcel in) {
            return new RawContextData(in);
        }

        @Override
        public RawContextData[] newArray(int size) {
            return new RawContextData[size];
        }
    };
}
