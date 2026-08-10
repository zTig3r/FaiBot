package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface IModalHandler extends IComponentHandler {
    void modalInteraction(ModalInteractionEvent event);
}
