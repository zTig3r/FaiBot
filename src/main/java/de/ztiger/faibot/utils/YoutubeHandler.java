package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;

import static de.ztiger.faibot.FaiBot.*;

public class YoutubeHandler {
    public static void checkVideo() {
        JSONObject json = readFromUrl("https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&playlistId=UU2YqG8Bc1RAncad0AFKuhtA&key=" + config.get("YOUTUBE_KEY") + "&maxResults=1&order=date&type=video");
        if(json == null) return;

        try {
            String videoId = json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");

            String lastVideo = getter.getLastVideo();
            if (lastVideo == null || lastVideo.equals(videoId)) return;
            setter.setLastVideo(videoId);

            JSONObject videoInfo = readFromUrl("https://youtube.googleapis.com/youtube/v3/videos?part=contentDetails&naxResults=1&id=" + videoId + "&key=" + config.get("YOUTUBE_KEY"));
            if (videoInfo == null) return;

            String duration = videoInfo.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration");
            if(Duration.parse(duration).getSeconds() < 60) return;

            sendVideoEmbed(videoId);
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    private static JSONObject readFromUrl(String url) {
        try {
            URL u = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) u.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            return new JSONObject(sb.toString());
        } catch (IOException | JSONException e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    private static void sendVideoEmbed(String videoId) {
        youtubeChannel.sendMessage("@everyone Neues Video von **Izi Fit:** \n\rhttps://youtu.be/" + videoId).queue();
        logger.info("New video posted: {}", videoId);
    }

    public static void triggerVideoCheck(SlashCommandInteractionEvent event) {
        checkVideo();
        event.reply("Videostatus wurde erfolgreich überprüft!").setEphemeral(true).queue();
    }
}
