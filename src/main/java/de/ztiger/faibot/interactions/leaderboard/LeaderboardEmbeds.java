package de.ztiger.faibot.interactions.leaderboard;

import de.ztiger.faibot.localization.keys.Leaderboard;
import de.ztiger.faibot.utils.BotEmbed;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class LeaderboardEmbeds {

    public static MessageEmbed leaderboard(List<String> leaderboard, int page, int totalPages) {
        BotEmbed embed = BotEmbed.defaultEmbed()
                .title(Localization.get(Leaderboard.Embed.TITLE))
                .footer(Localization.format(Leaderboard.Embed.FOOTER, "page", page, "totalPages", totalPages));

        leaderboard.forEach(embed::normalField);

        return embed.build();
    }
}
