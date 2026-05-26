package com.beats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "songs")
public class Songs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String artist;

    @Column(length = 200)
    private String album;

    @Column(length = 100)
    private String genre;

    // Duration in seconds
    private Integer duration;

    @Column(length = 500)
    private String filePath;

    @Column( length = 500)
    private String imagePath;
    
    @Column
    private int repeatedCount;

	public Songs orElse(Object object) {

		return null;
	}
   
}
