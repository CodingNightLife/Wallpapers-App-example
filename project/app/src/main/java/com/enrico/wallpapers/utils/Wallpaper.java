package com.enrico.wallpapers.utils;

public class Wallpaper {

    private String url;
    private String thumb;
    private String name;
    private String author;
    private String year;

    Wallpaper(String url, String thumb, String name, String author, String year) {
        this.url = url;
        this.thumb = thumb;
        this.name = name;
        this.author = author;
        this.year = year;
    }

    public String getUrl() {
        return url;
    }

    public String getThumb() {
        return thumb;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }
}
