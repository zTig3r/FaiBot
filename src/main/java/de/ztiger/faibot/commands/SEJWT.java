package de.ztiger.faibot.commands;

import de.ztiger.faibot.utils.SEHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;

import static de.ztiger.faibot.FaiBot.*;

public class SEJWT {

    @SuppressWarnings("ConstantConditions")
    public static void setStreamelementsToken(SlashCommandInteractionEvent event) {
        FileConfiguration config = cfgm.getConfig("config");
        String token = event.getOption("token").getAsString();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(getSEData(token))) {
                String res = EntityUtils.toString(response.getEntity());
                int startIdx = res.indexOf("_id");

                config.set("channelID", res.substring(startIdx, res.indexOf("\",", startIdx)).strip().replace("_id\":\"", ""));
            }
        } catch (IOException e) {
            logger.error("Error retrieving SE-ID", e);
        }

        config.set("jwt", token);
        cfgm.saveConfig("config");
        seHandler = new SEHandler();

        logger.info("Set Streamelement JWT-Token");
        event.reply("Streamelements JWT-Token erfolgreich gesetzt").setEphemeral(true).queue();
    }

    private static @NotNull HttpGet getSEData(String token) {
        HttpGet request = new HttpGet("https://api.streamelements.com/kappa/v2/channels/me");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Bearer " + token);
        request.addHeader("Content-Type", "application/json");
        return request;
    }
}
