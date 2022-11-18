package com.example.travelfragment.model;



import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Travel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String artname;

    @Nullable
    @ColumnInfo(name = "artist")
    private String artistname;

    @Nullable
    @ColumnInfo(name = "year")
    private String year;

    @Nullable
    @ColumnInfo(name = "image")
    public byte[] image;

    public Travel(String artname, @Nullable String artistname, @Nullable String year, @Nullable byte[] image) {
        this.artname = artname;
        this.artistname = artistname;
        this.year = year;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtname() {
        return artname;
    }

    public void setArtname(String artname) {
        this.artname = artname;
    }

    @Nullable
    public String getArtistname() {
        return artistname;
    }

    public void setArtistname(@Nullable String artistname) {
        this.artistname = artistname;
    }

    @Nullable
    public String getYear() {
        return year;
    }

    public void setYear(@Nullable String year) {
        this.year = year;
    }

    @Nullable
    public byte[] getImage() {
        return image;
    }

    public void setImage(@Nullable byte[] image) {
        this.image = image;
    }
}
