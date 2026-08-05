package de.ztiger.faibot.listeners;

import de.ztiger.faibot.interactions.components.IModalHandler;
import de.ztiger.faibot.interactions.idea.IdeaCmd;
import de.ztiger.faibot.interactions.nixos.NixosCmd;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ComponentListener extends ListenerAdapter {

    private final Map<String, IModalHandler> modalHandlers = new HashMap<>();

    public ComponentListener() {
        registerModalHandler(new IdeaCmd());
        registerModalHandler(new NixosCmd());
    }

    public void registerModalHandler(IModalHandler handler) {
        modalHandlers.put(handler.getModalId().toLowerCase(), handler);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        IModalHandler handler = modalHandlers.get(event.getModalId().toLowerCase());
        if (handler != null) {
            handler.modalInteraction(event);
        }
    }
}
