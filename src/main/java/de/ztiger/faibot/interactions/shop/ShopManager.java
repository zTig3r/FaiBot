package de.ztiger.faibot.interactions.shop;

import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.Colors.colors;
import static de.ztiger.faibot.utils.Lang.format;
import static de.ztiger.faibot.utils.Lang.getLang;

public class ShopManager {
    private static final Logger logger = LoggerFactory.getLogger(ShopManager.class);
    private static final String KEY = "shop.";

    public static String processColorPurchase(Member member, String color, int price) {
        String userId = member.getId();

        if (getter.getPoints(userId) < price) {
            return getLang(KEY + "error");
        }

        setter.removePoints(userId, price);
        setter.addInventory(userId, color);

        logger.info("User {} bought the color {}", member.getUser().getEffectiveName(), color);

        return format(KEY + "success", Map.of("color", colors.get(color).translation));
    }
}