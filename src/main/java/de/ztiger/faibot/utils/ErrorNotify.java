package de.ztiger.faibot.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.logChannel;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;

public class ErrorNotify extends AppenderBase<ILoggingEvent> {

    private static final List<String> errors = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        String message = eventObject.getFormattedMessage();
        if (errors.contains(message)) return;

        errors.add(message);
        logChannel.sendMessageEmbeds(getEmbed("error", Map.of("msg", message), Color.RED)).queue();
    }
}
