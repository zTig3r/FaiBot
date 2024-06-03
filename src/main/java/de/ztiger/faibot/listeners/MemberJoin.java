package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Lang.format;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();

        String age = user.getTimeCreated().toString().split("T")[0];
        Period period = Period.between(LocalDate.parse(age, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());
        String ageString = period.getYears() + " year" + (period.getYears() != 1 ? "s" : "") + ", " +
                period.getMonths() + " month" + (period.getMonths() != 1 ? "s" : "") + ", " +
                period.getDays() + " day" + (period.getDays() != 1 ? "s" : "");

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor("Member joined", null, user.getAvatarUrl())
                .setThumbnail(user.getAvatarUrl())
                .addField("\u00A0", user.getAsMention() + " " + user.getEffectiveName(), false)
                .addField("Account Age", ageString, false)
                .setFooter("ID: " + user.getId())
                .setTimestamp(OffsetDateTime.now())
                .build();

        if(!getter.userExists(user.getId())) setter.addUser(event.getUser().getId());

        welcomeChannel.sendMessage(format("welcomeMessage", Map.of("user", user.getAsMention()))).queue();
        logChannel.sendMessageEmbeds(embed).queue();
    }
}
