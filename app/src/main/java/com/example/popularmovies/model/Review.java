package com.example.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String id;
    private String author;
    private String content;
    private String url;

    public Review() {
    }

    /*public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }*/

    private Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuhor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };
}