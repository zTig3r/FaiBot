package de.ztiger.faibot.interactions.serverstats;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.GuildProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ServerStatsCmd implements ICommand {

    private final GuildProvider guildProvider;
    private final LocalizationService i18n;

    private static final String KEY = "serverstats.";

    @Override
    public CommandData getCommandData() {
       return Commands.slash("setupstats", "Erstelle die Server Stats").setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    // TODO: Get strings from language file by enum

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        String name = i18n.get(KEY + "title");
        guildProvider.getMainGuild().ifPresent(guild -> {
            if (!guild.getCategoriesByName(name, true).isEmpty()) {
                event.reply(i18n.get(KEY + "error")).setEphemeral(true).queue();
                return;
            }

            guild.createCategory(name).complete();

            int members = guild.getMemberCount();
            int bots = guild.getMembersWithRoles(guild.getRolesByName("Bots", true).getFirst()).size();

            guild.createVoiceChannel(i18n.format(KEY + "all", Map.of("members", members + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();
            guild.createVoiceChannel(i18n.format(KEY + "members", Map.of("members", members - bots + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();
            guild.createVoiceChannel(i18n.format(KEY + "bots", Map.of("bots", bots + "")))
                    .setParent(guild.getCategoriesByName(name, true).getFirst())
                    .queue();

            log.info("Created Server Stats category");
            event.reply(i18n.get(KEY + "success")).setEphemeral(true).queue();
        });
    }
}
