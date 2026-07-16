package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface ISelectHandler {
    void executeSelect(StringSelectInteractionEvent event);
}
