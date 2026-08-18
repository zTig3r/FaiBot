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

    // Send no errors in development environment (ENV=dev)
    private static final boolean IS_DEV = "dev".equalsIgnoreCase(System.getenv("ENV"));

    private final ChannelProvider channelProvider;

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
        String formattedStackTrace = null;
        if (stackTrace != null) {
            int maxLen = 350;
            String snippet = stackTrace.substring(0, Math.min(stackTrace.length(), maxLen));
            formattedStackTrace = "```java\n" + snippet + (stackTrace.length() > maxLen ? "\n... [truncated]" : "") + "```";
        }

        return Container.of(
                TextDisplay.of(message),
                TextDisplay.of(formattedStackTrace != null ? formattedStackTrace : "No stack trace available")
        ).withAccentColor(Color.RED);

    }
}