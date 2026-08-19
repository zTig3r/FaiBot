package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.General;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class MemberJoin extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final LocalizationService i18n;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();

        String age = user.getTimeCreated().toString().split("T")[0];
        Period period = Period.between(LocalDate.parse(age, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.now());
        String ageString = i18n.formatPeriod(period);

        channelProvider.sendMessage(BotChannel.WELCOME, i18n.format(General.WELCOME_MESSAGE, "user", user.getAsMention()));

        channelProvider.sendComponent(BotChannel.LOG, memberJoin(user.getAsMention(), user.getName(), ageString, user.getId(),
                                                                 user.getEffectiveAvatarUrl()));
    }

    private Container memberJoin(String tag, String name, String age, String userId, String avatarUrl) {
        return Container.of(
                Section.of(
                        Thumbnail.fromUrl(avatarUrl),
                        TextDisplay.of(i18n.get(Log.Member.Join.TITLE)),
                        TextDisplay.of(tag + " " + name),
                        TextDisplay.of(i18n.format(Log.Member.Join.AGE, "age", age))
                ),
                TextDisplay.of(i18n.format(Log.Member.FOOTER, "userid", userId))
        ).withAccentColor(Color.GREEN);
    }
}
