package de.ztiger.faibot.commands;

import de.ztiger.faibot.stream.SEHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.stream.HttpManager.getData;

public class SEJWT {

    @SuppressWarnings("ConstantConditions")
    public static void setStreamelementsToken(SlashCommandInteractionEvent event) {
        FileConfiguration config = cfgm.getConfig("config");
        String token = event.getOption("token").getAsString();

        try (CloseableHttpResponse response = getData("https://api.streamelements.com/kappa/v2/channels/me", token)) {
            String res = EntityUtils.toString(response.getEntity());
            int startIdx = res.indexOf("_id");

            config.set("channelID", res.substring(startIdx, res.indexOf("\",", startIdx)).strip().replace("_id\":\"", ""));
        } catch (IOException e) {
            logger.error("Error retrieving SE-ID", e);
        }

        config.set("jwt", token);
        cfgm.saveConfig("config");
        seHandler = new SEHandler();

        logger.info("Set Streamelement JWT-Token");
        event.reply("Streamelements JWT-Token erfolgreich gesetzt").setEphemeral(true).queue();
    }
}
