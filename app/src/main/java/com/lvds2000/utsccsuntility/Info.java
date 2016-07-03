package com.lvds2000.utsccsuntility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lester on 2015-12-27.
 */
public class Info implements Parcelable {
    String name;
    String url;
    String mode;
    Info(String name, String url, String mode){
        this.name = name;
        this.url = url;
    }
    protected Info(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();

    }
    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
    public String getMode(){
        return mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(mode);
    }
    public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}