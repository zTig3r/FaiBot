package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface IAutoCompleteHandler {
    void handleAutoComplete(CommandAutoCompleteInteractionEvent event);
}
