package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.LinkedHashMap;


@SuppressWarnings("SuspiciousMethodCalls")
public class MessageCachingService {

    private static final HashMap<String, LinkedHashMap<String, Message>> cache = new HashMap<>();

    public static void add(Message message) {
        String channel = message.getChannel().getId();

        if (!cache.containsKey(channel)) cache.put(channel, new LinkedHashMap<>());

        if (cache.get(channel).size() > 100) cache.get(channel).remove(cache.get(channel).keySet().toArray()[0]);
        cache.get(channel).put(message.getId(), message);
    }

    public static void remove(Message message) {
        if (!cache.get(message.getChannel().getId()).containsKey(message.getId())) return;

        cache.get(message.getChannel().getId()).remove(message.getId());
    }

    public static Message get(Message message) {
        if (!cache.get(message.getChannel().getId()).containsKey(message.getId())) return null;

        return cache.get(message.getChannel().getId()).get(message.getId());
    }

    public static Message get(String messageID, String channelID) {
        if (!cache.get(channelID).containsKey(messageID)) return null;

        return cache.get(channelID).get(messageID);
    }

}
