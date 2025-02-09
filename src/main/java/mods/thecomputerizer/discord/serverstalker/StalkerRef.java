package mods.thecomputerizer.discord.serverstalker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StalkerRef {
    
    public static final String NAME = "Server Stalker";
    public static final String VERSION = "0.0.1";
    
    public static Logger getLogger(String type) {
        return LoggerFactory.getLogger(NAME+" "+type);
    }
}