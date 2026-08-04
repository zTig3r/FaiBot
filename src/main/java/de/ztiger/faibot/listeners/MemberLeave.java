package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MemberLeave extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();

        ChannelProvider.getChannel(BotChannel.LOG).ifPresent(channel -> {
            channel.sendMessageComponents(memberLeave(user.getAsMention(), user.getEffectiveName(), user.getId(), user.getEffectiveAvatarUrl())).useComponentsV2().queue();
        });
    }

    private Container memberLeave(String tag, String name, String userId, String avatarUrl) {
        return Container.of(
                Section.of(
                        Thumbnail.fromUrl(avatarUrl),
                        TextDisplay.of(Localization.get(Log.Member.Leave.TITLE)),
                        TextDisplay.of(tag + " " + name)
                ),
                TextDisplay.of(Localization.format(Log.Member.FOOTER, "userId", userId))
        ).withAccentColor(Color.RED);
    }
}
