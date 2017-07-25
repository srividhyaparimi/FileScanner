package com.example.srividhya.filescanner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by srividhya on 7/24/2017.
 */

public class Update implements Parcelable {

    public int totalMBSize = 0;
    public double avgSizeofFile  = 0;
    public String[] largeToptenFilenames = new String[10];
    public long[]   largeToptenFilesizes = new long[10];
    public String[] mostRecentFiveExtensions = new String[5];
    public byte isScan = 0;
    public Update(){}

    protected Update(Parcel in) {
        totalMBSize = in.readInt();
        avgSizeofFile = in.readDouble();
        in.readStringArray(largeToptenFilenames);
        in.readLongArray(largeToptenFilesizes);
        in.readStringArray( mostRecentFiveExtensions);
        isScan = in.readByte();
    }

    public static final Creator<Update> CREATOR = new Creator<Update>() {
        @Override
        public Update createFromParcel(Parcel in) {
            return new Update(in);
        }

        @Override
        public Update[] newArray(int size) {

            return new Update[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalMBSize);
        dest.writeDouble(avgSizeofFile);

        dest.writeStringArray(largeToptenFilenames);
        dest.writeLongArray(largeToptenFilesizes);

        dest.writeStringArray( mostRecentFiveExtensions);

        dest.writeByte(isScan);
    }
}