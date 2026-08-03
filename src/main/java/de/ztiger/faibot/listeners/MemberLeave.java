package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Map;

import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;

public class MemberLeave extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();

        Map<String, String> contents = Map.of("tag", user.getAsMention(), "name", user.getEffectiveName(), "id", user.getId(), "img", user.getAvatarUrl());

        ChannelProvider.sendEmbed(BotChannel.LOG, getEmbed("memberLeave", contents, Color.RED));
    }
}
