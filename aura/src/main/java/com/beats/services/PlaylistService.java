package com.beats.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.Playlists;
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
@Service
public class PlaylistService {
	@Autowired
	PlaylistRepository playlistRepo;
	@Autowired
	PlaylistSongsRepository playlistSongsRepo;
	
	public List<Playlists> getTrendingPlaylists() {
	    return playlistRepo.findTop10ByOrderByPlayedCountDesc();
	}
	
	public List<Playlists> getPlaylistsByUser(Long userId) {

	    return playlistRepo.findByUser_UserId(userId);
	}
	
	public void savePlaylist(Playlists playlist) {

	    playlistRepo.save(playlist);
	}
	
	public void deletePlaylist(Long playlistId) {

	    playlistRepo.deleteById(playlistId);
	}
	
	public Playlists getPlaylistById(Long playlistId) {
		return playlistRepo.findByPlaylistId(playlistId);
	}
	public Playlists findByUserIdAndPlaylistName(Long userId, String string) {
		return playlistRepo.findByUser_UserIdAndPlaylistName(userId,string);
	}

	public int getPlaylistSongCount(Long playlistId) {
		return playlistSongsRepo.findByPlaylist_PlaylistId(playlistId).size();
	}

}
