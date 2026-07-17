package de.ztiger.faibot.listeners;

import de.ztiger.faibot.interactions.color.ColorCmd;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.interactions.components.ISelectHandler;
import de.ztiger.faibot.interactions.leaderboard.LeaderboardCmd;
import de.ztiger.faibot.interactions.shop.ShopCmd;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ComponentListener extends ListenerAdapter {

    private final Map<String, IButtonHandler> buttonHandlers = new HashMap<>();
    private final Map<String, ISelectHandler> selectHandlers = new HashMap<>();

    public ComponentListener() {
        registerButtonHandler(new ColorCmd());
        registerButtonHandler(new LeaderboardCmd());
        registerButtonHandler(new ShopCmd());

        registerSelectHandler(new ColorCmd());
    }

    public void registerButtonHandler(IButtonHandler handler) {
        buttonHandlers.put(handler.getComponentPrefix().toLowerCase(), handler);
    }

    public void registerSelectHandler(ISelectHandler handler) {
        selectHandlers.put(handler.getComponentPrefix().toLowerCase(), handler);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String customId = event.getButton().getCustomId();
        if (customId == null) return;

        String[] parts = customId.split(":");
        if (parts.length < 2) return;

        String prefix = parts[0].toLowerCase();
        String action = parts[1];
        String payload = "";

        if (parts.length == 3) payload = parts[2];

        IButtonHandler handler = buttonHandlers.get(prefix);
        if (handler != null) {
            handler.handleButton(event, action, payload);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponentId();

        if (id.contains(":")) {
            String prefix = id.split(":")[0];
            ISelectHandler cmd = selectHandlers.get(prefix);
            if (cmd != null) {
                cmd.executeSelect(event);
                return;
            }
        }

        for (ISelectHandler cmd : selectHandlers.values()) {
            cmd.executeSelect(event);
        }
    }
}
