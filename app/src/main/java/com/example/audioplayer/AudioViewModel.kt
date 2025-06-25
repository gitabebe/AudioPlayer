package com.example.audioplayer

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.AudioFolder
import com.example.audioplayer.data.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    // --- State Properties ---

    // UI State: Tracks the currently selected folder. If null, show the folder list.
    private val _selectedFolder = mutableStateOf<AudioFolder?>(null)
    val selectedFolder: State<AudioFolder?> = _selectedFolder

    // Player State
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentTitle = mutableStateOf("")
    val currentTitle: State<String> = _currentTitle

    // Tracks the index of the song *within the selected folder*
    private val _currentSongIndex = mutableStateOf(0)

    // --- Data ---
    val folders: List<AudioFolder>

    init {
        // Define the folder structure here.
        // Make sure you have added b1.amr, b2.amr, etc. to your res/raw folder!
        folders = listOf(
            AudioFolder(
                name = "ጸሎት ዘዘወትር",
                songs = listOf(
                    Song(title = "በስመ አብ", resId = R.raw.a01),
                    Song(title = "ነአኩተከ", resId = R.raw.a02),
                    Song(title = "አቡነ ዘበሰማያት", resId = R.raw.a03),
                    Song(title = "በሰላመ ቅዱስ ገብርኤል መልአክ", resId = R.raw.a04),
                    Song(title = "ጸሎተ ሃይማኖት", resId = R.raw.a05),
                    Song(title = "ቅዱስ ቅዱስ ቅዱስ እግዚአብሔር", resId = R.raw.a06),
                    Song(title = "እሰግድ ለአብ ወወልድ ወመንፈስ ቅዱስ", resId = R.raw.a07),
                    Song(title = "ስብሐት ለአብ ስብሐት ለወልድ ስብሐት ለመንፈስ ቅዱስ", resId = R.raw.a08),
                    Song(title = "ሰላም ለኪ", resId = R.raw.a09),
                    Song(title = "ጸሎተ እግዝእትነ ማርያም", resId = R.raw.a10)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘሰኑይ",
                songs = listOf(
                    Song(title = "ፈቀደ እግዚእ", resId = R.raw.ba01),
                    Song(title = "ሠረቀ በሥጋ", resId = R.raw.ba02),
                    Song(title = "ኢየሱስ ክርስቶስ", resId = R.raw.ba03),
                    Song(title = "ርእየ ኢሳይያስ", resId = R.raw.ba04),
                    Song(title = "ተፈሣሕ ወተሐሠይ", resId = R.raw.ba05),
                    Song(title = "ዘሀሎ ወይሄሎ", resId = R.raw.ba06),
                    Song(title = "ተፈሥሒ ኦ ቤተ ልሔም", resId = R.raw.ba07),
                    Song(title = "ትትፌሣሕ ወትትሐሠይ", resId = R.raw.ba08),
                    Song(title = "ብርሃን ዘበአማን", resId = R.raw.ba09)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘሠሉስ",
                songs = listOf(
                    Song(title = "አክሊለ ምክሕነ", resId = R.raw.bb01),
                    Song(title = "እስመ በፈቃዱ", resId = R.raw.bb02),
                    Song(title = "አንቲ ውእቱ ዕፅ", resId = R.raw.bb03),
                    Song(title = "አንቲ ውእቱ ገራህት", resId = R.raw.bb04),
                    Song(title = "ተፈሥሒ ኦ ወላዲተ እግዚእ", resId = R.raw.bb05),
                    Song(title = "ተፈስሒ እስም ድልወ", resId = R.raw.bb06),
                    Song(title = "ኦ ድንግል", resId = R.raw.bb07),
                    Song(title = " ቃለ አብ ሕያው", resId = R.raw.bb08),
                    Song(title = "ውእቱኬ", resId = R.raw.bb09),
                    Song(title = "ዕበያ ለድንግል", resId = R.raw.bb10),
                    Song(title = "ዘውእቱ እብን", resId = R.raw.bb11),
                    Song(title = "ኮንኪ ዐጽቀ", resId = R.raw.bb12),
                    Song(title = "አንቲ እሙ ለብርሃን", resId = R.raw.bb13),
                    Song(title = "አይ ልሳን", resId = R.raw.bb14),
                    Song(title = "ተፈሥሒ ኦ ማርያም", resId = R.raw.bb15)


                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘረቡዕ",
                songs = listOf(
                    Song(title = "ኩሉ ሠራዊተ ሰማያት", resId = R.raw.bc01),
                    Song(title = "ኩሉ ትውልድ", resId = R.raw.bc02),
                    Song(title = "አንቲ ዘበአማን", resId = R.raw.bc03),
                    Song(title = "ረከብኪ ጸጋ", resId = R.raw.bc04),
                    Song(title = "ግብረ ድንግል", resId = R.raw.bc05),
                    Song(title = "የዐቢ ክብራ ለማርያም", resId = R.raw.bc06),
                    Song(title = "ሕዝቅኤል ነቢይ", resId = R.raw.bc07),
                    Song(title = "ኆኅትሰ ድንግል", resId = R.raw.bc08)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘሐሙስ",
                songs = listOf(
                    Song(title = "እፀ እንተ ርእየ ሙሴ", resId = R.raw.bd01),
                    Song(title = "እፀ እንተ ርእየ ሙሴ", resId = R.raw.bd02),
                    Song(title = "ኦ ዝ መንክር ወዕፁብ", resId = R.raw.bd03),
                    Song(title = "ኦ ዝ መንክር ነሥአ", resId = R.raw.bd04),
                    Song(title = "መሐለ እግዚአብሔር", resId = R.raw.bd05),
                    Song(title = "ዳዊት ዘነግሠ", resId = R.raw.bd06),
                    Song(title = "አሐዱ ዘእምቅድስት ሥላሴ", resId = R.raw.bd07)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘአርብ",
                songs = listOf(
                    Song(title = "ቡርክት አንቲ እማንስት", resId = R.raw.be01),
                    Song(title = "ለኪ ለባሕቲትኪ", resId = R.raw.be02),
                    Song(title = "ቡርክት አንቲ ማርያም ወቡሩክ ፍሬ ከርስኪ", resId = R.raw.be03),
                    Song(title = "ማርያም ድንግል ሙዳየ ዕፍረት", resId = R.raw.be04),
                    Song(title = "ማርያም ንጽሕት ድንግል ወላዲተ አምላክ", resId = R.raw.be05),
                    Song(title = "ማርያም ድንግል ትጸርሕ", resId = R.raw.be06)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘቀዳሚት",
                songs = listOf(
                    Song(title = "ንጽሕት ወብርሕት", resId = R.raw.bf01),
                    Song(title = "ተፈሥሒ ኦ ምልእተ ጸጋ ተፈሥሒ እስመ ረከብኪ", resId = R.raw.bf02),
                    Song(title = "ከመ ከብካብ", resId = R.raw.bf03),
                    Song(title = "አንቲ ውእቱ ዘመድ ", resId = R.raw.bf04),
                    Song(title = "ኪንኪ ዳግሚተ ሰማይ", resId = R.raw.bf05),
                    Song(title = "አንቲ ውእቱ ደብተራ", resId = R.raw.bf06),
                    Song(title = "ተሰመይኪ እመ ለክርስቶስ", resId = R.raw.bf07),
                    Song(title = "አንቲ ውእቱ ሰዋስው", resId = R.raw.bf08),
                    Song(title = "ናሁ እግዚእ", resId = R.raw.bf09),
                    Song(title = "ተፈሥሒ ኦ ምልእተ ጸጋ ድንግል ዘእንበለ ርኩስ", resId = R.raw.bf10)
                )
            ),
            AudioFolder(
                name = "ውዳሴ ማርያም ዘሰንበተ ክርስቲያን",
                songs = listOf(
                    Song(title = "ተሰመይኪ ፍቅርተ", resId = R.raw.bg01),
                    Song(title = "ወበእንተዝ ናዐብየኪ", resId = R.raw.bg02),
                    Song(title = "መቅደስ ዘይኪልልዋ", resId = R.raw.bg03),
                    Song(title = "አንቲ ውእቱ መሶበ ወርቅ", resId = R.raw.bg04),
                    Song(title = "አንቲ ውእቱ ተቅዋም", resId = R.raw.bg05),
                    Song(title = "አንቲ ውእቱ ማዕጠንት", resId = R.raw.bg06),
                    Song(title = "ተፈሥሒ ኦ ማርያም ርግብ ሠናይት", resId = R.raw.bg07),
                    Song(title = "በትረ አሮን", resId = R.raw.bg08),
                    Song(title = "ለኪ ይደሉ", resId = R.raw.bg09)
                )
            ),
            AudioFolder(
                name = "አንቀጸ ብርሃን",
                songs = listOf(
                    Song(title = "ውዳሴ ወግናይ ለእመ አዶናይ ቅድስት ወብፅዕት", resId = R.raw.c01),
                    Song(title = "በእንተ ተሠግዎቱ", resId = R.raw.c02),
                    Song(title = "ቀዲሙ ዜነወነ", resId = R.raw.c03),
                    Song(title = "አንቲ ውእቱ ንጽሕት", resId = R.raw.c04),
                    Song(title = "ገብርኤል መልአክ", resId = R.raw.c05),
                    Song(title = "አንቲ ውእቱ ዘኮንኪ ጽርሐ", resId = R.raw.c06),
                    Song(title = "አስተማሰልናኪ", resId = R.raw.c07),
                    Song(title = "አንቲ ውእቱ ተቅዋም ዘወርቅ", resId = R.raw.c08),
                    Song(title = "እግዚአ ኩሉ", resId = R.raw.c09),
                    Song(title = "ናስተማስለኪ", resId = R.raw.c10),
                    Song(title = "አንቲ ውእቱ ዕፅ ቡሩክ", resId = R.raw.c11),
                    Song(title = "በትረ አሮን", resId = R.raw.c12),
                    Song(title = "ለኪ ይደሉ", resId = R.raw.c13)
                )
            ),
            AudioFolder(
                name = "ይወድስዋ መላእክት",
                songs = listOf(
                    Song(title = "ይወድስዋ መላእክት", resId = R.raw.d01),
                    Song(title = "ወበሳድስ ወርህ", resId = R.raw.d02),
                    Song(title = "ይቤላ መልአክ", resId = R.raw.d03)
                )
            )

        )
    }

    // --- Public Functions for UI Interaction ---

    fun selectFolder(folder: AudioFolder) {
        _selectedFolder.value = folder
    }

    fun goBackToFolders() {
        _selectedFolder.value = null
        stopAndReleasePlayer()
    }

    fun play(songIndex: Int) {
        _selectedFolder.value?.let { folder ->
            if (songIndex < 0 || songIndex >= folder.songs.size) return

            val song = folder.songs[songIndex]
            mediaPlayer?.release()

            try {
                mediaPlayer = MediaPlayer.create(context, song.resId).apply {
                    start()
                    setOnCompletionListener { playNext() }
                }

                _isPlaying.value = true
                _currentTitle.value = song.title
                _currentSongIndex.value = songIndex
                startProgressUpdater()
            } catch (e: Exception) {
                _isPlaying.value = false
                e.printStackTrace()
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
                startProgressUpdater()
            }
        }
    }

    fun seekTo(newProgress: Float) {
        mediaPlayer?.let {
            val newPosition = (it.duration * newProgress).toInt()
            it.seekTo(newPosition)
        }
    }

    fun playNext() {
        _selectedFolder.value?.let { folder ->
            val nextIndex = (_currentSongIndex.value + 1) % folder.songs.size
            play(nextIndex)
        }
    }

    fun playPrevious() {
        _selectedFolder.value?.let { folder ->
            val previousIndex = (_currentSongIndex.value - 1 + folder.songs.size) % folder.songs.size
            play(previousIndex)
        }
    }

    // --- Private Helper Functions ---

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (_isPlaying.value) {
                mediaPlayer?.let {
                    if (it.duration > 0) {
                        _progress.value = it.currentPosition.toFloat() / it.duration
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopAndReleasePlayer() {
        _isPlaying.value = false
        mediaPlayer?.release()
        mediaPlayer = null
        _currentTitle.value = ""
        _progress.value = 0f
    }

    override fun onCleared() {
        stopAndReleasePlayer()
        super.onCleared()
    }
}
