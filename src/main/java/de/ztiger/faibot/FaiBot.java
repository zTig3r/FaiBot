package de.ztiger.faibot;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.utils.ErrorNotify;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

@Slf4j
public class FaiBot {

    public static void main(String[] args) {
        try {
            Dotenv env = Dotenv.configure().load();

            AppContainer app = new AppContainer(env);

            registerErrorLogAppender(app.getChannelProvider());

            startHealthServer();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(app)));

            log.info("FaiBot started successfully!");
        } catch (Exception e) {
            log.error("Fatal error during FaiBot initialization", e);
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

        log.info("Registered Discord Error Logger Appender.");
    }

    private static void startHealthServer() {
        Spark.port(8090);
        Spark.get("/health", (req, res) -> "OK");
        log.info("Health endpoint running at http://localhost:{}/health", 8090);
    }

    private static void shutdown(AppContainer app) {
        log.info("Shutting down FaiBot...");
        app.shutdown();
        Spark.stop();
        log.info("Shutdown complete.");
    }
}