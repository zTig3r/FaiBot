package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.localization.keys.Log;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberLeave extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();

        ChannelProvider.sendEmbed(BotChannel.LOG, memberLeave(user.getAsMention(), user.getEffectiveName(), user.getId(), user.getAvatarUrl()));
    }

    private static MessageEmbed memberLeave(String tag, String name, String userId, String avatarUrl) {
        return BotEmbed.error()
                .title(Localization.get(Log.MemberLeave.TITLE))
                .normalField(tag + " " + name)
                .footer(Localization.format(Log.MemberJoin.FOOTER, "userId", userId))
                .withTimestamp()
                .thumbnail(avatarUrl)
                .build();
    }
}
