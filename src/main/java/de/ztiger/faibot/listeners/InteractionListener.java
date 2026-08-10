package de.ztiger.faibot.listeners;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IButtonHandler;
import de.ztiger.faibot.interactions.components.IModalHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InteractionListener extends ListenerAdapter {

    private final Map<String, ICommand> commands = new HashMap<>();
    private final Map<String, IButtonHandler> buttonHandlers = new HashMap<>();
    private final Map<String, IModalHandler> modalHandlers = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(InteractionListener.class);

    public InteractionListener(ICommand... instances) {
        for (ICommand cmd : instances) {
            register(cmd);
        }
    }

    public void register(ICommand cmd) {
        commands.put(cmd.getCommandData().getName(), cmd);

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

        logger.info("Handling slash command: {}", event.getName());

        if (cmd != null) cmd.executeSlash(event);
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
