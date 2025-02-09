package mods.thecomputerizer.discord.serverstalker;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import mods.thecomputerizer.discord.serverstalker.message.MessageHandler;
import mods.thecomputerizer.discord.serverstalker.util.MonoHelper;
import mods.thecomputerizer.discord.serverstalker.voice.VoiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static mods.thecomputerizer.discord.serverstalker.StalkerRef.NAME;

public class ServerStalker {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NAME+" Main");
    private static final String TOKEN = System.getenv("bot.token");
    
    static GatewayDiscordClient buildGateway() {
        LOGGER.debug("Building gateway with token {}",TOKEN);
        final GatewayDiscordClient gateway = DiscordClientBuilder.create(TOKEN).build().login().block();
        if(Objects.isNull(gateway)) LOGGER.error("Client gateway failed to initialize!");
        return gateway;
    }
    
    static void finalizeGateway(final GatewayDiscordClient gateway) {
        MonoHelper.combine(setPresence(gateway),gateway.onDisconnect()).block();
    }
    
    static ClientPresence getPresence() {
        return ClientPresence.online(ClientActivity.playing("around with this server").withState("ლ(｀ー´ლ)"));
    }
    
    static void initializeHandlers(GatewayDiscordClient gateway) {
        MessageHandler.init(gateway);
        VoiceHandler.init(gateway);
    }
    
    public static void main(String ... args) {
        final GatewayDiscordClient gateway = buildGateway();
        if(Objects.nonNull(gateway)) {
            onGatewayCreated(gateway);
            initializeHandlers(gateway);
            finalizeGateway(gateway);
        }
    }
    
    static void onGatewayCreated(final GatewayDiscordClient gateway) {
        gateway.updatePresence(getPresence());
        gateway.on(ReadyEvent.class).subscribe(ServerStalker::onReady);
    }
    
    static void onReady(ReadyEvent event) {
        final User self = event.getSelf();
        LOGGER.info("Logged in as {}",self.getUsername());
    }
    
    static Mono<Void> setPresence(GatewayDiscordClient gateway) {
        return gateway.updatePresence(getPresence());
    }
}
