package de.ztiger.faibot;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.data.*;
import de.ztiger.faibot.interactions.halloffame.HallOfFameCmd;
import de.ztiger.faibot.interactions.halloffame.HallOfFameComponents;
import de.ztiger.faibot.interactions.halloffame.HallOfFameService;
import de.ztiger.faibot.interactions.idea.IdeaCmd;
import de.ztiger.faibot.interactions.idea.IdeaComponents;
import de.ztiger.faibot.interactions.nixos.NixosCmd;
import de.ztiger.faibot.interactions.nixos.NixosComponents;
import de.ztiger.faibot.interactions.points.PointsCmd;
import de.ztiger.faibot.interactions.points.PointsComponents;
import de.ztiger.faibot.interactions.serverstats.ServerStatsCmd;
import de.ztiger.faibot.interactions.twitch.*;
import de.ztiger.faibot.interactions.youtube.YoutubeCmd;
import de.ztiger.faibot.interactions.youtube.YoutubeHandler;
import de.ztiger.faibot.listeners.*;
import de.ztiger.faibot.services.*;
import de.ztiger.faibot.utils.*;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class AppContainer {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final Dotenv env;
    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;
    private final ShardManager shardManager;

    private final ChannelProvider channelProvider;
    private final GuildProvider guildProvider;
    private final RoleProvider roleProvider;
    private final UserProvider userProvider;
    private final LocalizationService i18n;

    private final ExternalReferenceService externalReferenceService;
    private final TwitchApiService twitchApiService;
    private final TwitchUserService twitchUserService;
    private final SeasonService seasonService;
    private final PlacementService placementService;
    private final TwitchStreamHandler twitchStreamHandler;
    private final YoutubeHandler youtubeHandler;
    private final MessageCachingService messageCachingService;
    private final HallOfFameService hallOfFameService;

    private final HallOfFameComponents hallOfFameComponents;
    private final IdeaComponents ideaComponents;
    private final NixosComponents nixosComponents;
    private final PointsComponents pointsComponents;
    private final TwitchComponents twitchComponents;

    private final InteractionListener interactionListener;

    public AppContainer(Dotenv env) throws Exception {
        this.env = env;

        // Core Utilities
        this.configManager = new ConfigManager(env);
        this.channelProvider = new ChannelProvider(configManager);
        this.guildProvider = new GuildProvider(configManager);
        this.roleProvider = new RoleProvider(configManager);
        this.userProvider = new UserProvider();
        this.i18n = new LocalizationService(configManager);

        // Database & DAOs
        this.databaseManager = new DatabaseManager(env);
        Dao<TwitchUser, String> userDao = DaoManager.createDao(databaseManager.getConnectionSource(), TwitchUser.class);
        Dao<Season, String> seasonDao = DaoManager.createDao(databaseManager.getConnectionSource(), Season.class);
        Dao<Placement, Integer> placementDao = DaoManager.createDao(databaseManager.getConnectionSource(), Placement.class);
        Dao<ExternalReference, Integer> externalReferenceDao = DaoManager.createDao(databaseManager.getConnectionSource(), ExternalReference.class);

        // Domain Services & Component Factories
        this.externalReferenceService = new ExternalReferenceService(externalReferenceDao);
        this.twitchApiService = new TwitchApiService(env.get("CLIENT_ID"), env.get("CLIENT_SECRET"));
        this.twitchUserService = new TwitchUserService(userDao);
        this.seasonService = new SeasonService(seasonDao);
        this.placementService = new PlacementService(twitchUserService, seasonService, placementDao, twitchApiService);
        this.messageCachingService = new MessageCachingService();

        twitchUserService.initCache();

        // UI
        this.hallOfFameComponents = new HallOfFameComponents(configManager, i18n);
        this.ideaComponents = new IdeaComponents(i18n);
        this.nixosComponents = new NixosComponents(configManager, i18n);
        this.pointsComponents = new PointsComponents(i18n);
        this.twitchComponents = new TwitchComponents(configManager, i18n);

        this.hallOfFameService = new HallOfFameService(externalReferenceService, placementService, channelProvider, hallOfFameComponents);

        this.youtubeHandler = new YoutubeHandler(channelProvider, externalReferenceService, roleProvider, env.get("YOUTUBE_KEY"), i18n);

        // Stream Handler
        String twitchChannel = configManager.getConfig().getString("twitch-channel");
        this.twitchStreamHandler = new TwitchStreamHandler(twitchApiService, twitchChannel, channelProvider, roleProvider, twitchComponents, i18n);

        // Commands & Listeners
        HallOfFameCmd hallOfFameCmd = new HallOfFameCmd(channelProvider, hallOfFameComponents, externalReferenceService, hallOfFameService, i18n);
        IdeaCmd ideaCmd = new IdeaCmd(channelProvider, ideaComponents, i18n);
        NixosCmd nixosCmd = new NixosCmd(placementService, seasonService, channelProvider, roleProvider, hallOfFameService, nixosComponents, i18n);
        PointsCmd pointsCmd = new PointsCmd(placementService, twitchUserService, pointsComponents, i18n);
        ServerStatsCmd serverStatsCmd = new ServerStatsCmd(guildProvider, i18n);
        TwitchCmd twitchCmd = new TwitchCmd(twitchStreamHandler, i18n);
        YoutubeCmd youtubeCmd = new YoutubeCmd(youtubeHandler, i18n);

        this.interactionListener = new InteractionListener(hallOfFameCmd, ideaCmd, nixosCmd, pointsCmd, serverStatsCmd, twitchCmd, youtubeCmd);

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(env.get("TOKEN"))
                .setAutoReconnect(true)
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EXPRESSIONS, GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOJI);

        builder.addEventListeners(
                interactionListener,
                new MessageReceived(channelProvider, messageCachingService),
                new MemberLeave(channelProvider, i18n),
                new MessageDelete(channelProvider, messageCachingService, userProvider, i18n),
                new MessageEdit(channelProvider, messageCachingService, i18n),
                new MemberJoin(channelProvider, i18n),
                new BotReady(interactionListener, guildProvider)
        );

        this.shardManager = builder.build();

        this.channelProvider.setShardManager(this.shardManager);
        this.guildProvider.setShardManager(this.shardManager);
        this.roleProvider.setShardManager(this.shardManager);
        this.userProvider.setShardManager(this.shardManager);

        scheduler.scheduleAtFixedRate(serverStatsCmd.updateServerStats(), 10, 3600, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(youtubeHandler.checkVideo(), 10, 300, TimeUnit.SECONDS);
    }

    public void shutdown() {
        twitchStreamHandler.shutdown();
        twitchApiService.shutdown();
        if (shardManager != null) {
            shardManager.shutdown();
        }
        databaseManager.close();
    }
}