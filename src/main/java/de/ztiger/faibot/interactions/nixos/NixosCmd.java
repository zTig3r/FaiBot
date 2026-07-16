package de.ztiger.faibot.interactions.nixos;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class NixosCmd implements ICommand {
    @Override
    public CommandData getCommandData() {
        return Commands.slash("nixos", "Postet eine Nachricht mit den Statistiken der aktuellen Nixo-Season")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        // TODO: Implement
    }
}
