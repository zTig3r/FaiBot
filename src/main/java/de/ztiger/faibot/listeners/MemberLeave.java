package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;

import static de.ztiger.faibot.FaiBot.logChannel;

public class MemberLeave extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor("Member left", null, event.getUser().getAvatarUrl())
                .setThumbnail(event.getUser().getAvatarUrl())
                .addField("\u00A0", event.getUser().getAsMention() + " " + event.getUser().getEffectiveName(), false)
                .setFooter("ID: " + event.getUser().getId())
                .setTimestamp(OffsetDateTime.now())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }
}
