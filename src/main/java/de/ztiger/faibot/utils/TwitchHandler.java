package de.ztiger.faibot.utils;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static de.ztiger.faibot.FaiBot.*;

@SuppressWarnings("FieldMayBeFinal")
public class TwitchHandler {

    private static String clientID = config.get("CLIENT_ID");
    private static String clientSecret = config.get("CLIENT_SECRET");
    private static String title = "Fienix und Izio sind nun Live!";
    private static String profileImage = "https://static-cdn.jtvnw.net/jtv_user_pictures/8f9b0e0e-1b1a-4b1a-9b1a-8f9b0e0e1b1a-profile_image-300x300.png";
    private static int viewers = 0;
    private static String messageID, duration, offlineImage, game;
    private static TwitchClient client;

    private static Timer timer;
    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            updateEmbed();
        }
    };


    public TwitchHandler() {
        client = TwitchClientBuilder.builder()
                .withClientId(clientID)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .build();

        User user = client.getHelix().getUsers(null, null, List.of("fienix_and_izio")).execute().getUsers().get(0);

        offlineImage = user.getOfflineImageUrl();
        profileImage = user.getProfileImageUrl();

        client.getClientHelper().enableStreamEventListener("fienix_and_izio");
        client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
            updateValues();

            sendLiveEmbed();

            timer = new Timer();
            timer.schedule(task, 300000, 1800000);

            logger.info("TwitchHandler: Stream is now live");
        });

        client.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> updateEmbed());

        client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
            logger.info("TwitchHandler: Stream is now offline");

            sendOffEmbed();

            duration = "";
            timer.cancel();
        });

        logger.info("TwitchHandler initialized");
    }

    private static Stream getStream() {
        StreamList list = client.getHelix().getStreams(null, null, null, null, null, null, null, List.of("fienix_and_izio")).execute();
        if (list.getStreams().isEmpty()) return null;
        return list.getStreams().get(0);
    }

    private static void updateDuration(Duration timestamp) {
        long hours = timestamp.toHours();
        long minutes = timestamp.toMinutes() % 60;

        StringBuilder result = new StringBuilder();

        if (hours != 0) {
            result.append(hours).append(" Stunde");
            if (hours != 1) result.append("n");
        }

        if (minutes != 0) {
            if (result.length() > 0) result.append(" ");
            result.append(minutes).append(" Minute");
            if (minutes != 1) result.append("n");
        }

        duration = result.toString();
    }

    private static MessageEmbed createEmbed() {
        updateValues();
        String previewURL = "https://static-cdn.jtvnw.net/previews-ttv/live_user_fienix_and_izio-1280x720.jpg";
        return new EmbedBuilder()
                .setAuthor("fienix_and_izio", null, profileImage)
                .setThumbnail(profileImage)
                .setTitle(title, "https://www.twitch.tv/fienix_and_izio")
                .addField("Aktuelles Spiel: ", game, true)
                .addField("Zuschauer: ", String.valueOf(viewers), true)
                .addField("Dauer: ", duration, true)
                .setImage(previewURL + "?state=" + System.currentTimeMillis())
                .setColor(Color.decode("#6441a4"))
                .build();
    }


    private static void sendOffEmbed() {
        String id = client.getHelix().getUsers(null, null, List.of("fienix_and_izio")).execute().getUsers().get(0).getId();
        VideoList list = client.getHelix().
                getVideos(null, null, id, null, null, null, null, Video.Type.ARCHIVE, null, null, null).execute();

        String[] split = list.getVideos().get(0).getDuration().split("[hms]");

        updateDuration(Duration.ofHours(Integer.parseInt(split[0])).plusMinutes(Integer.parseInt(split[1])));

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("fienix_and_izio", null, profileImage)
                .setThumbnail(profileImage)
                .setDescription("Danke fürs Zuschauen! - Der Stream lief " + duration)
                .setImage(offlineImage)
                .setColor(Color.decode("#6441a4"))
                .build();

        twitchChannel.editMessageById(messageID, " ").setEmbeds(embed).queue();
    }

    private static void updateValues() {
        Stream stream = getStream();
        if (stream == null) return;

        viewers = stream.getViewerCount();
        title = stream.getTitle();
        game = stream.getGameName();
        updateDuration(stream.getUptime());
    }

    private static void updateEmbed() {
        twitchChannel.editMessageEmbedsById(messageID).setEmbeds(createEmbed()).queue();
    }

    private static void sendLiveEmbed() {
        twitchChannel.sendMessage("Hey @everyone, Fienix und Izio sind nun Live. Schaut gerne mal vorbei ^^").setEmbeds(createEmbed()).queue((message) -> messageID = message.getId());
    }

    public static void triggerLiveEmbed(SlashCommandInteractionEvent event) {
        sendLiveEmbed();
        event.reply("Live-Embed wurde gesendet!").setEphemeral(true).queue();
    }

    public static void triggerOffEmbed(SlashCommandInteractionEvent event) {
        sendOffEmbed();
        event.reply("Offline-Embed wurde gesendet!").setEphemeral(true).queue();
    }
}
