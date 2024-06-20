package de.ztiger.faibot.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.awt.*;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;

public class ErrorNotify extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        logChannel.sendMessageEmbeds(getEmbed("error", Map.of("msg", eventObject.getFormattedMessage()), Color.RED)).queue();
    }
}
