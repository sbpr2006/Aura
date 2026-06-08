package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.beats.AuraApplication;
import com.beats.model.PlaylistSongs;
import com.beats.model.Playlists;
import com.beats.model.Songs;
import com.beats.model.Users;
import com.beats.repository.MusicRepository;
import com.beats.repository.PlaylistRepository;
import com.beats.repository.PlaylistSongsRepository;
import com.beats.repository.SongRepository;
import com.beats.services.SongServices;

@SpringBootTest(classes = AuraApplication.class)
@ActiveProfiles("test")
@Transactional
class SongServicesTest {

    @Autowired
    private SongServices songServices;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongsRepository playlistSongsRepository;

    @Autowired
    private MusicRepository musicRepository;

    private Songs song1;
    private Songs song2;
    private Songs song3;
    private Users user;

    @BeforeEach
    void setUp() {
        // Clear repositories (transactional setup will clean up, but good for setup safety)
        playlistSongsRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
        musicRepository.deleteAll();

        // 1. Create a user
        user = Users.builder()
                .username("songuser")
                .email("songuser@example.com")
                .password("password")
                .name("Song User")
                .build();
        user = musicRepository.save(user);

        // 2. Create songs
        song1 = Songs.builder()
                .title("Aura Beats")
                .artist("Artist A")
                .album("Album X")
                .repeatedCount(10L)
                .build();
        song1 = songRepository.save(song1);

        song2 = Songs.builder()
                .title("Deep Beats")
                .artist("Artist B")
                .album("Album Y")
                .repeatedCount(25L)
                .build();
        song2 = songRepository.save(song2);

        song3 = Songs.builder()
                .title("Calm Aura")
                .artist("Artist C")
                .album("Album Z")
                .repeatedCount(5L)
                .build();
        song3 = songRepository.save(song3);
    }

    @Test
    void testGetTrendingSongs() {
        List<Songs> trending = songServices.getTrendingSongs();

        // Should return top 5 ordered by repeatedCount desc (we have 3)
        assertEquals(3, trending.size());
        assertEquals("Deep Beats", trending.get(0).getTitle());  // 25
        assertEquals("Aura Beats", trending.get(1).getTitle());  // 10
        assertEquals("Calm Aura", trending.get(2).getTitle());   // 5
    }

    @Test
    void testGetSongById() {
        Songs found = songServices.getSongById(song1.getSongId());
        assertNotNull(found);
        assertEquals("Aura Beats", found.getTitle());
    }

    @Test
    void testFindByTitle() {
        Songs found = songServices.findByTitle("deep beats");
        assertNotNull(found);
        assertEquals(song2.getSongId(), found.getSongId());
    }

    @Test
    void testGetAllSongs() {
        List<Songs> songs = songServices.getAllSongs();
        assertEquals(3, songs.size());
    }

    @Test
    void testSearchSongs() {
        // Search "Beats" -> should match "Aura Beats" and "Deep Beats"
        List<Songs> beatsSongs = songServices.searchSongs("Beats");
        assertEquals(2, beatsSongs.size());

        // Search "Aura" -> should match "Aura Beats" and "Calm Aura"
        List<Songs> auraSongs = songServices.searchSongs("Aura");
        assertEquals(2, auraSongs.size());

        // Search "Album X" (should match album search)
        List<Songs> albumSongs = songServices.searchSongs("Album X");
        assertEquals(1, albumSongs.size());
        assertEquals("Aura Beats", albumSongs.get(0).getTitle());

        // Search something nonexistent
        List<Songs> none = songServices.searchSongs("randomnonexistent");
        assertTrue(none.isEmpty());
    }

    @Test
    void testGetSongsByPlaylistId() {
        // Create Playlist
        Playlists playlist = Playlists.builder()
                .playlistName("Test List")
                .user(user)
                .build();
        playlist = playlistRepository.save(playlist);

        // Link song1 and song3 to playlist
        PlaylistSongs ps1 = PlaylistSongs.builder().playlist(playlist).song(song1).build();
        PlaylistSongs ps3 = PlaylistSongs.builder().playlist(playlist).song(song3).build();
        playlistSongsRepository.save(ps1);
        playlistSongsRepository.save(ps3);

        List<Songs> songs = songServices.getSongsbyPlalistId(playlist.getPlaylistId());
        assertEquals(2, songs.size());
        assertTrue(songs.stream().anyMatch(s -> s.getTitle().equals("Aura Beats")));
        assertTrue(songs.stream().anyMatch(s -> s.getTitle().equals("Calm Aura")));
    }

    @Test
    void testGetFavouriteSongs() {
        // With no favourites list created yet
        List<Songs> favsEmpty = songServices.getFavouriteSongs(user.getUserId());
        assertTrue(favsEmpty.isEmpty());

        // Create FavouriteList playlist
        Playlists favList = Playlists.builder()
                .playlistName("FavouriteList")
                .user(user)
                .build();
        favList = playlistRepository.save(favList);

        // Add song2 to favourites
        PlaylistSongs ps = PlaylistSongs.builder().playlist(favList).song(song2).build();
        playlistSongsRepository.save(ps);

        List<Songs> favs = songServices.getFavouriteSongs(user.getUserId());
        assertEquals(1, favs.size());
        assertEquals("Deep Beats", favs.get(0).getTitle());
    }

    @Test
    void testIncreaseSongCount() {
        // Create Playlist containing song1
        Playlists playlist = Playlists.builder()
                .playlistName("Count Playlist")
                .user(user)
                .playedCount(0L)
                .build();
        playlist = playlistRepository.save(playlist);

        PlaylistSongs ps = PlaylistSongs.builder().playlist(playlist).song(song1).build();
        playlistSongsRepository.save(ps);

        // Increase song1 count
        // Pre-count: song1 repeatedCount = 10L
        songServices.increaseSongCount(song1.getSongId());

        // Verify song count increased
        Songs updatedSong = songRepository.findBySongId(song1.getSongId());
        assertEquals(11L, updatedSong.getRepeatedCount());

        // Verify playlist playedCount updated (it updates based on sum of repeatedCount of its songs)
        Playlists updatedPlaylist = playlistRepository.findByPlaylistId(playlist.getPlaylistId());
        // song1 is 11L, sum = 11L
        assertEquals(11L, updatedPlaylist.getPlayedCount());
    }
}
