package de.ztiger.faibot.listeners;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.idea.IdeaCmd;
import de.ztiger.faibot.interactions.nixos.NixosCmd;
import de.ztiger.faibot.interactions.serverstats.ServerStatsCmd;
import de.ztiger.faibot.interactions.twitch.TwitchCmd;
import de.ztiger.faibot.interactions.youtube.YoutubeCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandListener extends ListenerAdapter {
    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandListener() {
        // TODO: Add recommendation commands
        register(new IdeaCmd());

        // --- Admin Commands ---
        register(new NixosCmd());
        register(new ServerStatsCmd());
        register(new TwitchCmd());
        register(new YoutubeCmd());
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
}


