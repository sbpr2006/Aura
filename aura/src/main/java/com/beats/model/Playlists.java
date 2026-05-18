package com.beats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "playlists")
public class Playlists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Long playlistId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "playlist_name", nullable = false, length = 200)
    private String playlistName;

    // Default Constructor
    public Playlists() {
    }

    // Parameterized Constructor
    public Playlists(Users user, String playlistName) {
        this.user = user;
        this.playlistName = playlistName;
    }

    // Getters and Setters
    public Long getPlaylistId() { return playlistId; } 
    public void setPlaylistId(Long playlistId) { this.playlistId = playlistId; }

    public Users getUser() { return user; } 
    public void setUser(Users user) { this.user = user; }

    public String getPlaylistName() { return playlistName; } 
    public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }

   


}