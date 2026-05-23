package com.beats.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.PlaylistSongs;
import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.repository.SongRepository;
@Service
public class SongServices {
	@Autowired
	SongRepository songRepo;
	@Autowired
	PlaylistRepository playlistRepo;
	@Autowired
	PlaylistSongsRepository playlistSongsRepo;
	@Autowired
	PlaylistService playlistService;

	public List<Songs> getTrendingSongs() {

	    return songRepo.findTop5ByOrderByRepeatedCountDesc();
	}

	/*public Object getPopularArtists() {
		return null;
	}*/
	
	public List<Songs> getFavouriteSongs(Long userId) {

	    // GET FAVOURITE PLAYLIST OF USER
	    Playlists favouritePlaylist =playlistService.findByUserIdAndPlaylistName(userId,"FavouriteList");
	    // IF PLAYLIST NOT FOUND
	    if (favouritePlaylist == null)return new ArrayList<>();
	    // GET PLAYLIST SONGS USING PLAYLIST ID
	    Long pId=favouritePlaylist.getPlaylistId();
	    List<PlaylistSongs> playlistSongs =playlistSongsRepo.findByPlaylist_PlaylistId(pId);
	    // CONVERT PLAYLISTSONGS -> SONGS
	    List<Songs> songs = new ArrayList<>();
	    for (PlaylistSongs ps : playlistSongs) {
	        songs.add(ps.getSong());
	    }
	    return songs;
	}
}
