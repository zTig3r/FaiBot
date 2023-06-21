package de.ztiger.faibot.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import static de.ztiger.faibot.FaiBot.*;

@SuppressWarnings("rawtypes")
public class YoutubeHandler {

    private static String videoId;

    public static void checkVideo() {
        HttpsURLConnection connection;
        try {
            URL url = new URL("https://youtube.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=1&playlistId=UU2YqG8Bc1RAncad0AFKuhtA&key=" + config.get("YOUTUBE_KEY") + "&maxResults=1&order=date&type=video");

            connection = (HttpsURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            JSONObject json = new JSONObject(sb.toString());

            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.contains("items")) {
                    JSONArray jsonArray = new JSONArray(json.get(key).toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("snippet");
                    videoId = jsonObject.getJSONObject("resourceId").getString("videoId");
                }
            }

            sendVideoEmbed();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendVideoEmbed() {
        if (getter.getLastVideo().equals(videoId)) return;
        youtubeChannel.sendMessage("@everyone Neues Video von **Izi Fit:** \n\rhttps://youtu.be/" + videoId).queue();
        setter.setLastVideo(videoId);
        logger.info("New video posted: " + videoId);
    }

    public static void triggerVideoCheck(SlashCommandInteractionEvent event) {
        checkVideo();
        event.reply("Videostatus wurde erfolgreich überprüft!").setEphemeral(true).queue();
    }
}
