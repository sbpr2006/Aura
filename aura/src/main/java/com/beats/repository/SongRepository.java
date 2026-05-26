package com.beats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beats.model.Songs;
@Repository
public interface SongRepository extends JpaRepository<Songs,Integer>{

	List<Songs> findTop5ByOrderByRepeatedCountDesc();

	Songs findBySongId(Long songId);

}
