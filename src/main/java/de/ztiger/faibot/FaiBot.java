package de.ztiger.faibot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import de.ztiger.faibot.config.ConfigManager;
import de.ztiger.faibot.data.*;
import de.ztiger.faibot.interactions.idea.IdeaCmd;
import de.ztiger.faibot.interactions.nixos.NixosCmd;
import de.ztiger.faibot.interactions.serverstats.ServerStatsCmd;
import de.ztiger.faibot.interactions.twitch.*;
import de.ztiger.faibot.interactions.youtube.YoutubeCmd;
import de.ztiger.faibot.listeners.*;
import de.ztiger.faibot.services.*;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.DatabaseManager;
import de.ztiger.faibot.utils.ErrorNotify;
import de.ztiger.faibot.utils.GuildProvider;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

public class FaiBot {

    // TODO: Have a logger for each class separately
    // TODO: Implement custom colors

    private static final Logger logger = LoggerFactory.getLogger(FaiBot.class);

    public static void main(String[] args) {
        try {
            Dotenv env = Dotenv.configure().load();
            ConfigManager cfgm = new ConfigManager(env);

            ShardManager shardManager = buildShardManager(env);

            ChannelProvider channelProvider = new ChannelProvider(shardManager, cfgm);
            GuildProvider guildProvider = new GuildProvider(shardManager, cfgm);
            LocalizationService i18n = new LocalizationService(cfgm);

            registerErrorLogAppender(channelProvider);

            Services services = initServices(env, cfgm, channelProvider, i18n);

            InteractionListener interactionListener = initInteractionListener(services, channelProvider, guildProvider, i18n);
            registerEventListeners(shardManager, interactionListener, channelProvider, guildProvider, i18n);

            startHealthServer();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(services, shardManager)));

            logger.info("FaiBot started successfully!");
        } catch (Exception e) {
            logger.error("Fatal error during FaiBot initialization", e);
            System.exit(1);
        }
    }

    private static void registerErrorLogAppender(ChannelProvider channelProvider) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        ErrorNotify errorNotifyAppender = new ErrorNotify(channelProvider);
        errorNotifyAppender.setContext(loggerContext);

        ThresholdFilter filter = new ThresholdFilter();
        filter.setContext(loggerContext);
        filter.setLevel("ERROR");
        filter.start();
        errorNotifyAppender.addFilter(filter);

        errorNotifyAppender.start();

        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(errorNotifyAppender);

        logger.info("Registered Discord Error Logger Appender.");
    }

    private static Services initServices(Dotenv env, ConfigManager cfgm, ChannelProvider channelProvider, LocalizationService i18n) throws Exception {
        logger.info("Initializing Database & Services...");
        DatabaseManager dbManager = new DatabaseManager(env);

        Dao<TwitchUser, String> userDao = DaoManager.createDao(dbManager.getConnectionSource(), TwitchUser.class);
        Dao<Season, String> seasonDao = DaoManager.createDao(dbManager.getConnectionSource(), Season.class);
        Dao<Placement, Integer> placementDao = DaoManager.createDao(dbManager.getConnectionSource(), Placement.class);

        TwitchApiService twitchApiService = new TwitchApiService(
                env.get("CLIENT_ID"),
                env.get("CLIENT_SECRET")
        );

        TwitchUserService twitchUserService = new TwitchUserService(userDao);
        SeasonService seasonService = new SeasonService(seasonDao);
        PlacementService rankingService = new PlacementService(twitchUserService, seasonService, placementDao, twitchApiService);

        String twitchChannel = cfgm.getConfig().getString("twitch-channel");
        TwitchStreamHandler twitchHandler = new TwitchStreamHandler(twitchApiService, twitchChannel, channelProvider, i18n);

        return new Services(cfgm, dbManager, twitchApiService, twitchUserService, seasonService, rankingService, twitchHandler);
    }

    private static ShardManager buildShardManager(Dotenv env) {
        logger.info("Connecting to Discord...");
        return DefaultShardManagerBuilder.createDefault(env.get("TOKEN"))
                .setAutoReconnect(true)
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EXPRESSIONS, GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOJI)
                .build();
    }

    private static InteractionListener initInteractionListener(Services services, ChannelProvider channelProvider, GuildProvider guildProvider, LocalizationService i18n) {
        NixosCmd nixosCmd = new NixosCmd(services.rankingService(), services.seasonService(), channelProvider, i18n);
        IdeaCmd ideaCmd = new IdeaCmd(channelProvider, i18n);
        ServerStatsCmd serverStatsCmd = new ServerStatsCmd(guildProvider, i18n);
        TwitchCmd twitchCmd = new TwitchCmd(services.twitchStreamHandler(), i18n);
        YoutubeCmd youtubeCmd = new YoutubeCmd();

        return new InteractionListener(nixosCmd, ideaCmd, serverStatsCmd, twitchCmd, youtubeCmd);
    }

    private static void registerEventListeners(ShardManager shardManager, InteractionListener interactionListener, ChannelProvider channelProvider, GuildProvider guildProvider, LocalizationService i18n) {
        logger.info("Registering Event Listeners...");

        shardManager.addEventListener(
                interactionListener,
                new MessageReceived(channelProvider),
                new MemberLeave(channelProvider, i18n),
                new MessageDelete(channelProvider, i18n),
                new MessageEdit(channelProvider, i18n),
                new MemberJoin(channelProvider, i18n),
                new BotReady(interactionListener, guildProvider)
        );
    }

    private static void startHealthServer() {
        Spark.port(8090);
        Spark.get("/health", (req, res) -> "OK");
        logger.info("Health endpoint running at http://localhost:{}/health", 8090);
    }

    private static void shutdown(Services services, ShardManager shardManager) {
        logger.info("Shutting down FaiBot...");
        services.twitchStreamHandler().shutdown();
        services.twitchApiService().shutdown();
        services.databaseManager().close();
        shardManager.shutdown();
        Spark.stop();
        logger.info("Shutdown complete.");
    }

    private record Services(ConfigManager configManager, DatabaseManager databaseManager,
                            TwitchApiService twitchApiService,
                            TwitchUserService twitchUserService, SeasonService seasonService,
                            PlacementService rankingService, TwitchStreamHandler twitchStreamHandler) {
    }
}