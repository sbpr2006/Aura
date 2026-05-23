package com.beats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beats.model.Playlists;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlists, Long> {
	List<Playlists>     findTop10ByOrderByPlayedCountDesc();

	List<Playlists> findByUserUserId(Long userId);

	Playlists findByUser_UserIdAndPlaylistName(Long userId, String string);

	Playlists findByPlaylistId(Long playlistId);

	List<Playlists> findByUser_UserId(Long userId);
	
}