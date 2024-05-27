package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;

import static de.ztiger.faibot.FaiBot.logChannel;

public class MemberLeave extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor("Member left", null, user.getAvatarUrl())
                .setThumbnail(user.getAvatarUrl())
                .addField("\u00A0", user.getAsMention() + " " + user.getEffectiveName(), false)
                .setFooter("ID: " + user.getId())
                .setTimestamp(OffsetDateTime.now())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }
}
