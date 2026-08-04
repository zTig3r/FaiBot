package de.ztiger.faibot.listeners;

import de.ztiger.faibot.utils.GuildProvider;
import de.ztiger.faibot.utils.YoutubeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Localization.get;

public class BotReady extends ListenerAdapter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void onReady(ReadyEvent event) {
        GuildProvider.getMainGuild().ifPresentOrElse(
                guild -> {
                    CommandListener commandListener = new CommandListener();
                    guild.updateCommands().addCommands(commandListener.getCommandDataList()).queue();

                    ComponentListener componentListener = new ComponentListener();
                    event.getJDA().addEventListener(commandListener);
                    event.getJDA().addEventListener(componentListener);

                    checkUsersDB(guild);
                },
                () -> logger.error("Main guild could not be found!")
        );

        scheduler.scheduleAtFixedRate(this::updateServerStats, 10, 3600, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(YoutubeHandler::checkVideo, 10, 300, TimeUnit.SECONDS);
    }

    private void updateServerStats() {
        GuildProvider.getMainGuild().ifPresent(guild -> {
            String categoryName = get("serverstats.title");
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
        });
    }

    private static void checkUsersDB(Guild guild) {
        for (Member member : guild.getMembers()) {
            String id = member.getUser().getId();
            if (getter.getId(id) == -1) setter.addUser(id);
        }
    }
}