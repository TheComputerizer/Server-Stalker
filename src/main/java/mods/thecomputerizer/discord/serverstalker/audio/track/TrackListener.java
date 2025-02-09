package mods.thecomputerizer.discord.serverstalker.audio.track;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import mods.thecomputerizer.discord.serverstalker.audio.AudioHandler;

public class TrackListener extends AudioEventAdapter {
    
    public static TrackListener get(AudioHandler handler) {
        return new TrackListener(handler);
    }
    
    private final AudioHandler handler;
    
    private TrackListener(AudioHandler handler) {
        this.handler = handler;
    }
    
    @Override public void onEvent(AudioEvent event) {
        if(event instanceof TrackEndEvent || event instanceof TrackExceptionEvent)
            this.handler.setNotPlaying();
        else if(event instanceof TrackStartEvent) this.handler.setPlaying();
    }
    
    @Override public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        this.handler.setNotPlaying();
    }
    
    @Override public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException ex) {
        this.handler.setNotPlaying();
    }
    
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        this.handler.setPlaying();
    }
}