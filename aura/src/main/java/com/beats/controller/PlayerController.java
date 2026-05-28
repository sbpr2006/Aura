package com.beats.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Songs nextSong(
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
                (currentIndex + 1)
                % queue.size();

        session.setAttribute(
                "currentIndex",
                currentIndex
        );

        return queue.get(currentIndex);
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

                songServices
                .getSongsbyPlalistId(playlistId);

        session.setAttribute(
                "queue",
                songs
        );

        session.setAttribute(
                "currentIndex",
                0
        );
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

        if(!queue.contains(song)){

            queue.add(song);
        }

        session.setAttribute("queue", queue);

        session.setAttribute(
                "currentIndex",
                queue.indexOf(songId)
        );
    }
    
}