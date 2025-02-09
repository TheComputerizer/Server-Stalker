package mods.thecomputerizer.discord.serverstalker.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import discord4j.voice.AudioProvider;
import mods.thecomputerizer.discord.serverstalker.audio.track.TrackScheduler;

import java.util.Objects;

import static com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry.DEFAULT_REGISTRY;
import static com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.OPUS_QUALITY_MAX;
import static com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality.HIGH;

public class AudioHandler {
    
    private static AudioHandler INSTANCE;
    
    public static AudioHandler getInstance() {
        if(Objects.isNull(INSTANCE)) INSTANCE = new AudioHandler();
        return INSTANCE;
    }
    
    public AudioProvider getLavaProvider() {
        return getInstance().getProvider();
    }
    
    /**
     * Returns the percentage value as an int
     */
    public static int getVolume() {
        return getInstance().getProvider().getPlayer().getVolume();
    }
    
    public static void init() {
        getInstance();
    }
    
    public static boolean setVolume(float percent) {
        return getInstance().getProvider().setVolume(percent);
    }
    
    public static boolean play(String item) {
        String s = item.startsWith("<") && item.endsWith(">") ? item.substring(1,item.length()-1) : item;
        return getInstance().loadAndPlay(s);
    }
    
    public static boolean stop() {
        AudioHandler handler = getInstance();
        boolean playing = handler.playing;
        handler.getProvider().getPlayer().stopTrack();
        handler.playing = false;
        return playing;
    }
    
    private final AudioPlayerManager manager;
    private final LavaProvider provider;
    private TrackScheduler scheduler;
    private boolean playing;
    
    private AudioHandler() {
        this.manager = setupManager(new DefaultAudioPlayerManager());
        this.provider = new LavaProvider(this.manager.createPlayer(),this);
    }
    
    public LavaProvider getProvider() {
        return this.provider;
    }
    
    public boolean isPlaying() {
        return this.playing;
    }
    
    public boolean loadAndPlay(String item) {
        if(Objects.isNull(this.scheduler)) this.scheduler = TrackScheduler.get(this);
        this.manager.loadItem(item,this.scheduler);
        return true;
    }
    
    private void registerSources(AudioPlayerManager manager) {
        manager.registerSourceManager(new YoutubeAudioSourceManager());
        manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        manager.registerSourceManager(new BandcampAudioSourceManager());
        manager.registerSourceManager(new VimeoAudioSourceManager());
        manager.registerSourceManager(new TwitchStreamAudioSourceManager());
        manager.registerSourceManager(new BeamAudioSourceManager());
        manager.registerSourceManager(new GetyarnAudioSourceManager());
        manager.registerSourceManager(new HttpAudioSourceManager(DEFAULT_REGISTRY));
    }
    
    public void setPlaying() {
        this.playing = true;
    }
    
    public void setNotPlaying() {
        this.playing = false;
    }
    
    private AudioPlayerManager setupManager(AudioPlayerManager manager) {
        AudioConfiguration config = manager.getConfiguration();
        config.setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        config.setOpusEncodingQuality(OPUS_QUALITY_MAX);
        config.setResamplingQuality(HIGH);
        registerSources(manager);
        return manager;
    }
}