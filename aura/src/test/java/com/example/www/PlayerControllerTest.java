package com.example.www;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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

@SpringBootTest(classes = AuraApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    private Playlists playlist;
    private Users user;

    @BeforeEach
    void setUp() {
        playlistSongsRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
        musicRepository.deleteAll();

        // 1. Create User
        user = Users.builder().username("playeruser").email("player@example.com").password("pass").build();
        user = musicRepository.save(user);

        // 2. Create Songs
        song1 = Songs.builder().title("Track A").artist("Artist X").repeatedCount(0L).build();
        song2 = Songs.builder().title("Track B").artist("Artist Y").repeatedCount(0L).build();
        song3 = Songs.builder().title("Track C").artist("Artist Z").repeatedCount(0L).build();
        song1 = songRepository.save(song1);
        song2 = songRepository.save(song2);
        song3 = songRepository.save(song3);

        // 3. Create Playlist with song1 and song2
        playlist = Playlists.builder().playlistName("My Queue List").user(user).build();
        playlist = playlistRepository.save(playlist);

        PlaylistSongs ps1 = PlaylistSongs.builder().playlist(playlist).song(song1).build();
        PlaylistSongs ps2 = PlaylistSongs.builder().playlist(playlist).song(song2).build();
        playlistSongsRepository.save(ps1);
        playlistSongsRepository.save(ps2);
    }

    @Test
    void testGetQueueEmpty() throws Exception {
        mockMvc.perform(get("/api/queue/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetQueueFromSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Songs> queue = new ArrayList<>();
        queue.add(song1);
        queue.add(song2);
        session.setAttribute("queue", queue);

        mockMvc.perform(get("/api/queue/get").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Track A"))
                .andExpect(jsonPath("$[1].title").value("Track B"));
    }

    @Test
    void testAddToQueue() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add song1
        mockMvc.perform(post("/api/queue/add")
                        .param("songId", song1.getSongId().toString())
                        .session(session))
                .andExpect(status().isOk());

        List<Songs> queue = (List<Songs>) session.getAttribute("queue");
        assertNotNull(queue);
        assertEquals(1, queue.size());
        assertEquals("Track A", queue.get(0).getTitle());

        // Add song1 again (should filter duplicates)
        mockMvc.perform(post("/api/queue/add")
                        .param("songId", song1.getSongId().toString())
                        .session(session))
                .andExpect(status().isOk());

        queue = (List<Songs>) session.getAttribute("queue");
        assertEquals(1, queue.size());

        // Add song2
        mockMvc.perform(post("/api/queue/add")
                        .param("songId", song2.getSongId().toString())
                        .session(session))
                .andExpect(status().isOk());

        queue = (List<Songs>) session.getAttribute("queue");
        assertEquals(2, queue.size());
    }

    @Test
    void testLoadPlaylistQueue() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/queue/loadPlaylist")
                        .param("playlistId", playlist.getPlaylistId().toString())
                        .session(session))
                .andExpect(status().isOk());

        List<Songs> queue = (List<Songs>) session.getAttribute("queue");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        assertNotNull(queue);
        assertEquals(2, queue.size());
        assertEquals(-1, currentIndex);
    }

    @Test
    void testSetCurrentSong() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/songs/setCurrent")
                        .param("index", "2")
                        .session(session))
                .andExpect(status().isOk());

        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        assertEquals(2, currentIndex);
    }

    @Test
    void testIncrementPointer() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Initial pointer increment from null -> 1
        mockMvc.perform(post("/api/songs/incrementPointer")
                        .session(session))
                .andExpect(status().isOk());

        Integer index1 = (Integer) session.getAttribute("currentIndex");
        assertEquals(1, index1);

        // Subsequent increment from 1 -> 2
        mockMvc.perform(post("/api/songs/incrementPointer")
                        .session(session))
                .andExpect(status().isOk());

        Integer index2 = (Integer) session.getAttribute("currentIndex");
        assertEquals(2, index2);
    }

    @Test
    void testNextSongStandard() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Songs> queue = new ArrayList<>();
        queue.add(song1);
        queue.add(song2);
        session.setAttribute("queue", queue);
        session.setAttribute("currentIndex", 0);

        mockMvc.perform(get("/api/songs/next").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Track B"));

        Integer index = (Integer) session.getAttribute("currentIndex");
        assertEquals(1, index);
    }

    @Test
    void testNextSongEndOfQueue() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Songs> queue = new ArrayList<>();
        queue.add(song1);
        session.setAttribute("queue", queue);
        session.setAttribute("currentIndex", 0);

        // When index + 1 >= queue size, it picks a random song from all songs (Track A, B or C)
        // and appends it to queue, then goes to the new index
        mockMvc.perform(get("/api/songs/next").session(session))
                .andExpect(status().isOk());

        List<Songs> updatedQueue = (List<Songs>) session.getAttribute("queue");
        Integer index = (Integer) session.getAttribute("currentIndex");

        assertEquals(2, updatedQueue.size());
        assertEquals(1, index);
    }

    @Test
    void testNextSongShuffle() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Songs> queue = new ArrayList<>();
        queue.add(song1);
        queue.add(song2);
        queue.add(song3);
        session.setAttribute("queue", queue);
        session.setAttribute("currentIndex", 1); // Track B

        // Next with shuffle=true -> should change index to something other than 1
        mockMvc.perform(get("/api/songs/next")
                        .param("shuffle", "true")
                        .session(session))
                .andExpect(status().isOk());

        Integer index = (Integer) session.getAttribute("currentIndex");
        assertTrue(index == 0 || index == 2);
    }

    @Test
    void testPreviousSong() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Songs> queue = new ArrayList<>();
        queue.add(song1);
        queue.add(song2);
        session.setAttribute("queue", queue);
        session.setAttribute("currentIndex", 1);

        mockMvc.perform(get("/api/songs/previous").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Track A"));

        Integer index = (Integer) session.getAttribute("currentIndex");
        assertEquals(0, index);

        // Previous from 0 wraps around to 1
        mockMvc.perform(get("/api/songs/previous").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Track B"));

        index = (Integer) session.getAttribute("currentIndex");
        assertEquals(1, index);
    }

    @Test
    void testGetRandomSong() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentSongId", song1.getSongId().toString());

        // Should return a random song excluding Track A
        mockMvc.perform(get("/api/songs/random").session(session))
                .andExpect(status().isOk());

        String newSongIdStr = (String) session.getAttribute("currentSongId");
        assertNotNull(newSongIdStr);
        // Should not be song1 id
        assertTrue(!newSongIdStr.equals(song1.getSongId().toString()));
    }
}
