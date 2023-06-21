package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.ztiger.faibot.FaiBot.botChannel;
import static de.ztiger.faibot.commands.Inventory.sendInventory;
import static de.ztiger.faibot.commands.ChangeColor.*;
import static de.ztiger.faibot.commands.Daily.sendDailyReward;
import static de.ztiger.faibot.commands.Leaderboard.*;
import static de.ztiger.faibot.commands.ServerStats.setupStats;
import static de.ztiger.faibot.commands.Shop.*;
import static de.ztiger.faibot.commands.Stats.sendStats;
import static de.ztiger.faibot.utils.TwitchHandler.triggerLiveEmbed;
import static de.ztiger.faibot.utils.TwitchHandler.triggerOffEmbed;
import static de.ztiger.faibot.utils.YoutubeHandler.triggerVideoCheck;

@SuppressWarnings("ConstantConditions")
public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getChannel().equals(botChannel)) {
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("Bitte benutze diesen Befehl in " + botChannel.getAsMention() + "!").setEphemeral(true).queue();
                return;
            }
        }

        switch (event.getName()) {
            case "stats" -> sendStats(event);
            case "color" -> sendColorEmbed(event);
            case "leaderboard" -> sendLeaderboardEmbed(event);
            case "daily" -> sendDailyReward(event);
            case "inventory" -> sendInventory(event);
            case "shop" -> sendShopEmbed(event);

            case "setupStats" -> setupStats(event);
            case "starttwitch" -> triggerLiveEmbed(event);
            case "endtwitch" -> triggerOffEmbed(event);
            case "checkyoutube" -> triggerVideoCheck(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getButton().getId();

        if (id.contains("BUY")) {
            if (id.contains("cancel")) sendShopEmbed(event);
            else if (id.contains("confirm")) handleBuy(event);
            else handleShopEmbed(event);
            return;
        }

        switch (id) {
            case "nameColor" -> nameColorEmbed(event);
            case "statsColor", "cancel" -> statsColorEmbed(event);
            case "back" -> sendColorEmbed(event);
            case "apply" -> applyColor(event);
            case "next" -> next(event);
            case "return" -> back(event);
            default -> changeColor(event);
        }
    }
}
