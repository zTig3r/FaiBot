package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.EmbedCreator.getEmbed;
import static de.ztiger.faibot.utils.Lang.getLang;

@SuppressWarnings("ConstantConditions")
public class Inventory {

    public static void sendInventory(SlashCommandInteractionEvent event) {
        Map<String, String> contents = new HashMap<>();
        List<String> memberColors = new ArrayList<>(getter.getInventory(event.getMember().getId()));

        if (memberColors.isEmpty()) contents.put("field", getLang("inventory.noItems"));
        else memberColors.forEach(color -> {
            String c = colors.get(color).translation;
            contents.put("field" + c, c);
        });

        event.replyEmbeds(getEmbed("inventory", contents)).setEphemeral(true).queue();
    }
}
