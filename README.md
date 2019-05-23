# Quran align player
A Quran sound player that prints the currently pronounced words based on input from [Quran-align](https://github.com/cpfair/quran-align) files and sound from [Every Ayah](http://everyayah.com) recitations.

I used the files provided in the Quran-align repo without any modification. The main purpose of this player is to present how to use the file and builds a little on top of it.

This is a *very simple* sort of `how to use` the quran-align files, not intended for production or anything more than just testing. I haven't reviewed the align-file also nor do any error checking.

Each pronounced segment is printed to the console. 

## How to use
1) Download sample `assets` folder (you can find it in the releases tab)
2) Put the `assets` folder into your code directory so it looks like this [(download assets)](https://github.com/shakram02/Quran-align-player/releases/download/Assets/assets.zip) 
```
.
├── assets
├── lib
├── LICENSE
├── out
├── out.txt
├── quran_align_player.iml
├── README.md
└── src
```
3) Run the project

## Dependencies
- Java 8
- JavaFX (openjfx). If `MediaPlayer` class doesn't work install `ffmpeg-compat-57` from AUR. if you're on Manjaro (Arch Based) Linux
- Gson [repo](https://github.com/google/gson) -included in `lib` folder-
