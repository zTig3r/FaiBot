package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.format;

public class MemberJoin extends ListenerAdapter {

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();

        String age = user.getTimeCreated().toString().split("T")[0];
        Period period = Period.between(LocalDate.parse(age, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());
        String ageString = period.getYears() + " year" + (period.getYears() != 1 ? "s" : "") + ", " +
                period.getMonths() + " month" + (period.getMonths() != 1 ? "s" : "") + ", " +
                period.getDays() + " day" + (period.getDays() != 1 ? "s" : "");

        Map<String, String> contents = Map.of("tag", user.getAsMention(), "name", user.getEffectiveName(), "age", ageString, "id", user.getId(), "img", user.getAvatarUrl());

        if (!getter.userExists(user.getId())) setter.addUser(event.getUser().getId());

        welcomeChannel.sendMessage(format("welcomeMessage", Map.of("user", user.getAsMention()))).queue();
        logChannel.sendMessageEmbeds(getEmbed("memberJoin", contents, Color.GREEN)).queue();
    }
}
