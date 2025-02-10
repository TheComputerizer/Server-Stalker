package mods.thecomputerizer.discord.serverstalker.voice;

import discord4j.voice.AudioReceiver;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import org.slf4j.Logger;
import reactor.util.annotation.NonNull;

import java.nio.ByteBuffer;

@SuppressWarnings("deprecation")
public class WhisperListener extends AudioReceiver {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Whisper Listener");
    
    @Override public void receive(char sequence, int timestamp, int ssrc, @NonNull byte[] audio) {
        String text = WhisperWrapper.getInstance().transcribe(byteToFloat(audio));
        LOGGER.info("Received text `{}`",text);
        if("leave".equalsIgnoreCase(text)) VoiceHandler.disconnect();
    }
    
    float[] byteToFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).asFloatBuffer().array();
    }
}
