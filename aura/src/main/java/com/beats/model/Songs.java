package com.beats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "songs")
public class Songs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String artist;

    @Column(length = 200)
    private String album;

    @Column(length = 100)
    private String genre;

    // Duration in seconds
    private Integer duration;

    @Column(length = 500)
    private String filePath;

    @Column( length = 500)
    private String imagePath;

    // Default Constructor
    public Songs() {
    }

    // Parameterized Constructor
    public Songs(String title, String artist, String album,
                String genre, Integer duration,
                String filePath, String imagePath) {

        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
        this.imagePath = imagePath;
    }


    // Getters and Setters

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getTitle() { return title; } 
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; } 
    public void setArtist(String artist) { this.artist = artist; }
    
    public String getAlbum() { return album; } 
    public void setAlbum(String album) { this.album = album; }
    
    public String getGenre() { return genre; } 
    public void setGenre(String genre) { this.genre = genre; }
    
    public Integer getDuration() { return duration; } 
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getFilePath() { return filePath; } 
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getImagePath() { return imagePath; } 
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }


}
