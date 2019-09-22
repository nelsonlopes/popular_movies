package com.example.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movies")
public class Movie implements Parcelable {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "original_title")
    private String originalTitle;
    private String overview;
    @ColumnInfo(name = "vote_average")
    private Double voteAverage;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;

    public Movie() {
    }

    public Movie(int id, String originalTitle, String overview, Double voteAverage,
                 String releaseDate, String posterPath, String backdropPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }

    @Ignore
    private Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) { this.overview = overview; }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) { this.voteAverage = voteAverage; }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterPath() { return posterPath; }

    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getBackdropPath() { return backdropPath; }

    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
