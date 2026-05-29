package com.beats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.services.PlaylistService;
import com.beats.services.SongServices;
import com.beats.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/playlist")
public class PlaylistControlller {
	@Autowired
	PlaylistSongsRepository playlistSongsRepo;
	
    @Autowired(required = true)
    SongServices songServices;

    @Autowired
    PlaylistService playlistService;

    @Autowired
    MusicRepository musicRepo;

    @Autowired
    UserService userService;

    // CREATE PLAYLIST

    @PostMapping("/create")
    public String createPlaylist(
            @RequestParam String playlistName,
            HttpSession session,
            Model model) {

        try {

            Users user =
                    (Users) session.getAttribute("loggedUser");

            if(user == null) {

                return "redirect:/usr/loginPage";
            }

            Playlists playlist = new Playlists();

            playlist.setPlaylistName(playlistName);

            playlist.setUser(user);

            playlistService.savePlaylist(playlist);

            session.setAttribute(
                    "success",
                    "Playlist Created Successfully"
            );

        } catch(Exception e) {

            session.setAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usr/playlists";
    }

    // DELETE PLAYLIST

    @PostMapping("/delete/{playlistId}")
    @Transactional
    public String deletePlaylist(
            @PathVariable Long playlistId,
            HttpSession session) {

        try {
        	playlistSongsRepo
        	.deleteByPlaylist_PlaylistId(playlistId);

            playlistService.deletePlaylist(playlistId);

            session.setAttribute(
                    "success",
                    "Playlist Deleted Successfully"
            );

        } catch(Exception e) {

            session.setAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usr/playlists";
    }

    // RENAME PLAYLIST

    @PostMapping("/rename")
    public String renamePlaylist(
            @RequestParam Long playlistId,
            @RequestParam String playlistName,
            HttpSession session) {

        try {

            Playlists playlist =
                    playlistService.getPlaylistById(
                            playlistId
                    );

            if(playlist == null) {

                session.setAttribute(
                        "error",
                        "Playlist Not Found"
                );

                return "redirect:/usr/playlist";
            }

            playlist.setPlaylistName(playlistName);

            playlistService.savePlaylist(playlist);

            session.setAttribute(
                    "success",
                    "Playlist Renamed Successfully"
            );

        } catch(Exception e) {

            session.setAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usr/playlist";
    }
    
    @PostMapping("/toggleFavourite/{songId}")
    @ResponseBody
    public String toggleFavourite(
            @PathVariable Long songId,
            HttpSession session) {

        try {

            // GET LOGGED USER
            Users user =
                    (Users) session.getAttribute("loggedUser");

            if(user == null) {

                return "LOGIN_REQUIRED";
            }

            // GET SONG
            Songs song =
                    songServices.getSongById(songId);

            if(song == null) {

                return "SONG_NOT_FOUND";
            }

            // GET FAVOURITES PLAYLIST
            Playlists favouritePlaylist =
                    playlistService.getOrCreateFavouriteList(user);

            // CHECK SONG EXISTS
            boolean exists =
                    playlistService.songExistsInPlaylist(
                            favouritePlaylist.getPlaylistId(),
                            songId);

            // REMOVE SONG
            if(exists) {

                playlistService.removeSongFromPlaylist(
                        favouritePlaylist.getPlaylistId(),
                        songId);

                return "REMOVED";
            }

            // ADD SONG
            playlistService.addSongToPlaylist(
                    favouritePlaylist.getPlaylistId(),
                    song);

            return "ADDED";
        }

        catch (Exception e) {

            e.printStackTrace();

            return "ERROR";
        }
    }
    
    @PostMapping("/removeSong")
    public String removeSongFromPlaylist(
            @RequestParam Long playlistId,
            @RequestParam Long songId,
            HttpSession session) {

        try {

            playlistService.removeSongFromPlaylist(
                    playlistId,
                    songId
            );

            session.setAttribute(
                    "success",
                    "Song removed from playlist successfully"
            );

        }
        catch (Exception e) {

        	session.setAttribute(
                    "error",
                    "Failed to remove song"
            );
        }

        return "redirect:/usr/myplaylist/" + playlistId;
    }
    
      
    @PostMapping("/addSongByName")
    public String addSongByName(@RequestParam String songName,
                                @RequestParam Long playlistId) {

        Songs song = songServices.findByTitle(songName);

        if(song != null){

            playlistService.addSongToPlaylist(playlistId, song);

        }

        return "redirect:/usr/myplaylist/" + playlistId;
    }
    @PostMapping("/addSongById")
    public String addSongById(@RequestParam Long playlistId,
                                    @RequestParam Long songId){
    	
    	Songs song = songServices.getSongById(songId);

        playlistService.addSongToPlaylist(
                playlistId,
                song
        );

         return "redirect:/usr/nowPlaying?songId=" + songId;
    }
    
    public void increaseSongCount(@RequestParam Long songId){

        songServices.increaseSongCount(songId);

    }
    
    @GetMapping("/playall/{id}")
    public String openPlaylist(
            @PathVariable Long id,
            Model model,
            HttpSession session){

        List<Songs> songs =
                songServices
                .getSongsbyPlalistId(id);

        // STORE IN SESSION

        session.setAttribute(
                "queue",
                songs
        );

        session.setAttribute(
                "currentIndex",
                0
        );

        model.addAttribute(
                "songs",
                songs
        );

        return "myPlaylist";
    }
}