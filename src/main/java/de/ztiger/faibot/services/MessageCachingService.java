package de.ztiger.faibot.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class MessageCachingService {

    private final Cache<Long, CachedMessage> CACHE = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build();

    public record CachedMessage(long id, long channelId, long authorId, String content, long timestamp) {
        public static CachedMessage fromJDA(Message message) {
            return new CachedMessage(
                    message.getIdLong(),
                    message.getChannel().getIdLong(),
                    message.getAuthor().getIdLong(),
                    message.getContentRaw(),
                    message.getTimeCreated().toInstant().toEpochMilli()
            );
        }
    }

    public void add(Message message) {
        CACHE.put(message.getIdLong(), CachedMessage.fromJDA(message));
    }

    public void remove(long messageId) {
        CACHE.invalidate(messageId);
    }

    public CachedMessage get(long messageId) {
        return CACHE.getIfPresent(messageId);
    }
}