package com.beats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.beats.model.Playlists;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlists, Long> {
	List<Playlists>     findTop10ByOrderByPlayedCountDesc();

	Playlists findByPlaylistId(Long playlistId);

	List<Playlists> findByUser_UserId(Long userId);

	Playlists findByUser_UserIdAndPlaylistName(Long userId, String string);
	
	Playlists findByUser_UserIdAndPlaylistId(Long userId, long playlistId);
	
	@Modifying
	@Transactional
	@Query("""
	UPDATE Playlists p
	SET p.playedCount = (
	    SELECT COALESCE(SUM(s.repeatedCount),0)
	    FROM PlaylistSongs ps
	    JOIN ps.song s
	    WHERE ps.playlist.playlistId = p.playlistId
	)
	""")
	void refreshAllPlaylistPlayedCounts();
}