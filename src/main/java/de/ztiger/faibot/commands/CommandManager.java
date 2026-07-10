package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandManager() {
        register(new ChangeColor());
        register(new Inventory());
        register(new Leaderboard());
        register(new Shop());
        register(new Stats());

        register(new Nixos());
        register(new ServerStats());
        register(new Twitch());
        register(new Youtube());
    }

    private void register(ICommand cmd) {
        commands.put(cmd.getCommandData().getName(), cmd);
    }

    public List<CommandData> getCommandDataList() {
        return commands.values().stream().map(ICommand::getCommandData).collect(Collectors.toList());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        ICommand cmd = commands.get(event.getName());
        if (cmd == null) return;

        cmd.executeSlash(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getButton().getCustomId();

        if (id.contains(":")) {
            String prefix = id.split(":")[0];
            ICommand cmd = commands.get(prefix);
            if (cmd != null) {
                cmd.executeButton(event);
                return;
            }
        }

        for (ICommand cmd : commands.values()) {
            cmd.executeButton(event);
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getComponentId();

        if (id.contains(":")) {
            String prefix = id.split(":")[0];
            ICommand cmd = commands.get(prefix);
            if (cmd != null) {
                cmd.executeSelect(event);
                return;
            }
        }

        for (ICommand cmd : commands.values()) {
            cmd.executeSelect(event);
        }
    }
}
