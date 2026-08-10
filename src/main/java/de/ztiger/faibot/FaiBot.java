package de.ztiger.faibot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.ErrorNotify;
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

    private static final Logger logger = LoggerFactory.getLogger(FaiBot.class);

    public static void main(String[] args) {
        try {
            Dotenv env = Dotenv.configure().load();
            ShardManager shardManager = buildShardManager(env);

            AppContainer app = new AppContainer(env, shardManager);

            registerErrorLogAppender(app.getChannelProvider());
            shardManager.addEventListener(app.getEventListeners());

            startHealthServer();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(app, shardManager)));

            logger.info("FaiBot started successfully!");
        } catch (Exception e) {
            logger.error("Fatal error during FaiBot initialization", e);
            System.exit(1);
        }
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

    private static void startHealthServer() {
        Spark.port(8090);
        Spark.get("/health", (req, res) -> "OK");
        logger.info("Health endpoint running at http://localhost:{}/health", 8090);
    }

    private static void shutdown(AppContainer app, ShardManager shardManager) {
        logger.info("Shutting down FaiBot...");
        app.shutdown();
        shardManager.shutdown();
        Spark.stop();
        logger.info("Shutdown complete.");
    }
}