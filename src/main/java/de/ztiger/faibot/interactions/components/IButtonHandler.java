package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButtonHandler extends IComponentHandler {
    void handleButton(ButtonInteractionEvent event, String action, String payload);
}