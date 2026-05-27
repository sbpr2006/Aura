package com.beats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beats.model.PlaylistSongs;
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

        return "redirect:/usr/playlist";
    }

    // DELETE PLAYLIST

    @PostMapping("/delete/{playlistId}")
    public String deletePlaylist(
            @PathVariable Long playlistId,
            HttpSession session) {

        try {

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

        return "redirect:/usr/playlist";
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

        return "redirect:/playlist/myList/" + playlistId;
    }
}