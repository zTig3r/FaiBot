package de.ztiger.faibot.commands;

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

public class Inventory implements ICommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("inventory", "Zeigt dein Inventar an");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        Map<String, String> contents = new HashMap<>();
        List<String> items = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        if (items.isEmpty()) contents.put("field", getLang("inventory.noItems"));
        else items.forEach(item -> {
            String itemT = colors.get(item).translation;
            contents.put("field" + itemT, itemT);
        });

        event.replyEmbeds(getEmbed("inventory", contents)).setEphemeral(true).queue();
    }
}
