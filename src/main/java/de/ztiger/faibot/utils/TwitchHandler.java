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

    private static final String CHANNEL = cfgm.getConfig("config").getString("channel");
    private static String messageID, offlineImage, profileImage;
    private static TwitchClient client;

    private static Timer timer;

    public TwitchHandler() {
        client = TwitchClientBuilder.builder()
                .withClientId(config.get("CLIENT_ID"))
                .withClientSecret(config.get("CLIENT_SECRET"))
                .withEnableHelix(true)
                .build();

        User user = client.getHelix().getUsers(null, null, List.of(CHANNEL)).execute().getUsers().get(0);
        offlineImage = user.getOfflineImageUrl();
        profileImage = user.getProfileImageUrl();

        client.getClientHelper().enableStreamEventListener(CHANNEL);
        client.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> streamStart());
        client.getEventManager().onEvent(ChannelChangeGameEvent.class, event -> updateEmbed());
        client.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> streamEnd());

        logger.info("TwitchHandler initialized");
    }

    private static String formatDuration(Duration timestamp) {
        long hours = timestamp.toHours();
        long minutes = timestamp.toMinutes() % 60;
        return String.format("%d %s %d %s", hours, hours == 1 ? "Stunde" : "Stunden", minutes, minutes == 1 ? "Minute" : "Minuten");
    }

    private static MessageEmbed createEmbed() {
        Stream stream = client.getHelix().getStreams(null, null, null, null, null, null, null, List.of("fienix_and_izio")).execute().getStreams().stream().findFirst().orElse(null);
        if (stream == null) return null;

        String previewURL = "https://static-cdn.jtvnw.net/previews-ttv/live_user_fienix_and_izio-1280x720.jpg";
        return new EmbedBuilder()
                .setAuthor("fienix_and_izio", null, profileImage)
                .setThumbnail(profileImage)
                .setTitle(stream.getTitle(), "https://www.twitch.tv/fienix_and_izio")
                .addField("Aktuelles Spiel: ", stream.getGameName(), true)
                .addField("Zuschauer: ", String.valueOf(stream.getViewerCount()), true)
                .addField("Dauer: ", formatDuration(stream.getUptime()), true)
                .setImage(previewURL + "?state=" + System.currentTimeMillis())
                .setColor(Color.decode("#6441a4"))
                .build();
    }

    private static void updateEmbed() {
        twitchChannel.editMessageEmbedsById(messageID).setEmbeds(createEmbed()).queue();
    }

    private static void streamStart() {
        logger.info("TwitchHandler: Stream is now live");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateEmbed();
            }
        }, 300000, 1800000);

        twitchChannel.sendMessage("Hey @everyone, Fienix und Izio sind nun Live. Schaut gerne mal vorbei ^^").setEmbeds(createEmbed()).queue((message) -> messageID = message.getId());
    }

    private static void streamEnd() {
        logger.info("TwitchHandler: Stream is now offline");

        String id = client.getHelix().getUsers(null, null, List.of(CHANNEL)).execute().getUsers().get(0).getId();
        VideoList list = client.getHelix().
                getVideos(null, null, id, null, null, null, null, Video.Type.ARCHIVE, null, null, null).execute();

        String timestamp = list.getVideos().get(0).getDuration();
        if (!timestamp.contains("h")) timestamp = "0h" + timestamp;
        String[] split = timestamp.split("[hms]");

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("fienix_and_izio", null, profileImage)
                .setThumbnail(profileImage)
                .setDescription("Danke fürs Zuschauen! - Der Stream lief " + formatDuration(Duration.ofHours(Integer.parseInt(split[0])).plusMinutes(Integer.parseInt(split[1]))))
                .setImage(offlineImage)
                .setColor(Color.decode("#6441a4"))
                .build();

        twitchChannel.editMessageById(messageID, " ").setEmbeds(embed).queue();
        timer.cancel();
        timer = null;
    }

    public static void triggerLive(SlashCommandInteractionEvent event) {
        streamStart();
        event.reply("Live-Embed wurde gesendet!").setEphemeral(true).queue();
    }

    public static void triggerOff(SlashCommandInteractionEvent event) {
        streamEnd();
        event.reply("Offline-Embed wurde gesendet!").setEphemeral(true).queue();
    }
}