package de.ztiger.faibot.interactions.inventory;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.getLang;

public class InventoryCmd implements ICommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("inventory", "Zeigt dein Inventar an");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        List<String> items = new ArrayList<>(getter.getInventory(member.getId()));

        Map<String, String> contents = new HashMap<>();
        if (items.isEmpty()) contents.put("field", getLang("inventory.noItems"));
        else items.forEach(item -> {
            String itemT = colors.get(item).translation;
            contents.put("field" + itemT, itemT);
        });

        event.replyEmbeds(getEmbed("inventory", contents)).setEphemeral(true).queue();
    }
}
