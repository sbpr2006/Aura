package com.beats.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@Builder
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

   
}