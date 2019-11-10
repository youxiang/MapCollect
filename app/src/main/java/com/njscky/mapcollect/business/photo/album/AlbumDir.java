package com.njscky.mapcollect.business.photo.album;

import android.os.Parcel;
import android.os.Parcelable;

import com.njscky.mapcollect.db.entitiy.PhotoJCJ;

import java.util.ArrayList;

public class AlbumDir implements Parcelable {
    public static final Creator<AlbumDir> CREATOR = new Creator<AlbumDir>() {
        @Override
        public AlbumDir createFromParcel(Parcel source) {
            return new AlbumDir(source);
        }

        @Override
        public AlbumDir[] newArray(int size) {
            return new AlbumDir[size];
        }
    };
    public String dirName;
    public ArrayList<PhotoJCJ> photos;
    public boolean selected;
    public String JCJBH;

    public AlbumDir() {
    }

    protected AlbumDir(Parcel in) {
        this.dirName = in.readString();
        this.photos = in.createTypedArrayList(PhotoJCJ.CREATOR);
        this.selected = in.readByte() != 0;
        this.JCJBH = in.readString();
    }

    public void addPhoto(PhotoJCJ photoJCJ) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(photoJCJ);
    }

    public int getPhotoNum() {
        if (photos == null) {
            return 0;
        }
        return photos.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dirName);
        dest.writeTypedList(this.photos);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.JCJBH);
    }
}
