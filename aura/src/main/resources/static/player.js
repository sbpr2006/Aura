// GLOBAL AUDIO OBJECT

if (!window.globalAudioPlayer) {

    window.globalAudioPlayer = new Audio();
}

const globalAudioPlayer =
    window.globalAudioPlayer;

// RESTORE SONG

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

    if(titleEl && savedTitle){

        titleEl.innerText =
            savedTitle;
    }

    if(artistEl && savedArtist){

        artistEl.innerText =
            savedArtist;
    }

    if(imageEl && savedImage){

        imageEl.src =
            savedImage;
    }

    if(miniPlayerImage && savedImage){

        miniPlayerImage.src =
            savedImage;
    }

    // FIXED NOW PLAYING LINK

    if(nowPlayingLink &&
       savedSongId &&
       savedSongId !== "null"){

        nowPlayingLink.href =
            "/usr/nowPlaying?songId=" +
            savedSongId;
    }

    // AUTO PLAY

    if(isPlaying === "true" &&
       savedSong){

        globalAudioPlayer.play()
            .catch(error => {

                console.log(error);

            });
    }
});

// SAVE TIME

globalAudioPlayer.addEventListener(
    "timeupdate",
    () => {

        localStorage.setItem(
            "currentTime",
            globalAudioPlayer.currentTime
        );
    }
);

// SAVE STATE

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

// PLAY SONG FUNCTION

function playSong(button) {

    const songId =
        button.getAttribute("data-songid");

    const title =
        button.getAttribute("data-title");

    const artist =
        button.getAttribute("data-artist");

    const image =
        button.getAttribute("data-image");

    const audio =
        button.getAttribute("data-audio");

    // UPDATE PLAYER UI

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

    if(titleEl){

        titleEl.innerText = title;
    }

    if(artistEl){

        artistEl.innerText = artist;
    }

    if(imageEl){

        imageEl.src = image;
    }

    if(miniPlayerImage){

        miniPlayerImage.src = image;
    }

    // CLEAN AUDIO PATH

    let cleanPath =
        audio.trim().replace(/^\/+/, "");

    // PLAY AUDIO

    globalAudioPlayer.src =
        "/" + cleanPath;

    globalAudioPlayer.load();

    globalAudioPlayer.play()

        .then(() => {

            // SAVE SONG DATA

            localStorage.setItem(
                "currentSongId",
                songId
            );

            localStorage.setItem(
                "currentSong",
                globalAudioPlayer.src
            );

            localStorage.setItem(
                "currentTitle",
                title
            );

            localStorage.setItem(
                "currentArtist",
                artist
            );

            localStorage.setItem(
                "currentImage",
                image
            );

            localStorage.setItem(
                "currentTime",
                "0"
            );

            localStorage.setItem(
                "isPlaying",
                "true"
            );

            // UPDATE NOW PLAYING LINK

            if(nowPlayingLink){

                nowPlayingLink.href =
                    "/usr/nowPlaying?songId=" +
                    songId;
            }
        })

        .catch(error => {

            console.error(error);

        });
}