package com.beats.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beats.model.Songs;
import com.beats.services.PlayerService;
import com.beats.services.PlaylistService;
import com.beats.services.SongServices;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerService playerService;
    @Autowired(required=true)
	SongServices songServices;
    @Autowired
	PlaylistService playlistService;

    @GetMapping("/songs/next")
    @ResponseBody
    public ResponseEntity<Songs> nextSong(
            @RequestParam(required=false) Boolean shuffle,
            HttpSession session){

        List<Songs> queue =
                (List<Songs>) session.getAttribute("queue");

        Integer currentIndex =
                (Integer) session.getAttribute("currentIndex");

        if(queue == null || queue.isEmpty()){
            return ResponseEntity.ok(null);
        }

        if(currentIndex == null){
            currentIndex = 0;
        }

        if(Boolean.TRUE.equals(shuffle)){
            // RANDOM INDEX EXCLUDING CURRENT
            int newIndex;
            do {
                newIndex = new java.util.Random().nextInt(queue.size());
            } while(queue.size() > 1 && newIndex == currentIndex);

            currentIndex = newIndex;

        } else {

            int nextIndex = currentIndex + 1;

            // END OF QUEUE — pick random song
            if(nextIndex >= queue.size()){

                Songs randomSong = songServices.getAllSongs()
                    .stream()
                    .skip((long)(new java.util.Random().nextInt(
                        (int) songServices.getAllSongs().size()
                    )))
                    .findFirst()
                    .orElse(null);

                if(randomSong != null){
                    queue.add(randomSong);
                    session.setAttribute("queue", queue);
                }

                currentIndex = queue.size() - 1;

            } else {
                currentIndex = nextIndex;
            }
        }

        session.setAttribute("currentIndex", currentIndex);

        return ResponseEntity.ok(queue.get(currentIndex));
    }
    
    @GetMapping("/songs/previous")
    @ResponseBody
    public Songs previousSong(
            HttpSession session){

        List<Songs> queue =

                (List<Songs>)
                session.getAttribute("queue");

        Integer currentIndex =

                (Integer)
                session.getAttribute("currentIndex");

        if(queue == null || queue.isEmpty()){

            return null;
        }

        currentIndex =

                (currentIndex - 1 + queue.size())
                % queue.size();

        session.setAttribute(
                "currentIndex",
                currentIndex
        );

        return queue.get(currentIndex);
    }
    
    @PostMapping("/songs/setCurrent")
    @ResponseBody
    public void setCurrentSong(

            @RequestParam int index,

            HttpSession session){

        session.setAttribute(
                "currentIndex",
                index
        );
    }
    @PostMapping("/queue/loadPlaylist")
    @ResponseBody
    public void loadPlaylistQueue(

            @RequestParam Long playlistId,

            HttpSession session){

        List<Songs> songs =
                songServices.getSongsbyPlalistId(playlistId);

        session.setAttribute("queue", songs);
        session.setAttribute("currentIndex", -1);
    }
    
    @PostMapping("/queue/add")
    @ResponseBody
    public void addToQueue(
            @RequestParam Long songId,
            HttpSession session){
    	Songs song=songServices.getSongById(songId);

        List<Songs> queue =
                (List<Songs>) session.getAttribute("queue");

        if(queue == null){

            queue = new ArrayList<>();
        }

        // AVOID DUPLICATE

        boolean alreadyExists = queue.stream()
        	    .anyMatch(s -> s.getSongId().equals(song.getSongId()));

        	if(!alreadyExists){
        	    queue.add(song);
        	}

        	session.setAttribute("queue", queue);
    }
    
    @GetMapping("/songs/random")
    @ResponseBody
    public Songs getRandomSong(HttpSession session){

        List<Songs> allSongs = songServices.getAllSongs(); // use all songs

        if(allSongs == null || allSongs.isEmpty()){
            return null;
        }

        // EXCLUDE CURRENT SONG TO AVOID REPEAT
        String currentSongId = (String) session.getAttribute("currentSongId");

        List<Songs> filtered = allSongs.stream()
            .filter(s -> !String.valueOf(s.getSongId()).equals(currentSongId))
            .collect(java.util.stream.Collectors.toList());

        if(filtered.isEmpty()) filtered = allSongs;

        Songs picked = filtered.get(new Random().nextInt(filtered.size()));
        session.setAttribute("currentSongId", String.valueOf(picked.getSongId()));
        return picked;
    }
    
    @PostMapping("/songs/incrementPointer")
    @ResponseBody
    public void incrementPointer(
            HttpSession session){

        Integer currentIndex =
                (Integer) session.getAttribute(
                        "currentIndex"
                );

        if(currentIndex == null){

            currentIndex = 0;
        }

        currentIndex++;

        session.setAttribute(
                "currentIndex",
                currentIndex
        );
    }
    
    @GetMapping("/queue/get")
    @ResponseBody
    public List<Songs> getQueue(HttpSession session){

        List<Songs> queue =
            (List<Songs>) session.getAttribute("queue");

        return queue != null ? queue : new ArrayList<>();
    }
    
}