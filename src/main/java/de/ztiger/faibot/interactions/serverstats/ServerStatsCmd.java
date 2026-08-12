package de.ztiger.faibot.interactions.serverstats;

import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.localization.keys.Serverstats;
import de.ztiger.faibot.services.LocalizationService;
import de.ztiger.faibot.utils.GuildProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ServerStatsCmd implements ICommand {

    private final GuildProvider guildProvider;
    private final LocalizationService i18n;

    @Override
    public CommandData getCommandData() {
        return Commands.slash("setupstats", i18n.get(Serverstats.Command.DESCRIPTION)).setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        String name = i18n.get(Serverstats.TITLE);

        guildProvider.getMainGuild().ifPresent(guild -> {
            if (!guild.getCategoriesByName(name, true).isEmpty()) {
                event.reply(i18n.get(Serverstats.ERROR)).setEphemeral(true).queue();
                return;
            }

            int total = guild.getMemberCount();
            int bots = getBotCount(guild);
            int humans = total - bots;

            guild.createCategory(name).queue(category -> {
                category.createVoiceChannel(i18n.format(Serverstats.ALL, "members", total)).queue();
                category.createVoiceChannel(i18n.format(Serverstats.MEMBERS, "members", humans)).queue();
                category.createVoiceChannel(i18n.format(Serverstats.BOTS, "bots", bots)).queue();

                log.info("Created Server Stats category");
                event.reply(i18n.get(Serverstats.SUCCESS)).setEphemeral(true).queue();
            });
        });
    }

    public Runnable updateServerStats() {
        return () -> guildProvider.getMainGuild().ifPresent(guild -> {
            String categoryName = i18n.get(Serverstats.TITLE);
            List<Category> categories = guild.getCategoriesByName(categoryName, true);

            if (categories.isEmpty()) return;

            var voiceChannels = categories.getFirst().getVoiceChannels();
            if (voiceChannels.size() < 3) return;

            int total = guild.getMemberCount();
            if (voiceChannels.getFirst().getName().contains(String.valueOf(total))) return;

            int bots = getBotCount(guild);
            int humans = total - bots;

            voiceChannels.get(0).getManager().setName(i18n.format(Serverstats.ALL, "members", total)).queue();
            voiceChannels.get(1).getManager().setName(i18n.format(Serverstats.MEMBERS, "members", humans)).queue();
            voiceChannels.get(2).getManager().setName(i18n.format(Serverstats.BOTS, "bots", bots)).queue();

            log.info("Updated Server Stats");
        });
    }

    private int getBotCount(Guild guild) {
        return (int) guild.getMembers().stream().filter(m -> m.getUser().isBot()).count();
    }
}