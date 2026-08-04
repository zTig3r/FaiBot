package de.ztiger.faibot.interactions.inventory;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

import static de.ztiger.faibot.FaiBot.getter;

public class InventoryCmd implements ICommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("inventory", "Zeigt dein Inventar an");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        // TODO: Get the colors from the colors config with their emoji
        List<String> items = new ArrayList<>(getter.getInventory(member.getId()));

        event.replyEmbeds(InventoryEmbeds.inventoryEmbed(items)).setEphemeral(true).queue();
    }
}
