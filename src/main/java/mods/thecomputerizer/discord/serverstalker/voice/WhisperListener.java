package mods.thecomputerizer.discord.serverstalker.voice;

import discord4j.voice.AudioReceiver;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import org.slf4j.Logger;
import reactor.util.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static java.lang.Short.MAX_VALUE;

@SuppressWarnings("deprecation")
public class WhisperListener extends AudioReceiver {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Whisper Listener");
    
    private float[] buffer;
    private int read;
    private int packetCount;
    
    public WhisperListener() {
        this.buffer = new float[32*512]; //Should be like 1-3 seconds
    }
    
    @Override public void receive(char sequence, int timestamp, int ssrc, @NonNull byte[] audio) {
        float[] translated = translateAudioBytes(audio);
        if(this.read+translated.length>=this.buffer.length) {
            LOGGER.info("Attempting to parse text from {} floats",this.buffer.length);
            String text = WhisperWrapper.getInstance().transcribe(this.buffer);
            LOGGER.info("Parsed text is {}",text);
            this.buffer = new float[this.buffer.length];
            this.read = 0;
        }
        System.arraycopy(translated,0,this.buffer,this.read,translated.length);
        this.read+=translated.length;
        this.packetCount++;
        if(this.packetCount%50==0) {
            LOGGER.debug("Received {}th packet with length {}",this.packetCount,this.read);
            this.packetCount = 0;
        }
    }
    
    float[] translateAudioBytes(byte[] audioBytes) {
        ByteBuffer capture = ByteBuffer.wrap(audioBytes);
        ShortBuffer buffer = capture.asShortBuffer();
        float[] samples = new float[capture.capacity()/2];
        int i=0;
        while(buffer.hasRemaining())
            samples[i++] = Float.max(-1f,Float.min(((float)buffer.get())/(float)MAX_VALUE,1f));
        return samples;
    }
}
