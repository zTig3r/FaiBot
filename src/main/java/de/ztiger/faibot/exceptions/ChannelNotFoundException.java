package de.ztiger.faibot.exceptions;

import de.ztiger.faibot.config.BotChannel;

public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException(BotChannel channel, String cause) {
        super(String.format("Failed to resolve channel '%s' (%s): %s", channel.name(), channel.getConfigKey(), cause));
    }
}
