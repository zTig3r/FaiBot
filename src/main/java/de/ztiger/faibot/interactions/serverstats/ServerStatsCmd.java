package de.ztiger.faibot.interactions.serverstats;

import de.ztiger.faibot.interactions.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Map;

import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.listeners.BotReady.GUILD;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class ServerStatsCmd implements ICommand {

    private static final String KEY = "serverstats.";

    @Override
    public CommandData getCommandData() {
       return Commands.slash("setupstats", "Erstelle die Server Stats").setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        String name = getLang(KEY + "title");

        if (!GUILD.getCategoriesByName(name, true).isEmpty()) {
            event.reply(getLang(KEY + "error")).setEphemeral(true).queue();
            return;
        }

        GUILD.createCategory(name).complete();

        int members = GUILD.getMemberCount();
        int bots = GUILD.getMembersWithRoles(GUILD.getRolesByName("Bots", true).get(0)).size();

        GUILD.createVoiceChannel(format(KEY + "all", Map.of("members", members + "")))
                .setParent(GUILD.getCategoriesByName(name, true).get(0))
                .queue();
        GUILD.createVoiceChannel(format(KEY + "members", Map.of("members", members - bots + "")))
                .setParent(GUILD.getCategoriesByName(name, true).get(0))
                .queue();
        GUILD.createVoiceChannel(format(KEY + "bots", Map.of("bots", bots + "")))
                .setParent(GUILD.getCategoriesByName(name, true).get(0))
                .queue();

        logger.info("Created Server Stats category");
        event.reply(getLang(KEY + "success")).setEphemeral(true).queue();
    }
}
