package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static de.ztiger.faibot.FaiBot.*;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String age = event.getUser().getTimeCreated().toString().split("T")[0];
        Period period = Period.between(LocalDate.parse(age, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());
        String ageString = period.getYears() + " year" + (period.getYears() != 1 ? "s" : "") + ", " +
                period.getMonths() + " month" + (period.getMonths() != 1 ? "s" : "") + ", " +
                period.getDays() + " day" + (period.getDays() != 1 ? "s" : "");

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor("Member joined", null, event.getUser().getAvatarUrl())
                .setThumbnail(event.getUser().getAvatarUrl())
                .addField("\u00A0", event.getUser().getAsMention() + " " + event.getUser().getAsTag(), false)
                .addField("Account Age", ageString, false)
                .setFooter("ID: " + event.getUser().getId())
                .setTimestamp(OffsetDateTime.now())
                .build();

        setter.addUser(event.getUser().getId());

        welcomeChannel.sendMessage(event.getUser().getAsMention() + " Herzlich Willkommen auf Fienix und Izio's Discord Server! <:Nixo_Cool:1076155167038255225> \n\r→ Stelle dich doch Mal kurz vor. <:Nixo_Herz:795298661651054612>").queue();
        logChannel.sendMessageEmbeds(embed).queue();
    }
}
