package de.ztiger.faibot.interactions.components;

public interface IComponentHandler {
    String getComponentPrefix();

    default String buildId(String action, String payload) {
        String base = getComponentPrefix() + ":" + action;
        return (payload == null || payload.isEmpty()) ? base : base + ":" + payload;
    }

    default String buildId(String action) {
        return buildId(action, null);
    }
}
