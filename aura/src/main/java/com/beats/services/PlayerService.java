package com.beats.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.beats.model.Songs;

@Service
public class PlayerService {

    private List<Songs> queue =
            new ArrayList<>();

    private int currentIndex = 0;

    // SET QUEUE

    public void setQueue(List<Songs> songs){

        this.queue = songs;

        this.currentIndex = 0;
    }

    // CURRENT SONG

    public Songs getCurrentSong(){

        if(queue.isEmpty()){

            return null;
        }

        return queue.get(currentIndex);
    }

    // NEXT SONG

    public Songs nextSong(){

        if(queue.isEmpty()){

            return null;
        }

        currentIndex =
                (currentIndex + 1)
                % queue.size();

        return queue.get(currentIndex);
    }

    // PREVIOUS SONG

    public Songs previousSong(){

        if(queue.isEmpty()){

            return null;
        }

        currentIndex =
                (currentIndex - 1 + queue.size())
                % queue.size();

        return queue.get(currentIndex);
    }
}