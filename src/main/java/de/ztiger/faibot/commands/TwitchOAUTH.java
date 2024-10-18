package de.ztiger.faibot.commands;

import de.ztiger.faibot.stream.SEHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.simpleyaml.configuration.file.FileConfiguration;

import static de.ztiger.faibot.FaiBot.*;

public class TwitchOAUTH {

    @SuppressWarnings("ConstantConditions")
    public static void setTwitchOAUTH(SlashCommandInteractionEvent event) {
        FileConfiguration config = cfgm.getConfig("config");

        config.set("oauth", event.getOption("token").getAsString());
        cfgm.saveConfig("config");
        seHandler = new SEHandler();

        logger.info("Set Twitch OAUTH-Token");
        event.reply("Twitch OAUTH-Token erfolgreich gesetzt").setEphemeral(true).queue();
    }
}
