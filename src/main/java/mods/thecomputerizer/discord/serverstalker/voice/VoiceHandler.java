package mods.thecomputerizer.discord.serverstalker.voice;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.voice.LocalVoiceReceiveTaskFactory;
import discord4j.voice.VoiceConnection;
import io.github.givimad.whisperjni.WhisperJNI;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import mods.thecomputerizer.discord.serverstalker.audio.AudioHandler;
import mods.thecomputerizer.discord.serverstalker.util.GuildHelper;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

public class VoiceHandler {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Voice");
    
    private static VoiceConnection currentConnection;
    
    public static @Nullable VoiceConnection getConnection() {
        if(Objects.isNull(currentConnection)) {
            LOGGER.warn("Tried to get voice connection before joining a voice channel!");
            return null;
        }
        return currentConnection;
    }
    
    public static void init(GatewayDiscordClient gateway) {
        try {
            WhisperJNI.loadLibrary();
            WhisperJNI.setLibraryLogger(null);
        } catch(IOException ex) {
            LOGGER.error("Failed to load Whisper wrapper",ex);
        }
    }
    
    public static boolean isConnected() {
        if(Objects.isNull(currentConnection)) return false;
        if(TRUE.equals(currentConnection.isConnected().block())) return true;
        LOGGER.warn("Connection was set without being connected to a voice channel!");
        currentConnection = null;
        return false;
    }
    
    public static boolean joinFirstChannel(@Nullable Guild guild) {
        return joinChannel(GuildHelper.getFirstChannelOfType(guild, VoiceChannel.class));
    }
    
    public static boolean joinChannel(@Nullable Guild guild, String id) {
        if(Objects.isNull(id) || id.isBlank()) return false;
        GuildChannel channel = GuildHelper.getChannelByID(guild,id);
        return channel instanceof VoiceChannel && joinChannel((VoiceChannel)channel);
    }
    
    public static boolean joinChannel(@Nullable VoiceChannel channel) {
        if(Objects.isNull(channel)) return false;
        try {
            AudioChannelJoinSpec spec = AudioChannelJoinSpec.builder()
                    .provider(AudioHandler.getInstance().getLavaProvider())
                    .receiver(new WhisperListener())
                    .receiveTaskFactory(new LocalVoiceReceiveTaskFactory())
                    .build();
            currentConnection = channel.join(spec).block();
            return true;
        } catch(Throwable t) {
            LOGGER.error("Error joining voice channel {}",channel,t);
            currentConnection = null;
        }
        return false;
    }
    
    public static boolean disconnect() {
        VoiceConnection connection = getConnection();
        if(Objects.nonNull(connection)) {
            connection.disconnect().subscribe(ignored -> LOGGER.debug("Successfully disconnected from voice channel"));
            currentConnection = null;
            return true;
        }
        LOGGER.error("Failed to disconnect from voice");
        return false;
    }
}