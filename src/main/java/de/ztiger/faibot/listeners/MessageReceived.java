package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.MessageCachingService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class MessageReceived extends ListenerAdapter {

    private final ChannelProvider channelProvider;
    private final MessageCachingService messageCachingService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Channel channel = event.getChannel();
        Message message = event.getMessage();

        if (channelProvider.isChannel(channel, BotChannel.RECOMMENDATIONS) && message.getType() == MessageType.POLL_RESULT) {
            message.delete().queue();
            return;
        }

        if (event.getAuthor().isBot()) return;

        if (channelProvider.isChannel(channel, BotChannel.LOG)) return;

        messageCachingService.add(message);

        if (channelProvider.isChannel(channel, BotChannel.REACTION)) {
            message.addReaction(Emoji.fromUnicode("✅")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();
        }
    }
}
