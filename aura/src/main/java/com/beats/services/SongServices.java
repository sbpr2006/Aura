package com.beats.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.Songs;
import com.beats.repository.MusicRepository;
import com.beats.repository.SongRepository;
@Service
public class SongServices {
	@Autowired
	SongRepository songRepo;

	public List<Songs> getTrendingSongs() {

	    return songRepo.findTop5ByOrderByRepeatedCountDesc();
	}

	public Object getPopularArtists() {
		return null;
	}
	
}
