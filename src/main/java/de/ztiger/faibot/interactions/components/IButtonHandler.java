package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButtonHandler {
    String getComponentPrefix();

    void handleButton(ButtonInteractionEvent event, String action, String payload);
}