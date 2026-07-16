package de.ztiger.faibot.listeners;

import de.ztiger.faibot.FaiBot;
import de.ztiger.faibot.utils.YoutubeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Lang.getLang;

public class BotReady extends ListenerAdapter {

    public static Guild GUILD;

    @Override
    public void onReady(ReadyEvent event) {
        GUILD = event.getJDA().getGuildById(config.get("GUILD"));
        logChannel = event.getJDA().getTextChannelById(config.get("LOG"));
        recommendationsChannel = event.getJDA().getTextChannelById(config.get("RECOMMENDATIONS"));
        welcomeChannel = event.getJDA().getTextChannelById(config.get("WELCOME"));
        botChannel = event.getJDA().getTextChannelById(config.get("BOT"));
        twitchChannel = event.getJDA().getNewsChannelById(config.get("TWITCH"));
        youtubeChannel = event.getJDA().getNewsChannelById(config.get("YOUTUBE"));
        reactionChannel = event.getJDA().getTextChannelById(config.get("REACTION"));

        CommandListener commandListener = new CommandListener();
        GUILD.updateCommands().addCommands(commandListener.getCommandDataList()).queue();

        ComponentListener componentListener = new ComponentListener();
        event.getJDA().addEventListener(commandListener);
        event.getJDA().addEventListener(componentListener);

        String name = getLang("serverstats.title");
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                Guild guild = FaiBot.getShardManager().getGuildById(config.get("GUILD"));

                Category category = guild.getCategoriesByName(name, true).get(0);

                if (category.getVoiceChannels().get(0).getName().contains(String.valueOf(guild.getMemberCount())))
                    return;

                category.getVoiceChannels().get(0).getManager().setName("All Members: " + guild.getMemberCount()).queue();
                category.getVoiceChannels().get(1).getManager().setName("Members: " + (guild.getMembers().size() - 1)).queue();
                category.getVoiceChannels().get(2).getManager().setName("Bots: " + guild.getMembersWithRoles(guild.getRolesByName("Bots", true).get(0)).size()).queue();

                logger.info("Updated Server Stats");
            }
        };

        timer.schedule(hourlyTask, 100000, 1000 * 60 * 60);

        Timer timer1 = new Timer();
        TimerTask twoMinTask = new TimerTask() {
            @Override
            public void run() {
                YoutubeHandler.checkVideo();
            }
        };

        timer1.schedule(twoMinTask, 100000, 500 * 60 * 5);

        checkUsersDB();
    }

    private static void checkUsersDB() {
        for (Member member : GUILD.getMembers()) {
            String id = member.getUser().getId();
            if (getter.getId(id) == -1) setter.addUser(id);
        }
    }
}
