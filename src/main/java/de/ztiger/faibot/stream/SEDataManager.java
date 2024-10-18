package de.ztiger.faibot.stream;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.stream.HttpManager.getData;
import static de.ztiger.faibot.stream.HttpManager.putData;

public class SEDataManager {

    private static final FileConfiguration config = cfgm.getConfig("config");
    private static final String BODYFORMAT = "{\"username\": \"%s\", \"current\":%s}";
    private static final String ENDPOINT = "https://api.streamelements.com/kappa/v2/points/" + config.getString("channelID");
    private static final String JWT = config.getString("jwt");

    public static void backupData() {
        try {
            JSONArray topPoints = new JSONObject(getSEData("/top")).getJSONArray("users");
            JSONArray watchtime = new JSONObject(getSEData("/watchtime")).getJSONArray("users");

            Map<String, Integer> watchtimeMap = new HashMap<>();
            for (int j = 0; j < watchtime.length(); j++) {
                JSONObject watchtimeUser = watchtime.getJSONObject(j);
                watchtimeMap.put(watchtimeUser.getString("username"), watchtimeUser.getInt("minutes"));
            }

            for (int i = 0; i < topPoints.length() -1; i++) {
                JSONObject user = topPoints.getJSONObject(i);
                String username = user.getString("username");
                int watchTime = watchtimeMap.getOrDefault(username, 0);
                int points = user.getInt("points");

                setter.setTwitchData(username, watchTime, points);
                logger.info("Name: {} Watchtime: {} Points: {}", username, watchTime, points);
            }

            logger.info("Successfully backed up data");
        } catch (JSONException e) {
            throw new RuntimeException("Error backing up data", e);
        }
    }

    public static void fixLowPoints() {
        try {
            JSONArray userArray = new JSONObject(getSEData("/top")).getJSONArray("users");
            List<String> lowPoints = new ArrayList<>();

            for (int i = 0; i <= userArray.length() -1; i++) {
                JSONObject user = userArray.getJSONObject(i);

                if (user.getInt("points") < 100) lowPoints.add(user.getString("username"));
            }

            if (!lowPoints.isEmpty()) addPoints(lowPoints, 100);
        } catch (JSONException e) {
            throw new RuntimeException("Error fixing low points", e);
        }
    }

    public static void addPoints(List<String> names, int amount) {
        if (names.isEmpty()) return;

        String requestBody = "{\"users\": [" +
                names.stream().map(user -> String.format(BODYFORMAT, user, amount)).collect(Collectors.joining(",")) +
                "], \"mode\": \"add\"}";

        try (CloseableHttpResponse response = putData(ENDPOINT, JWT, requestBody)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) logger.info("Successfully added points: {}", requestBody);
            else logger.error("Failed to add points: {}", requestBody);
        } catch (IOException e) {
            logger.error("Error updating points", e);
        }
    }

    private static String getSEData(String category) {
        try (CloseableHttpResponse response = getData(ENDPOINT + category, JWT)) {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve " + category, e);
        }
    }
}
