package de.ztiger.faibot.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import de.ztiger.faibot.config.BotChannel;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorNotify extends AppenderBase<ILoggingEvent> {

    private static final Set<String> errors = ConcurrentHashMap.newKeySet();

    private final ChannelProvider channelProvider;

    // Send no errors in development environment (ENV=dev)
    private static final boolean IS_DEV = "dev".equalsIgnoreCase(System.getenv("ENV"));

    public ErrorNotify(ChannelProvider channelProvider) {
        this.channelProvider = channelProvider;
        setName("DISCORD_ERROR_NOTIFY");
    }

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

        channelProvider.sendComponent(BotChannel.LOG, error(message, stackTrace));
    }

    private Container error(String message, String stackTrace) {
        return Container.of(
                TextDisplay.of(message),
                TextDisplay.of(stackTrace != null ? "```java\n" + stackTrace.substring(0, 350) + "\n... [truncated]```" : "No stack trace available")
        ).withAccentColor(Color.RED);

    }
}