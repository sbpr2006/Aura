package com.beats.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.repository.SongRepository;
import com.beats.services.PlayerService;
import com.beats.services.PlaylistService;
import com.beats.services.SongServices;
import com.beats.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usr")
public class MusicController {
	@Autowired(required=true)
	SongServices songServices;
	@Autowired
	PlaylistService playlistService;
	@Autowired
	MusicRepository musicRepo;
	@Autowired
	SongRepository songRepo;
	@Autowired
	PlaylistSongsRepository playlistSongsRepo;
	@Autowired
	PlaylistRepository playlistRepo;
	@Autowired
	UserService userService;
	@Autowired
    private PlayerService playerService;
	
	@GetMapping("/register")
	public String register() {
		return "register.html";
	}
	
    @PostMapping("/addUser")
    public String addUser(Users user, Model model, HttpSession session) {
        try {
            Users savedUser = musicRepo.save(user);
            session.setAttribute("loggedUser", savedUser);
            session.setAttribute("username", savedUser.getUsername());
            session.setMaxInactiveInterval(30 * 60);
            model.addAttribute("message", "Registration Successful!");
            model.addAttribute("messageType", "success");
            return "redirect:/usr/home";
        } catch (Exception e) {
            model.addAttribute("message",e.getMessage());
            model.addAttribute("messageType", "error");
            return "register";
        }
    }
    
