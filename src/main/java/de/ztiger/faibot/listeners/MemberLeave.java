package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;

public class MemberLeave extends ListenerAdapter {

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();

        Map<String, String> contents = Map.of("tag", user.getAsMention(), "name", user.getEffectiveName(), "id", user.getId(), "img", user.getAvatarUrl());

        logChannel.sendMessageEmbeds(getEmbed("memberLeave", contents, Color.RED)).queue();
    }
}
