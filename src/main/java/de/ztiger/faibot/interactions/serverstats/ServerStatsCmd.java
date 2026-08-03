package de.ztiger.faibot.interactions.serverstats;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.utils.GuildProvider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Map;

import static de.ztiger.faibot.FaiBot.logger;
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
        GuildProvider.getMainGuild().ifPresent(guild -> {
            if (!guild.getCategoriesByName(name, true).isEmpty()) {
                event.reply(getLang(KEY + "error")).setEphemeral(true).queue();
                return;
            }

            guild.createCategory(name).complete();

            int members = guild.getMemberCount();
            int bots = guild.getMembersWithRoles(guild.getRolesByName("Bots", true).getFirst()).size();

            guild.createVoiceChannel(format(KEY + "all", Map.of("members", members + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();
            guild.createVoiceChannel(format(KEY + "members", Map.of("members", members - bots + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();
            guild.createVoiceChannel(format(KEY + "bots", Map.of("bots", bots + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();

            logger.info("Created Server Stats category");
            event.reply(getLang(KEY + "success")).setEphemeral(true).queue();
        });
    }
}
