package mods.thecomputerizer.discord.serverstalker.message;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static mods.thecomputerizer.discord.serverstalker.StalkerRef.NAME;

public class MessageHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NAME+" Messages");
    
    public static void init(GatewayDiscordClient gateway) {
        gateway.on(MessageCreateEvent.class).subscribe(MessageHandler::onMessage);
    }
    
    static void onMessage(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        if("!ping".equals(content)) {
            MessageChannel channel = message.getChannel().block();
            if(Objects.nonNull(channel)) {
                LOGGER.info("Sending Pong! in channel {}",channel.getData().name().get());
                channel.createMessage("Pong!").block();
            }
            else LOGGER.error("MessageChannel was somehow null?");
        }
    }
}
