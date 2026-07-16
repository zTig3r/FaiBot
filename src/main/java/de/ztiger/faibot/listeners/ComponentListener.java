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

    private final Map<String, IButtonHandler> handlers = new HashMap<>();

    public ComponentListener() {
        registerHandler(new ColorCmd());
        registerHandler(new LeaderboardCmd());
        registerHandler(new ShopCmd());
    }

    public void registerHandler(IButtonHandler handler) {
        handlers.put(handler.getComponentPrefix().toLowerCase(), handler);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String customId = event.getButton().getCustomId();

        String[] parts = customId.split(":", 2);
        if (parts.length < 2) return;

        String prefix = parts[0].toLowerCase();
        String action = parts[1];

        IButtonHandler handler = handlers.get(prefix);
        if (handler != null) {
            handler.handleButton(event, action, "");
        }
    }
    /*
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponentId();

        if (id.contains(":")) {
            String prefix = id.split(":")[0];
            ISelectHandler cmd = commands.get(prefix);
            if (cmd != null) {
                cmd.executeSelect(event);
                return;
            }
        }

        for (ICommandHandler cmd : commands.values()) {
            cmd.executeSelect(event);
        }
    }*/
}
