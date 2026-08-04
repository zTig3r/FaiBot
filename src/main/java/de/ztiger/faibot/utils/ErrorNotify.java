package de.ztiger.faibot.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import de.ztiger.faibot.config.BotChannel;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ErrorNotify extends AppenderBase<ILoggingEvent> {

    private static final List<String> errors = new ArrayList<>();

    // Send no errors in development environment (ENV=dev)
    private static final boolean IS_DEV = "dev".equalsIgnoreCase(System.getenv("ENV"));

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (IS_DEV) return;

        String message = eventObject.getFormattedMessage();

        if (errors.contains(message)) return;

        errors.add(message);

        String stackTrace;
        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        if (throwableProxy != null) {
            stackTrace = ThrowableProxyUtil.asString(throwableProxy);
        } else {
            stackTrace = null;
        }


        ChannelProvider.getChannel(BotChannel.LOG).ifPresent(channel -> {
            channel.sendMessageComponents(error(message, stackTrace)).useComponentsV2().queue();
        });
    }

    private Container error(String message, String stackTrace) {
        return Container.of(
                TextDisplay.of(message),
                TextDisplay.of(stackTrace != null ? "```java\n" + stackTrace.substring(0, 350) + "\n... [truncated]```" : "No stack trace available")
        ).withAccentColor(Color.RED);

    }
}