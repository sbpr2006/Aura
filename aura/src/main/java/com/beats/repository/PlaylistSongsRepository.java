package com.beats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beats.model.PlaylistSongs;

@Repository
public interface PlaylistSongsRepository extends JpaRepository<PlaylistSongs, Long>{

	List<PlaylistSongs> findByPlaylist_PlaylistId(Long playlistId);

	PlaylistSongs findByPlaylistPlaylistIdAndSongSongId(Long playlistId, Long songId);

	List<PlaylistSongs> findBySongSongId(Long songId);

	void deleteByPlaylist_PlaylistId(Long playlistId);


}
