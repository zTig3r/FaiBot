package de.ztiger.faibot.listeners;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.utils.ChannelProvider;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.ztiger.faibot.utils.MessageCachingService.add;

@RequiredArgsConstructor
public class MessageReceived extends ListenerAdapter {


    private final ChannelProvider channelProvider;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(channelProvider.isChannel(event.getChannel(), BotChannel.RECOMMENDATIONS) && event.getMessage().getType() == MessageType.POLL_RESULT) {
            event.getMessage().delete().queue();
            return;
        }

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        Channel channel = event.getChannel();

        if (channelProvider.isChannel(channel, BotChannel.LOG)) return;

        add(message);

        if (channelProvider.isChannel(channel, BotChannel.REACTION)) {
            message.addReaction(Emoji.fromUnicode("✅")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();
        }
    }
}
