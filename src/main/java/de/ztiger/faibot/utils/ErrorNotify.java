package de.ztiger.faibot.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import de.ztiger.faibot.config.BotChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

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

        String stackTrace = null;
        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        if (throwableProxy != null) {
            stackTrace = ThrowableProxyUtil.asString(throwableProxy);
        }

        ChannelProvider.sendEmbed(BotChannel.LOG, errorEmbed(message, stackTrace));
    }

    private static MessageEmbed errorEmbed(String message, String stackTrace) {
        BotEmbed embed = BotEmbed.error()
                .normalField(message)
                .withTimestamp();

        if (stackTrace != null && !stackTrace.isBlank()) {
            stackTrace = stackTrace.substring(0, 350) + "\n... [truncated]";

            embed.field("Stack Trace", "```java\n" + stackTrace + "\n```", false);
        }

        return embed.build();
    }
}