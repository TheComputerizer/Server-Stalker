package mods.thecomputerizer.discord.serverstalker.util;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.CategorizableChannel;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.GuildChannel;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuildHelper {
    
    public static @Nullable GuildChannel getChannelNamed(@Nullable Guild guild, String categoryName, String name) {
        for(Category category : getChannelsOfType(guild,Category.class)) {
            if(categoryName.isBlank() || categoryName.equals(category.getName())) {
                GuildChannel channel = getChannelNamed(category,name);
                if(Objects.nonNull(channel)) return channel;
            }
        }
        return null;
    }
    
    public static @Nullable CategorizableChannel getChannelNamed(Category category, String name) {
        for(CategorizableChannel channel : category.getChannels().toIterable())
            if(channel.getData().name().get().equals(name))
                return channel;
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <C extends GuildChannel> List<C> getChannelsOfType(@Nullable Guild guild, Class<C> type) {
        if(Objects.isNull(guild)) return List.of();
        List<C> channels = new ArrayList<>();
        for(GuildChannel channel : guild.getChannels().toIterable())
            if(type.isInstance(channel))
                channels.add((C)channel);
        return channels;
    }
    
    public static <C extends GuildChannel> @Nullable C getFirstChannelOfType(@Nullable Guild guild, Class<C> type) {
        List<C> channels = getChannelsOfType(guild,type);
        return channels.isEmpty() ? null : channels.getFirst();
    }
}