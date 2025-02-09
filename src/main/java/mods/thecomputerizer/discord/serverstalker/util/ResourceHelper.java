package mods.thecomputerizer.discord.serverstalker.util;

import mods.thecomputerizer.discord.serverstalker.ServerStalker;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourceHelper {
    
    private static final Logger LOGGER = StalkerRef.getLogger("ResourceHelper");
    
    public static InputStream getResourceStream(String path) {
        return ServerStalker.class.getClassLoader().getResourceAsStream(path);
    }
    
    public static List<String> getResponses(String type) {
        final String path = "responses/"+type+".txt";
        try(InputStream stream = getResourceStream(path)) {
            return stringToLines(streamToString(stream));
        } catch(IOException ex) {
            LOGGER.error("Failed to parse responses of type {} ({})",type,path,ex);
        }
        return List.of();
    }
    
    public static String streamToString(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(),UTF_8);
    }
    
    public static List<String> stringToLines(String string) {
        return Arrays.asList(string.split(System.lineSeparator()));
    }
}