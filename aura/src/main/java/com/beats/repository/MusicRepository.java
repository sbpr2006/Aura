package com.beats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beats.model.Users;

@Repository
public interface MusicRepository extends JpaRepository<Users,Integer> {
	
	Optional<Users> findByUsername(String username);

	public Users findByUsernameAndPassword(String username, String password);

}
