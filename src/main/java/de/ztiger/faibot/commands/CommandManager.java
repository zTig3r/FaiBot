package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.ztiger.faibot.commands.ChangeColor.*;
import static de.ztiger.faibot.commands.Daily.sendDailyReward;
import static de.ztiger.faibot.commands.Inventory.sendInventory;
import static de.ztiger.faibot.commands.Leaderboard.*;
import static de.ztiger.faibot.commands.ServerStats.setupStats;
import static de.ztiger.faibot.commands.Shop.*;
import static de.ztiger.faibot.commands.Stats.sendStats;
import static de.ztiger.faibot.utils.TwitchHandler.triggerLive;
import static de.ztiger.faibot.utils.TwitchHandler.triggerOff;
import static de.ztiger.faibot.utils.YoutubeHandler.triggerVideoCheck;

@SuppressWarnings("ConstantConditions")
public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "stats" -> sendStats(event);
            case "color" -> sendColorEmbed(event);
            case "leaderboard" -> sendLeaderboardEmbed(event);
            case "daily" -> sendDailyReward(event);
            case "inventory" -> sendInventory(event);
            case "shop" -> sendShopEmbed(event);

            case "setupstats" -> setupStats(event);
            case "starttwitch" -> triggerLive(event);
            case "endtwitch" -> triggerOff(event);
            case "checkyoutube" -> triggerVideoCheck(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getButton().getId();

        switch (id) {
            case "nameColor" -> colorEmbed(event, true);
            case "statsColor", "cancel" -> colorEmbed(event, false);
            case "back" -> sendColorEmbed(event);
            case "apply" -> applyStatsColor(event);
            case "next" -> next(event);
            case "return" -> back(event);
            case "BUYcancel" -> sendShopEmbed(event);
            case "BUYconfirm" -> handleBuy(event);
            default -> {
                if (id.startsWith("BUY")) handleShopEmbed(event);
                else {
                    boolean isName = id.startsWith("NAME");

                    if (id.contains("reset")) handleReset(event, isName);
                    else handleColor(event, isName);
                }
            }
        }
    }
}
