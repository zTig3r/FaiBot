package de.ztiger.faibot.listeners;

import de.ztiger.faibot.utils.GuildProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class BotReady extends ListenerAdapter {

    private final InteractionListener interactionListener;
    private final GuildProvider guildProvider;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void onReady(ReadyEvent event) {
        guildProvider.getMainGuild().ifPresentOrElse(
                guild -> guild.updateCommands().addCommands(interactionListener.getCommandDataList()).queue(),
                () -> log.error("Main guild could not be found!")
        );

        scheduler.scheduleAtFixedRate(this::updateServerStats, 10, 3600, TimeUnit.SECONDS);

    //    scheduler.scheduleAtFixedRate(YoutubeHandler::checkVideo, 10, 300, TimeUnit.SECONDS);
    }

    private void updateServerStats() {
       /* guildProvider.getMainGuild().ifPresent(guild -> {
            String categoryName = i18n.get("serverstats.title");
            List<Category> categories = guild.getCategoriesByName(categoryName, true);

            if (categories.isEmpty()) return;

            Category category = categories.getFirst();
            var voiceChannels = category.getVoiceChannels();
            if (voiceChannels.size() < 3) return;

            int memberCount = guild.getMemberCount();
            if (voiceChannels.getFirst().getName().contains(String.valueOf(memberCount))) return;

            int humanMembers = guild.getMembers().size() - 1;
            int botMembers = guild.getMembersWithRoles(guild.getRolesByName("Bots", true).getFirst()).size();

            voiceChannels.get(0).getManager().setName("All Members: " + memberCount).queue();
            voiceChannels.get(1).getManager().setName("Members: " + humanMembers).queue();
            voiceChannels.get(2).getManager().setName("Bots: " + botMembers).queue();

            logger.info("Updated Server Stats");
        });*/
    }
}