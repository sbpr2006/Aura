// GLOBAL AUDIO OBJECT
window.globalAudioPlayer = new Audio();

// RESTORE SONG
window.addEventListener("load", () => {

    const savedSong   = localStorage.getItem("currentSong");
    const savedTitle  = localStorage.getItem("currentTitle");
    const savedArtist = localStorage.getItem("currentArtist");
    const savedImage  = localStorage.getItem("currentImage");
    const savedTime   = localStorage.getItem("currentTime");
    const isPlaying   = localStorage.getItem("isPlaying");

    if(savedSong){

        globalAudioPlayer.src = savedSong;

        globalAudioPlayer.currentTime = savedTime || 0;

        // UPDATE UI IF ELEMENTS EXIST
        const titleEl  = document.getElementById("playerTitle");
        const artistEl = document.getElementById("playerArtist");
        const imageEl  = document.getElementById("playerImage");

        if(titleEl)  titleEl.innerText = savedTitle;
        if(artistEl) artistEl.innerText = savedArtist;
        if(imageEl)  imageEl.src = savedImage;

        if(isPlaying === "true"){
            globalAudioPlayer.play();
        }
    }
});

// SAVE TIME
globalAudioPlayer.addEventListener("timeupdate", () => {

    localStorage.setItem(
        "currentTime",
        globalAudioPlayer.currentTime
    );
});

// SAVE STATE
globalAudioPlayer.addEventListener("play", () => {
    localStorage.setItem("isPlaying", "true");
});

globalAudioPlayer.addEventListener("pause", () => {
    localStorage.setItem("isPlaying", "false");
});