    @GetMapping("/loginPage")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password,
                            HttpSession session, Model model) {
        try {
        	Users user =  userService.findByUsernameAndPassword(username,password);
        	if(user != null &&
        	   user.getPassword().equals(password)) {
                session.setAttribute("loggedUser", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmail());
                session.setMaxInactiveInterval(30 * 60);
                return "redirect:/usr/home";
            }
            model.addAttribute("message", "Invalid Username or Password");
            model.addAttribute("messageType", "error");
            return "login.html";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            model.addAttribute("messageType", "error");
            return "login.html";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usr/loginPage";
    }
    
    @GetMapping("/home")
    public String homePage(Model model, HttpSession session) {
    	 Users user = (Users) session.getAttribute("loggedUser");
         if(user == null) { return "redirect:/usr/loginPage";}
    	List<Songs> songs =songServices.getTrendingSongs();
    	List<Playlists> playlists=playlistService.getTrendingPlaylists();
        model.addAttribute("songs",songs );
        model.addAttribute("playlists", playlists);
        //model.addAttribute("artists", songServices.getPopularArtists());
        return "index.html"; 
    }
    
    @GetMapping("/profile")
    public String settings(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loggedUser");
        // check if user logged in
        if(user == null) { return "redirect:/usr/loginPage";}
        model.addAttribute("user", user);
        return "settings";
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(

            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String bio,
            HttpSession session,
            Model model) {
    	
        try{

            Users user=(Users)session.getAttribute("loggedUser");
            if(user == null) return "redirect:/usr/loginPage";

            // UPDATE USER DETAILS
            user.setName(name);
            user.setUsername(username);
            user.setEmail(email);
            user.setBio(bio);

            // SAVE
            Users updatedUser=userService.saveUser(user);

            // UPDATE SESSION
            session.setAttribute("loggedUser", updatedUser);
            session.setAttribute("username", updatedUser.getUsername());
            session.setAttribute("email", updatedUser.getEmail());

            model.addAttribute("user", updatedUser);
            model.addAttribute("success", "Profile Updated Successfully");
            return "settings";

        } catch(Exception e) {
            model.addAttribute("error", e.getMessage());
            return "settings";
        }
    }
    
    @PostMapping("/changePassword")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,Model model) {
        Users user =(Users) session.getAttribute("loggedUser");
        if(user == null) return "redirect:/usr/loginPage";

        // WRONG CURRENT PASSWORD
        if(!user.getPassword().equals(currentPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("error","Current Password Incorrect");
            return "settings";
        }

        // PASSWORDS NOT MATCHING
        if(!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("error","Passwords Do Not Match");
            return "settings";
        }

        // UPDATE PASSWORD ONLY HERE
        user.setPassword(newPassword);
        userService.saveUser(user);
        // UPDATE SESSION
        session.setAttribute("loggedUser", user);
        model.addAttribute("user", user);
        model.addAttribute("success","Password Updated Successfully");
        return "settings";
    }
  
    @GetMapping("/playlists") 
    public String libraryPage(Model model, HttpSession session) {
            Users user = (Users) session.getAttribute("loggedUser");
            if (user == null) return "redirect:/usr/loginPage";

            List<Playlists> playlists = playlistService.getPlaylistsByUser(user.getUserId());
            List<Songs> favouriteSongs = songServices.getFavouriteSongs(user.getUserId());

            Map<Long,Integer> playlistSongCounts =new HashMap<>();
            for(Playlists playlist : playlists){
                int count =playlistService.getPlaylistSongCount(playlist.getPlaylistId());
                playlistSongCounts.put(playlist.getPlaylistId(),count);
            }
            model.addAttribute("playlists", playlists);
            model.addAttribute("favouriteSongs", favouriteSongs);
            model.addAttribute("playlistSongCounts", playlistSongCounts);
            return "playlists";
        }

    @GetMapping("/myplaylist/favourites")
    public String openFavouritePlaylist(
            HttpSession session,
            Model model) {
        Users user = (Users) session.getAttribute("loggedUser");
        if(user == null)return "redirect:/usr/loginPage";


        // FIND FAVOURITE PLAYLIST
        Playlists favouritePlaylist =
                playlistService.findByUserIdAndPlaylistName(user.getUserId(),"FavouriteList");

        // CREATE IF NOT EXISTS

        if(favouritePlaylist == null) {

            favouritePlaylist = new Playlists();

            favouritePlaylist.setPlaylistName("FavouriteList");

            favouritePlaylist.setUser(user);

            playlistService.savePlaylist(favouritePlaylist);
        }

        // GET SONGS

        List<Songs> songs =songServices.getFavouriteSongs(user.getUserId());


        model.addAttribute("playlist", favouritePlaylist);

        model.addAttribute("playlistSongs", songs);

        return "myList";
    }
    
    @GetMapping("/myplaylist/{playlistId}")
    public String openPlaylist(@PathVariable long playlistId,
            HttpSession session,
            Model model) {
        Users user = (Users) session.getAttribute("loggedUser");
        if(user == null)return "redirect:/usr/loginPage";


        // FIND FAVOURITE PLAYLIST
        Playlists myPlaylist =
                playlistService.findByUserIdAndPlaylistId(user.getUserId(),playlistId);

        // CREATE IF NOT EXISTS

        if(myPlaylist == null) {

        }

        // GET SONGS

        List<Songs> songs =songServices.getSongsbyPlalistId(playlistId);
        
        playerService.setQueue(songs);


        model.addAttribute("playlist", myPlaylist);

        model.addAttribute("playlistSongs", songs);

        return "myList";
    }
    
    @GetMapping("/nowPlaying")
    public String nowPlayingPage(
            @RequestParam(required = false) Long songId,
            HttpSession session,
            Model model) {

        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/usr/loginPage";
        if (songId == null) return "redirect:/usr/home";
        boolean  isFavourite=playlistService.isSongInFavourites(user,songId);
        List<Playlists> userPlaylists =
                playlistService.getPlaylistsByUser(user.getUserId());
        Songs song = songServices.getSongById(songId);
        model.addAttribute("song", song);
        model.addAttribute("isFavourite", isFavourite);
        model.addAttribute("userPlaylists", userPlaylists);
        return "now_playing";
    }
    
    @ResponseBody
    @PostMapping("/song/incrementRepeat")
    public void increaseRepeatedCount(@RequestParam Long songId){

        Songs song = songRepo.findBySongId(songId);
        Long current = song.getRepeatedCount();
        if(current == null){
            current = 0L;
        }
        song.setRepeatedCount(current + 1);
        songRepo.save(song);
    }
    @PostMapping("/playlist/updatePlayedCount")
    @ResponseBody
    public void updatePlayedCount(){
    	playlistService.refreshAllPlaylistCounts();
    }
    
    
    @GetMapping("/search")
    public String searchPage( HttpSession session) {
    	 Users user = (Users) session.getAttribute("loggedUser");
         // check if user logged in
         if(user == null) { return "redirect:/usr/loginPage";}

        return "search";
    }

    @GetMapping("/searchSongs")
    @ResponseBody
    public List<Songs> searchSongs(
            @RequestParam String query) {

        return songServices.searchSongs(query);
    }
    
    @PostMapping("/addRecentSearch")
    @ResponseBody
    public String addRecentSearch(
            @RequestParam String query,
            HttpSession session) {

        List<String> recentSearches =
                (List<String>) session.getAttribute("recentSearches");

        if(recentSearches == null){

            recentSearches = new ArrayList<>();
        }

        
        recentSearches.remove(query);

        
        recentSearches.add(0, query);

        
        if(recentSearches.size() > 10){

            recentSearches.remove(10);
        }

        session.setAttribute(
                "recentSearches",
                recentSearches
        );

        return "ADDED";
    }
}
