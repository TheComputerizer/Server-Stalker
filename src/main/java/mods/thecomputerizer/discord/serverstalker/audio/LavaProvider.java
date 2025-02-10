package mods.thecomputerizer.discord.serverstalker.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import discord4j.voice.AudioProvider;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import mods.thecomputerizer.discord.serverstalker.audio.track.TrackListener;
import org.slf4j.Logger;

import java.nio.ByteBuffer;

import static com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats.DISCORD_OPUS;

public class LavaProvider extends AudioProvider {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Lava Provider");
    
    private final AudioPlayer player;
    private final MutableAudioFrame frame;
    
    protected LavaProvider(AudioPlayer player, AudioHandler handler) {
        super(ByteBuffer.allocate(DISCORD_OPUS.maximumChunkSize()));
        this.player = player;
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(getBuffer());
        player.addListener(TrackListener.get(handler));
    }
    
    public AudioPlayer getPlayer() {
        return this.player;
    }
    
    @Override public boolean provide() {
        // AudioPlayer writes audio data to its AudioFrame
        // If audio was provided, flip from write-mode to read-mode
        if(this.player.provide(this.frame)) {
            getBuffer().flip();
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if the volume was set to a new value
     */
    public boolean setVolume(float percent) {
        int oldVolume = this.player.getVolume();
        int volume = (int)(Math.min(Math.max(percent,0f),2f)*100f);
        if(volume!=oldVolume) {
            this.player.setVolume(volume);
            LOGGER.debug("Seting output volume to {}%",volume);
            return true;
        }
        return false;
    }
}
