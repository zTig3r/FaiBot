package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.General;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static de.ztiger.faibot.FaiBot.*;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();

        String age = user.getTimeCreated().toString().split("T")[0];
        Period period = Period.between(LocalDate.parse(age, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());
        String ageString = Localization.formatPeriod(period);

        if (!getter.userExists(user.getId())) setter.addUser(event.getUser().getId());

        ChannelProvider.sendMessage(BotChannel.WELCOME, Localization.format(General.WELCOME_MESSAGE, "user", user.getAsMention()));
        ChannelProvider.sendEmbed(BotChannel.LOG, memberJoin(user.getAsMention(), user.getName(), ageString, user.getId(), user.getAvatarUrl()));
    }

    private static MessageEmbed memberJoin(String tag, String name, String age, String userId, String avatarUrl) {
        return BotEmbed.success()
                .title(Localization.get(Log.MemberJoin.TITLE))
                .normalField(tag + " " + name)
                .field(Localization.get(Log.MemberJoin.AGE), age)
                .footer(Localization.format(Log.MemberJoin.FOOTER, "userId", userId))
                .withTimestamp()
                .thumbnail(avatarUrl)
                .build();
    }
}
