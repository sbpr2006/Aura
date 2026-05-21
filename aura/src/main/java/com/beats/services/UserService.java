package com.beats.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beats.model.Users;
import com.beats.repository.MusicRepository;

@Service
public class UserService {
	@Autowired
	MusicRepository musicRepo;

	public Users findByUsernameAndPassword(String username, String password) {
		Users user=musicRepo.findByUsernameAndPassword(username,password);
		return user;
	}

	public Users saveUser(Users user) {
		return musicRepo.save(user);
	}

}
