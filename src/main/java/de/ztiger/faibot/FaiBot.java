package de.ztiger.faibot;

import de.ztiger.faibot.commands.CommandManager;
import de.ztiger.faibot.listeners.*;
import de.ztiger.faibot.utils.MariaDB;
import de.ztiger.faibot.utils.SQLGetter;
import de.ztiger.faibot.utils.SQLSetter;
import de.ztiger.faibot.utils.TwitchHandler;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.SQLException;

import static de.ztiger.faibot.utils.TableCreator.createTables;

@SuppressWarnings({"InstantiationOfUtilityClass"})
public class FaiBot {

    public static final Logger logger = LoggerFactory.getLogger(FaiBot.class);
    public static Dotenv config;
    private static ShardManager shardManager;
    public static TextChannel logChannel, recommendationsChannel, welcomeChannel, botChannel;
    public static NewsChannel twitchChannel, youtubeChannel;
    public static MariaDB mariaDB;
    public static SQLGetter getter;
    public static SQLSetter setter;

    public static void main(String[] args) {
        try {
            new FaiBot();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        mariaDB = new MariaDB();
        getter = new SQLGetter();
        setter = new SQLSetter();

        try {
            mariaDB.connect();

            if (mariaDB.isConnected()) {
                logger.info("Connected to MariaDB database!");
                createTables();
            } else logger.error("Could not connect to MariaDB database!");

        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage());
        }

        File data = new File("data");
        if (!data.exists()) {
            if (!data.mkdir()) logger.error("Could not create data folder!");
            else logger.info("Created data folder!");
        }

        new TwitchHandler();

    }


    private FaiBot() throws LoginException {
        config = Dotenv.configure().load();
        shardManager = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"))
                .setAutoReconnect(true)
                .addEventListeners(new CommandManager(), new MessageReceived(), new MemberLeave(), new MessageDelete(), new MessageUpdate(), new MemberJoin(), new BotReady())
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS, GatewayIntent.MESSAGE_CONTENT)
                .setBulkDeleteSplittingEnabled(false)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS, CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOJI)
                .build();
    }


    public static ShardManager getShardManager() {
        return shardManager;
    }

}
