package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static de.ztiger.faibot.FaiBot.config;
import static de.ztiger.faibot.FaiBot.logger;

public class ServerStats {
    public static final String name = "📊 SERVER STATS 📊";

    public static void setupStats(SlashCommandInteractionEvent event) {
        Guild guild = event.getJDA().getGuildById(config.get("GUILD"));

        guild.createCategory(name).complete();

        guild.createVoiceChannel("All Members: " + guild.getMemberCount())
                .setParent(guild.getCategoriesByName(name, true).get(0))
                .setUserlimit(0)
                .queue();
        guild.createVoiceChannel("Members: " + (guild.getMembers().size() - 1))
                .setParent(guild.getCategoriesByName(name, true).get(0))
                .setUserlimit(0)
                .queue();
        guild.createVoiceChannel("Bots: " + guild.getMembersWithRoles(guild.getRolesByName("Bots", true).get(0)).size())
                .setParent(guild.getCategoriesByName(name, true).get(0))
                .setUserlimit(0)
                .queue();

        logger.info("Created category Server Stats category");

        event.reply("Server Stats wurden erfolgreich eingerichtet!").setEphemeral(true).queue();
    }
}
