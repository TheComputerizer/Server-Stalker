package mods.thecomputerizer.discord.serverstalker.message;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import mods.thecomputerizer.discord.serverstalker.StalkerRef;
import mods.thecomputerizer.discord.serverstalker.audio.AudioHandler;
import mods.thecomputerizer.discord.serverstalker.util.GuildHelper;
import mods.thecomputerizer.discord.serverstalker.util.RandomHelper;
import mods.thecomputerizer.discord.serverstalker.util.ResourceHelper;
import mods.thecomputerizer.discord.serverstalker.voice.VoiceHandler;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageHandler {
    
    private static final Logger LOGGER = StalkerRef.getLogger("Messages");
    private static final List<String> PING_RESPONSES = new ArrayList<>();
    
    private static String selfUsername;
    
    static boolean checkPing(Message message) {
        for(User user : message.getUserMentions()) {
            LOGGER.debug("Checking mention {} against self {}",user.getUsername(),selfUsername);
            if(selfUsername.equals(user.getUsername()))
                return true;
        }
        return false;
    }
    
    public static void init(GatewayDiscordClient gateway) {
        readPingResponses();
        registerCommands();
        gateway.on(MessageCreateEvent.class).subscribe(MessageHandler::onMessage);
    }
    
    static void onMessage(MessageCreateEvent event) {
        Message message = event.getMessage();
        if(checkPing(message) && !PING_RESPONSES.isEmpty()) {
            VoiceChannel channel = GuildHelper.getMemberVoiceChannel(message.getAuthorAsMember().block());
            if(Objects.nonNull(channel)) VoiceHandler.joinChannel(channel);
            else {
                String response = RandomHelper.getElement(PING_RESPONSES);
                if(!response.isEmpty()) {
                    sendResponse(message,response);
                    return;
                }
            }
        }
        MessageCommand.query(message);
    }
    
    static void readPingResponses() {
        List<String> responses = ResourceHelper.getResponses("ping");
        if(responses.isEmpty()) LOGGER.warn("No ping responses were read in!");
        else {
            LOGGER.info("Adding {} ping responses",responses.size());
            PING_RESPONSES.addAll(responses);
        }
    }
    
    static void registerCommands() {
        MessageCommand.register("ping",(message,args) -> {
            if(!args.isEmpty()) return false;
            sendResponse(message,"Pong!");
            return true;
        });
        MessageCommand.register("join",(message,args) -> {
            Guild guild = message.getGuild().block();
            return args.isBlank() ? VoiceHandler.joinFirstChannel(guild) :
                    VoiceHandler.joinChannel(guild,args.split(" ")[0]);
        });
        MessageCommand.register("leave",(ignored1,ignored2) -> VoiceHandler.disconnect());
        MessageCommand.register("volume",(message,args) -> {
            float volume = args.isBlank() ? 1f : Float.parseFloat(args.trim());
            if(AudioHandler.setVolume(volume))
                sendResponse(message,"Set volume to "+AudioHandler.getVolume()+"%");
            return true;
        });
        MessageCommand.register("play",(message,args) -> {
            if(VoiceHandler.isConnected()) {
                if(AudioHandler.play(args)) {
                    sendResponse(message,"Successfully queued audio from `"+args+"`");
                    return true;
                }
                sendResponse(message,"Failed to queue audio from `"+args+"`");
            } else sendResponse(message,"Connect me to a voice channel before doing that");
            return false;
        });
        MessageCommand.register("stop",(message,ignored) -> {
            if(VoiceHandler.isConnected()) {
                if(AudioHandler.stop()) {
                    sendResponse(message,"Stopped playing audio");
                    return true;
                }
                sendResponse(message,"No audio was playing to stop but ok");
            } else sendResponse(message,"How about you stop instead");
            return false;
        });
    }
    
    static void sendResponse(Message message, String response) {
        MessageChannel channel = message.getChannel().block();
        if(Objects.nonNull(channel)) {
            LOGGER.info("Sending {} in channel {}",response,channel.getData().name().get());
            channel.createMessage(response).block();
        }
        else LOGGER.error("MessageChannel was somehow null?");
    }
    
    public static void setSelfUsername(String username) {
        selfUsername = username;
    }
}