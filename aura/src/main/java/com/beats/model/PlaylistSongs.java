package com.beats.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "playlist_songs")
public class PlaylistSongs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_song_id")
    private Long playlistSongId;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlists playlist;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Songs song;

    // Default Constructor
    public PlaylistSongs() {
    }

    // Parameterized Constructor
    public PlaylistSongs(Playlists playlist, Songs song) {
        this.playlist = playlist;
        this.song = song;
    }

    // Getters and Setters

    public Long getPlaylistSongId() { return playlistSongId; } 
    public void setPlaylistSongId(Long playlistSongId) { this.playlistSongId = playlistSongId; }

    public Playlists getPlaylist() { return playlist; } 
    public void setPlaylist(Playlists playlist) { this.playlist = playlist; }

    public Songs getSong() { return song; } 
    public void setSong(Songs song) { this.song = song; }



}