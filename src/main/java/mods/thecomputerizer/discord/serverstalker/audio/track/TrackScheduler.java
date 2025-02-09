package mods.thecomputerizer.discord.serverstalker.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import mods.thecomputerizer.discord.serverstalker.audio.AudioHandler;
import org.slf4j.Logger;

public class TrackScheduler implements AudioLoadResultHandler {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Track Scheduler");
    
    public static TrackScheduler get(AudioHandler handler) {
        return new TrackScheduler(handler);
    }
    
    private final AudioHandler handler;
    
    private TrackScheduler(AudioHandler handler) {
        this.handler = handler;
    }
    
    public AudioPlayer getPlayer() {
        return this.handler.getProvider().getPlayer();
    }
    
    @Override public void trackLoaded(AudioTrack track) {
        getPlayer().playTrack(track);
    }
    
    @Override public void playlistLoaded(AudioPlaylist playlist) {
        LOGGER.error("Tried to load a playlist but that isn't implemented yet");
        this.handler.setNotPlaying();
    }
    
    @Override public void noMatches() {
        LOGGER.error("No matches found!");
        this.handler.setNotPlaying();
    }
    
    @Override public void loadFailed(FriendlyException ex) {
        LOGGER.error("Load failed!",ex);
        this.handler.setNotPlaying();
    }
}