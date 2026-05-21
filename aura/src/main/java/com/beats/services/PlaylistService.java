package com.beats.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.Playlists;
import com.beats.repository.PlaylistRepository;
@Service
public class PlaylistService {
	@Autowired
	PlaylistRepository playlistRepo;
	public List<Playlists> getTrendingPlaylists() {
	    return playlistRepo.findTop10ByOrderByPlayedCountDesc();
	}

}
