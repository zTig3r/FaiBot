package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.ThreadLocalRandom;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.MessageCachingService.add;
import static de.ztiger.faibot.utils.XP.*;

public class MessageReceived extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        Channel channel = event.getChannel();

        if (ChannelProvider.isChannel(channel, BotChannel.LOG)) return;

        add(message);

        if (ChannelProvider.isChannel(channel, BotChannel.RECOMMENDATIONS) && message.getContentRaw().contains("V:") || ChannelProvider.isChannel(channel, BotChannel.REACTION)) {
            message.addReaction(Emoji.fromUnicode("✅")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();
        }

        if (ChannelProvider.isChannel(channel, BotChannel.BOT)) return;

        String id = event.getMember().getId();

        if (canGetXp(event.getMember())) {
            setter.addXP(id, ThreadLocalRandom.current().nextInt(15, 25));
            setter.addPoints(id, ThreadLocalRandom.current().nextInt(0, 3));
            checkLevelUp(event.getMember());
            addUserTimer(event.getMember());
        }

        setter.addMessage(id);
    }
}
