package de.ztiger.faibot.listeners;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IAutoCompleteHandler;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.interactions.components.IModalHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InteractionListener extends ListenerAdapter {

    private final Map<String, ICommand> commands = new HashMap<>();
    private final Map<String , IAutoCompleteHandler> autoCompleteHandlers = new HashMap<>();
    private final Map<String, IButtonHandler> buttonHandlers = new HashMap<>();
    private final Map<String, IModalHandler> modalHandlers = new HashMap<>();

    public InteractionListener(ICommand... instances) {
        for (ICommand cmd : instances) {
            register(cmd);
        }
    }

    public void register(ICommand cmd) {
        commands.put(cmd.getCommandData().getName(), cmd);

        if (cmd instanceof IAutoCompleteHandler autoCompleteHandler) {
            autoCompleteHandlers.put(cmd.getCommandData().getName(), autoCompleteHandler);
        }
        if (cmd instanceof IButtonHandler buttonHandler) {
            buttonHandlers.put(buttonHandler.getComponentId(), buttonHandler);
        }
        if (cmd instanceof IModalHandler modalHandler) {
            modalHandlers.put(modalHandler.getComponentId(), modalHandler);
        }
    }

    public List<CommandData> getCommandDataList() {
        return commands.values().stream().map(ICommand::getCommandData).collect(Collectors.toList());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        ICommand cmd = commands.get(event.getName());
        if (cmd != null) cmd.executeSlash(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        IAutoCompleteHandler handler = autoCompleteHandlers.get(event.getName());
        if (handler != null) handler.handleAutoComplete(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String prefix = event.getComponentId().split(":")[0];
        IButtonHandler handler = buttonHandlers.get(prefix);
        if (handler != null) handler.handleButton(event);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String prefix = event.getModalId().split(":")[0];
        IModalHandler handler = modalHandlers.get(prefix);
        if (handler != null) handler.modalInteraction(event);
    }
}
