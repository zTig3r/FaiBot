package de.ztiger.faibot;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.data.*;
import de.ztiger.faibot.interactions.idea.IdeaCmd;
import de.ztiger.faibot.interactions.idea.IdeaComponents;
import de.ztiger.faibot.interactions.nixos.NixosCmd;
import de.ztiger.faibot.interactions.nixos.NixosComponents;
import de.ztiger.faibot.interactions.serverstats.ServerStatsCmd;
import de.ztiger.faibot.interactions.twitch.*;
import de.ztiger.faibot.interactions.youtube.YoutubeCmd;
import de.ztiger.faibot.listeners.*;
import de.ztiger.faibot.services.*;
import de.ztiger.faibot.utils.*;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.dv8tion.jda.api.sharding.ShardManager;

@Getter
public class AppContainer {

    private final Dotenv env;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final ShardManager shardManager;

    private final ChannelProvider channelProvider;
    private final GuildProvider guildProvider;
    private final LocalizationService i18n;

    private final TwitchApiService twitchApiService;
    private final TwitchUserService twitchUserService;
    private final SeasonService seasonService;
    private final PlacementService rankingService;
    private final TwitchStreamHandler twitchStreamHandler;

    private final IdeaComponents ideaComponents;
    private final NixosComponents nixosComponents;
    private final TwitchComponents twitchComponents;

    private final InteractionListener interactionListener;

    public AppContainer(Dotenv env, ShardManager shardManager) throws Exception {
        this.env = env;
        this.shardManager = shardManager;

        // Core Utilities
        this.configManager = new ConfigManager(env);
        this.channelProvider = new ChannelProvider(shardManager, configManager);
        this.guildProvider = new GuildProvider(shardManager, configManager);
        this.i18n = new LocalizationService(configManager);

        // Database & DAOs
        this.databaseManager = new DatabaseManager(env);
        Dao<TwitchUser, String> userDao = DaoManager.createDao(databaseManager.getConnectionSource(), TwitchUser.class);
        Dao<Season, String> seasonDao = DaoManager.createDao(databaseManager.getConnectionSource(), Season.class);
        Dao<Placement, Integer> placementDao = DaoManager.createDao(databaseManager.getConnectionSource(), Placement.class);

        // Domain Services & Component Factories
        this.twitchApiService = new TwitchApiService(env.get("CLIENT_ID"), env.get("CLIENT_SECRET"));
        this.twitchUserService = new TwitchUserService(userDao);
        this.seasonService = new SeasonService(seasonDao);
        this.rankingService = new PlacementService(twitchUserService, seasonService, placementDao, twitchApiService);

        // UI & Stream Handlers
        this.ideaComponents = new IdeaComponents(i18n);
        this.nixosComponents = new NixosComponents(configManager, i18n);
        this.twitchComponents = new TwitchComponents(configManager, i18n);

        String twitchChannel = configManager.getConfig().getString("twitch-channel");
        this.twitchStreamHandler = new TwitchStreamHandler(twitchApiService, twitchChannel, channelProvider, twitchComponents, i18n);

        // Commands & Listeners
        NixosCmd nixosCmd = new NixosCmd(rankingService, seasonService, channelProvider, nixosComponents, i18n);
        IdeaCmd ideaCmd = new IdeaCmd(channelProvider, ideaComponents, i18n);
        ServerStatsCmd serverStatsCmd = new ServerStatsCmd(guildProvider, i18n);
        TwitchCmd twitchCmd = new TwitchCmd(twitchStreamHandler, i18n);
        YoutubeCmd youtubeCmd = new YoutubeCmd();

        this.interactionListener = new InteractionListener(nixosCmd, ideaCmd, serverStatsCmd, twitchCmd, youtubeCmd);
    }

    public Object[] getEventListeners() {
        return new Object[]{
                interactionListener,
                new MessageReceived(channelProvider),
                new MemberLeave(channelProvider, i18n),
                new MessageDelete(channelProvider, i18n),
                new MessageEdit(channelProvider, i18n),
                new MemberJoin(channelProvider, i18n),
                new BotReady(interactionListener, guildProvider)
        };
    }

    public void shutdown() {
        twitchStreamHandler.shutdown();
        twitchApiService.shutdown();
        databaseManager.close();
    }
}