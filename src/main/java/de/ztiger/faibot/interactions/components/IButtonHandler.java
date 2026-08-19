package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface IButtonHandler extends IComponentHandler {
    void handleButton(ButtonInteractionEvent event);

    default String getComponentId(ButtonInteractionEvent event) {
        Button button = event.getButton();
        return button.getCustomId() != null ? button.getCustomId() : "";
    }

    default boolean hasAction(ButtonInteractionEvent event, String action) {
        return getComponentId(event).contains(action);
    }
}
