package mods.thecomputerizer.discord.serverstalker.message;

import discord4j.core.object.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MessageCommand {
    
    private static final String DEFAULT_PREFIX = "!";
    private static final List<MessageCommand> REGISTERED_MESSAGES = new ArrayList<>();
    
    public static boolean query(Message message) {
        for(MessageCommand command : REGISTERED_MESSAGES)
            if(command.executeIfValid(message)) return true;
        return false;
    }
    
    public static void register(String name, BiFunction<Message,String,Boolean> executor) {
        register(DEFAULT_PREFIX,name,true,executor);
    }
    
    public static void register(String prefix, String name, BiFunction<Message,String,Boolean> executor) {
        new MessageCommand(prefix,name,true,executor);
    }
    
    public static void register(String name, boolean strict, BiFunction<Message,String,Boolean> executor) {
        register(DEFAULT_PREFIX,name,strict,executor);
    }
    
    public static void register(String prefix, String name, boolean strict, BiFunction<Message,String,Boolean> executor) {
        new MessageCommand(prefix,name,strict,executor);
    }
    
    private final String prefix;
    private final String name;
    private final boolean strict;
    private final BiFunction<Message,String,Boolean> executor;
    
    private MessageCommand(String prefix, String name, boolean strict, BiFunction<Message,String,Boolean> executor) {
        this.prefix = prefix;
        this.name = name;
        this.strict = strict;
        this.executor = executor;
        REGISTERED_MESSAGES.add(this);
    }
    
    private boolean checkName(String name) {
        return this.strict ? this.name.equals(name) : name.startsWith(this.name);
    }
    
    private boolean executeIfValid(Message message) {
        String content = message.getContent();
        if(!content.startsWith(this.prefix)) return false;
        String name = content.substring(content.indexOf(this.prefix)+this.prefix.length());
        int split = name.indexOf(' ');
        if(!checkName(split==-1 ? name : name.substring(0,split))) return false;
        String args = split==-1 ? "" : name.substring(split+1);
        return this.executor.apply(message,args);
    }
}