// ======================================================
// GLOBAL FLAGS
// ======================================================

window.isShuffle = false;
window.isRepeat = false;

// RESTORE SAVED FLAGS

const savedShuffle = localStorage.getItem("isShuffle");
const savedRepeat = localStorage.getItem("isRepeat");

if(savedShuffle === "true")window.isShuffle = true;
if(savedRepeat === "true")window.isRepeat = true;

// ======================================================
// GLOBAL AUDIO PLAYER
// ======================================================
	
if (!window.globalAudioPlayer) {

    window.globalAudioPlayer = new Audio();

}


const globalAudioPlayer =
    window.globalAudioPlayer;
	
// ======================================================
// RESTORE PLAYER STATE
// ======================================================

window.addEventListener("load", () => {

    const savedSong =
        localStorage.getItem("currentSong");

    const savedSongId =
        localStorage.getItem("currentSongId");

    const savedTitle =
        localStorage.getItem("currentTitle");

    const savedArtist =
        localStorage.getItem("currentArtist");

    const savedImage =
        localStorage.getItem("currentImage");

    const savedTime =
        localStorage.getItem("currentTime");

    const isPlaying =
        localStorage.getItem("isPlaying");

    // RESTORE AUDIO

    if(savedSong){

        globalAudioPlayer.src =
            savedSong;

        globalAudioPlayer.currentTime =
            parseFloat(savedTime || 0);
    }

    // RESTORE UI

    updatePlayerUI({

        title: savedTitle,

        artist: savedArtist,

        imagePath: savedImage,

        songId: savedSongId
    });

    // AUTO PLAY

    if(isPlaying === "true" &&
       savedSong){

        globalAudioPlayer.play()
            .catch(error => {

                console.log(error);

            });
    }
});

// ======================================================
// SAVE PLAYER TIME
// ======================================================

globalAudioPlayer.addEventListener(
    "timeupdate",
    () => {

        localStorage.setItem(
            "currentTime",
            globalAudioPlayer.currentTime
        );
    }
);

// ======================================================
// SAVE PLAYER STATE
// ======================================================

globalAudioPlayer.addEventListener(
    "play",
    () => {

        localStorage.setItem(
            "isPlaying",
            "true"
        );
    }
);

globalAudioPlayer.addEventListener(
    "pause",
    () => {

        localStorage.setItem(
            "isPlaying",
            "false"
        );
    }
);

// ======================================================
// UPDATE PLAYER UI
// ======================================================

function updatePlayerUI(song){

    const titleEl =
        document.getElementById("playerTitle");

    const artistEl =
        document.getElementById("playerArtist");

    const imageEl =
        document.getElementById("playerImage");

    const miniPlayerImage =
        document.getElementById("miniPlayerImage");

    const nowPlayingLink =
        document.getElementById("nowPlayingLink");

    if(titleEl && song.title){

        titleEl.innerText =
            song.title;
    }

    if(artistEl && song.artist){

        artistEl.innerText =
            song.artist;
    }

    if(imageEl && song.imagePath){

        imageEl.src =
            song.imagePath;
    }

    if(miniPlayerImage &&
       song.imagePath){

        miniPlayerImage.src =
            song.imagePath;
    }

    // UPDATE NOW PLAYING LINK

    if(nowPlayingLink &&
       song.songId){

        nowPlayingLink.href =
            "/usr/nowPlaying?songId="
            + song.songId;
    }
}

// ======================================================
// SAVE TO LOCAL STORAGE
// ======================================================

function saveSong(song){

    localStorage.setItem(
        "currentSongId",
        song.songId
    );

    localStorage.setItem(
        "currentSong",
        globalAudioPlayer.src
    );

    localStorage.setItem(
        "currentTitle",
        song.title
    );

    localStorage.setItem(
        "currentArtist",
        song.artist
    );

    localStorage.setItem(
        "currentImage",
        song.imagePath
    );

    localStorage.setItem(
        "currentTime",
        "0"
    );

    localStorage.setItem(
        "isPlaying",
        "true"
    );
}

// ======================================================
// PLAY SONG FROM BUTTON
// ======================================================

function playSong(button){

    if(!button){

        return;
    }

    const song = {

        songId:
            button.getAttribute("data-songid"),

        title:
            button.getAttribute("data-title"),

        artist:
            button.getAttribute("data-artist"),

        imagePath:
            button.getAttribute("data-image"),

        filePath:
            button.getAttribute("data-audio")
    };

    loadSong(song);

    // UPDATE SESSION CURRENT INDEX

    const index =

        Array.from(
            document.querySelectorAll(".play-btn")
        ).indexOf(button);

    fetch(
        "/api/songs/setCurrent?index="
        + index,
        {
            method:"POST"
        }
    );
	fetch("/api/queue/add?songId=" + song.songId,{
	    method:"POST"
	})
	.then(() => {

	    console.log("Added to queue");

	});
}

