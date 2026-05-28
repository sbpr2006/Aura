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
	@Autowired
	SongRepository sonsRepo;
	
	
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
	    return getSongsbyPlalistId(pId);
	}

	public List<Songs> getSongsbyPlalistId(long playlistId) {
	    // GET PLAYLIST SONGS USING PLAYLIST ID
	    List<PlaylistSongs> playlistSongs =playlistSongsRepo.findByPlaylist_PlaylistId(playlistId);
	    // CONVERT PLAYLISTSONGS -> SONGS
	    List<Songs> songs = new ArrayList<>();
	    for (PlaylistSongs ps : playlistSongs) {
	        songs.add(ps.getSong());
	    }
	    return songs;
	}

	public Songs getSongById(Long songId) {

	    return songRepo
	            .findBySongId(songId);
	
	}

	public Songs findByTitle(String songName) {
		return songRepo.findByTitleIgnoreCase(songName);
	}

	public void increaseSongCount(Long songId){

	    Songs song = songRepo.findBySongId(songId);
	
	    // INCREASE SONG COUNT
	
	    Long current = song.getRepeatedCount();
	
	    song.setRepeatedCount(current + 1);
	
	    songRepo.save(song);
	
	    // UPDATE PLAYLIST COUNTS
	    List<PlaylistSongs> playlistSongs =
	
	            playlistSongsRepo.findBySongSongId(songId);
	
	    // UPDATE EACH PLAYLIST COUNT
	
	    for(PlaylistSongs ps : playlistSongs){
	
	        Playlists playlist =
	                ps.getPlaylist();
	
	        List<PlaylistSongs> songsInPlaylist =
	
	                playlistSongsRepo
	                .findByPlaylist_PlaylistId(
	                        playlist.getPlaylistId()
	                );
	
	        Long total = 0L;
	
	        for(PlaylistSongs item : songsInPlaylist){
	
	            Songs s = item.getSong();
	
	            if(s.getRepeatedCount() != null){
	
	                total += s.getRepeatedCount();
	            }
	        }
	
	        playlist.setPlayedCount(total);
	
	        playlistRepo.save(playlist);
	    }
	}
}
