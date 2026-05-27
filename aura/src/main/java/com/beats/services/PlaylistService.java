package com.beats.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.PlaylistSongs;
import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.model.Users;
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

	public Playlists findByUserIdAndPlaylistId(Long userId, long playlistId) {
		
		return playlistRepo.findByUser_UserIdAndPlaylistId(userId,playlistId);
	}
	
	public int getPlaylistSongCount(Long playlistId) {
		return playlistSongsRepo.findByPlaylist_PlaylistId(playlistId).size();
	}

	public Playlists getOrCreateFavouriteList(
	        Users user) {

	    Playlists playlist =
	            playlistRepo
	            .findByUser_UserIdAndPlaylistName(
	                    user.getUserId(),
	                    "FavouriteList");

	    if(playlist == null) {

	        playlist = new Playlists();

	        playlist.setPlaylistName("FavouriteList");

	        playlist.setUser(user);

	        playlistRepo.save(playlist);
	    }

	    return playlist;
	}

	public void addSongToPlaylist(Long playlistId, Songs song) {

	    Playlists playlist =
	            playlistRepo.findById(playlistId)
	            .orElse(null);

	    if(playlist == null || song == null) {
	        return;
	    }

	    // CHECK IF ALREADY EXISTS
	    boolean exists =
	            songExistsInPlaylist(
	                    playlistId,
	                    song.getSongId());

	    if(exists) {
	        return;
	    }

	    PlaylistSongs ps = new PlaylistSongs();

	    ps.setPlaylist(playlist);

	    ps.setSong(song);

	    playlistSongsRepo.save(ps);
	}

	public void removeSongFromPlaylist(
	        Long playlistId,
	        Long songId) {

	    PlaylistSongs ps =
	            playlistSongsRepo
	            .findByPlaylistPlaylistIdAndSongSongId(
	                    playlistId,
	                    songId);

	    if(ps != null) {

	        playlistSongsRepo.delete(ps);
	    }
	}
	
	public boolean songExistsInPlaylist(
	        Long playlistId,
	        Long songId) {

	    PlaylistSongs ps =
	            playlistSongsRepo
	            .findByPlaylistPlaylistIdAndSongSongId(
	                    playlistId,
	                    songId);

	    return ps != null;
	}
	public boolean isSongInFavourites(Users user, Long songId) {

	    // GET FAVOURITES PLAYLIST
	    Playlists favouritePlaylist =
	            getOrCreateFavouriteList(user);

	    // CHECK SONG EXISTS
	    return songExistsInPlaylist(
	            favouritePlaylist.getPlaylistId(),
	            songId
	    );
	}
	

}