// ======================================================
// LOAD SONG OBJECT
// ======================================================

function loadSong(song){

	if(!song || !song.filePath){

	       console.error("loadSong: invalid song", song);
	       return;
	   }
    // UPDATE UI

    updatePlayerUI(song);

    // CLEAN PATH

    let cleanPath =
        song.filePath
            .trim()
            .replace(/^\/+/, "");

    // SET AUDIO

    globalAudioPlayer.src =
        "/" + cleanPath;

    globalAudioPlayer.load();

    // PLAY AUDIO

    globalAudioPlayer.play()

    .then(() => {

        saveSong(song);

    })

    .catch(error => {

        console.error(error);

    });

    // INCREMENT REPEAT COUNT

    fetch(
        "/usr/song/incrementRepeat?songId="
        + song.songId,
        {
            method:"POST"
        }
    );
	
	fetch("/api/queue/get")
	.then(r => r.json())
	.then(data => console.log(data));
}

// ======================================================
// NEXT SONG
// ======================================================

function nextSong(){
	
	const currentSongId =
	        localStorage.getItem(
	            "currentSongId"
	        );

	// ADD CURRENT SONG TO PREVIOUS QUEUE (fire and forget)

	    if(currentSongId){
	        fetch("/api/queue/add?songId=" + currentSongId, { method:"POST" })
	        .catch(error => console.error(error));
	    }

	    // INCREMENT POINTER THEN FETCH NEXT

		fetch("/api/songs/next" + (window.isShuffle ? "?shuffle=true" : ""))
		    .then(response => {
		        if(!response.ok) throw new Error("HTTP " + response.status);
		        return response.text();
		    })
		    .then(data => {
		        if(!data || data.trim() === "" || data.trim() === "null"){
		            throw new Error("Empty response");
		        }

	        const song = JSON.parse(data);
	        if(song) loadSong(song);
	    })
	    .catch(error => {
	        console.error(error);
	        fetch("/api/songs/random")
	        .then(res => res.json())
	        .then(song => { if(song) loadSong(song); });
	    });
		//fetch("/api/queue/get").then(r=>r.json()).then(q=>console.log(q))
}



// ======================================================
// PREVIOUS SONG
// ======================================================

function prevSong(){

    fetch("/api/songs/previous")

    .then(response => response.json())

    .then(song => {

        loadSong(song);

    })

    .catch(error => {

        console.error(error);

    });
}

// ======================================================
// AUTO NEXT
// ======================================================

globalAudioPlayer.addEventListener(
    "ended",
    () => {

        nextSong();
    }
);

// ======================================================
// PLAY / PAUSE
// ======================================================

function togglePlay(){

    if(!globalAudioPlayer.src){

        return;
    }

    if(globalAudioPlayer.paused){

        globalAudioPlayer.play();

    }else{

        globalAudioPlayer.pause();
    }
}

// ======================================================
// PLAY ALL SONGS
// ======================================================

function playAllSongs(button){

    const playlistId =
        button.getAttribute("data-playlistid");

    // LOAD PLAYLIST INTO QUEUE
    fetch(
        "/api/queue/loadPlaylist?playlistId=" + playlistId,
        { method:"POST" }
    )
    .then(() => {

        // FETCH FIRST SONG FROM QUEUE
        return fetch("/api/songs/next");
    })
    .then(response => response.json())
    .then(song => {

        if(song){
            loadSong(song);
        }
    })
    .catch(error => console.error(error));
}

// ======================================================
// TOGGLE SHUFFLE
// ======================================================

function toggleShuffle(){

    window.isShuffle =
        !window.isShuffle;

    localStorage.setItem(
        "isShuffle",
        window.isShuffle
    );

    const shuffleBtn =
        document.getElementById(
            "shuffleBtn"
        );

    if(shuffleBtn){

        shuffleBtn.classList.toggle(
            "active",
            window.isShuffle
        );
    }
}

// ======================================================
// TOGGLE REPEAT
// ======================================================

function toggleRepeat(){

    window.isRepeat =
        !window.isRepeat;

    localStorage.setItem(
        "isRepeat",
        window.isRepeat
    );

    globalAudioPlayer.loop =
        window.isRepeat;

    const repeatBtn =
        document.getElementById(
            "repeatBtn"
        );

    if(repeatBtn){

        repeatBtn.classList.toggle(
            "active",
            window.isRepeat
        );
    }
}