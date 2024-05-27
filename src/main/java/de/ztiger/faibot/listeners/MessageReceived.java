package de.ztiger.faibot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static de.ztiger.faibot.FaiBot.*;
import static de.ztiger.faibot.utils.MessageCachingService.add;

@SuppressWarnings("ConstantConditions")
public class MessageReceived extends ListenerAdapter {

    HashMap<Member, Long> userTimer = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        Channel channel = event.getChannel();

        if (channel.equals(logChannel)) return;

        add(message);

        if (channel.equals(recommendationsChannel)) {
            if (!message.getContentRaw().contains("V:")) return;
            message.addReaction(Emoji.fromUnicode("✅")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();
        }

        if (channel.equals(botChannel)) return;

        String id = event.getMember().getId();

        if (canGetXp(event.getMember())) {
            setter.addXP(id, ThreadLocalRandom.current().nextInt(15, 25));
            setter.addPoints(id, ThreadLocalRandom.current().nextInt(0, 3));
            checkLevelUp(event.getMember());
            userTimer.put(event.getMember(), System.currentTimeMillis());
        }

        setter.addMessage(id);
    }

    private void checkLevelUp(Member member) {
        int level = getter.getLevel(member.getId());
        int xpForNextLevel = calcXP(level);
        int xp = getter.getXP(member.getId()) - getLastLevelsXP(level - 1);

        if (xp >= xpForNextLevel) {
            setter.addLevel(member.getId());
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor("Level UP!", null, member.getUser().getAvatarUrl())
                    .setDescription(member.getAsMention() + " hat Level " + (level + 1) + " erreicht!")
                    .setColor(Color.GREEN)
                    .build();

            logger.info(member.getEffectiveName() + " reached level " + (level + 1) + "!");
            botChannel.sendMessageEmbeds(embed).queue();
        }
    }

    private boolean canGetXp(Member member) {
        if (userTimer.containsKey(member)) {
            if ((((userTimer.get(member) / 1000) + 60) - (System.currentTimeMillis() / 1000)) <= 0) {
                userTimer.remove(member);
                return true;
            } else return false;
        }
        return true;
    }

    public static int getLastLevelsXP(int level) {
        return IntStream.rangeClosed(0, level).map(MessageReceived::calcXP).sum();
    }

    public static int calcXP(Member member) {
        int level = getter.getLevel(member.getId());

        return 5 * (level * level) + (50 * level) + 100 - getter.getXP(member.getId());
    }

    public static int calcXP(int level) {
        return 5 * (level * level) + (50 * level) + 100;
    }
}
