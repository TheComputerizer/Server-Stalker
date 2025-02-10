package mods.thecomputerizer.discord.serverstalker.voice;

import io.github.givimad.whisperjni.WhisperContext;
import io.github.givimad.whisperjni.WhisperFullParams;
import io.github.givimad.whisperjni.WhisperJNI;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class WhisperWrapper {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Whisper");
    
    private static WhisperWrapper INSTANCE;
    
    public static WhisperWrapper getInstance() {
        if(Objects.isNull(INSTANCE)) INSTANCE = new WhisperWrapper();
        return INSTANCE;
    }
    
    private final WhisperJNI whisper;
    private WhisperContext context;
    private WhisperFullParams parameters;
    
    private WhisperWrapper() {
        this.whisper = new WhisperJNI();
    }
    
    public void close() {
        if(Objects.nonNull(this.context)) this.context.close();
    }
    
    private @Nullable WhisperContext getContext() {
        if(Objects.nonNull(this.context)) return this.context;
        try {
            this.context = this.whisper.init(Path.of(System.getProperty("user.home"),"ggml-tiny.bin"));
        } catch(IOException ex) {
            LOGGER.error("Failed to initialize whisper context",ex);
        }
        return this.context;
    }
    
    private WhisperFullParams getParameters() {
        if(Objects.isNull(this.parameters)) this.parameters = new WhisperFullParams();
        return this.parameters;
    }
    
    private String getText(WhisperContext context) {
        this.whisper.fullNSegments(context);
        return this.whisper.fullGetSegmentText(context,0);
    }
    
    private int listen(float[] samples) {
        int result = this.whisper.full(getContext(), getParameters(), samples, samples.length);
        if(result!=0) throw new RuntimeException("Failed to transcribe audio");
        return result;
    }
    
    public String transcribe(float[] samples) {
        listen(samples);
        return getText(getContext());
    }
}
