package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.ztiger.faibot.utils.MessageCachingService.add;

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
    }
}
