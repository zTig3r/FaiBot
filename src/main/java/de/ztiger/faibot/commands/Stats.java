package de.ztiger.faibot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Timer;

import static de.ztiger.faibot.FaiBot.getter;
import static de.ztiger.faibot.FaiBot.logger;
import static de.ztiger.faibot.listeners.MessageReceived.calcXP;
import static de.ztiger.faibot.listeners.MessageReceived.getLastLevelsXP;
import static de.ztiger.faibot.utils.Colors.colors;

@SuppressWarnings({"ConstantConditions"})
public class Stats {

    private final static int minX = -900;
    private final static int maxX = 0;

    public static void sendStats(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user") != null ? event.getOption("user").getAsMember() : event.getMember();

        event.replyFiles(FileUpload.fromData(createStatsImage(member, convertColor(getter.getCardColor(member.getId()))))).queue();
    }

    public static void sendPreview(ButtonInteractionEvent event, String color) {
        Button apply = Button.primary("apply", "✅ Anwenden");
        Button cancel = Button.secondary("cancel", "❌ Abbrechen");

        event.editMessage("").setAttachments(FileUpload.fromData(createStatsImage(event.getMember(), convertColor(color)))).setActionRow(apply, cancel).setEmbeds().queue();
    }

    private static File createStatsImage(Member member, Color color) {
        String userCardPath = "data/" + member.getId() + ".png";

        try {
            BufferedImage userBar = colorImage(ImageIO.read(Stats.class.getResourceAsStream("/xpbar.png")), color);
            BufferedImage overlay = ImageIO.read(Stats.class.getResourceAsStream("/overlay.png"));

            int level = getter.getLevel(member.getId());
            int xpForNextLevel = calcXP(level);
            int xp = getter.getXP(member.getId()) - getLastLevelsXP(level - 1);
            int rank = getter.getRank(member.getId());
            int barX = (int) (minX + (maxX - minX) * ((double) xp / xpForNextLevel));

            BufferedImage background = new BufferedImage(overlay.getWidth(), overlay.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D bgFill = background.createGraphics();
            bgFill.setComposite(AlphaComposite.Src);
            bgFill.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            bgFill.setColor(Color.decode("#8e8e8e"));
            bgFill.fill(new RoundRectangle2D.Float(0, 0, overlay.getWidth(), overlay.getHeight(), 25, 25));
            bgFill.dispose();
            ImageIO.write(background, "png", new File(userCardPath));

            BufferedImage userCard = ImageIO.read(new File(userCardPath));

            Graphics contents = userCard.getGraphics();
            contents.drawImage(userBar, barX, 0, userBar.getWidth(), userBar.getHeight(), null);
            contents.drawImage(ImageIO.read(new URL(member.getUser().getAvatarUrl())), 15, 15, 283, 283, null);
            contents.drawImage(overlay, 0, 0, null);

            Font small = new Font("Tw Cen MT", Font.PLAIN, 40);
            Font normal = new Font("Tw Cen MT", Font.PLAIN, 50);
            Font smallC = new Font("Century Gothic", Font.PLAIN, 40);
            Font normalC = new Font("Century Gothic", Font.BOLD, 50);
            Font bigC = new Font("Century Gothic", Font.BOLD, 60);

            AttributedString rankString = new AttributedString("PLATZ " + rank);
            rankString.addAttribute(TextAttribute.FONT, small, 0, 5);
            rankString.addAttribute(TextAttribute.FONT, bigC, 6, String.valueOf(rank).length() + 6);
            rankString.addAttribute(TextAttribute.FOREGROUND, Color.WHITE, 0, String.valueOf(rank).length() + 6);

            AttributedString levelString = new AttributedString("LEVEL " + level);
            levelString.addAttribute(TextAttribute.FONT, small, 0, 5);
            levelString.addAttribute(TextAttribute.FONT, bigC, 6, String.valueOf(level).length() + 6);
            levelString.addAttribute(TextAttribute.FOREGROUND, (color == null ? Color.decode("#94c6f3") : color), 0, String.valueOf(level).length() + 6);

            AttributedCharacterIterator cI = levelString.getIterator();
            FontRenderContext frc = contents.getFontMetrics().getFontRenderContext();
            LineBreakMeasurer measurer = new LineBreakMeasurer(cI, frc);
            TextLayout textLayout = measurer.nextLayout(overlay.getWidth());

            AttributedString nameString = new AttributedString(member.getEffectiveName());
            nameString.addAttribute(TextAttribute.FONT, normalC, 0, member.getEffectiveName().length());
            nameString.addAttribute(TextAttribute.FOREGROUND, Color.WHITE, 0, member.getEffectiveName().length());

            AttributedString xpString = new AttributedString(xp + " / " + xpForNextLevel + " XP");
            xpString.addAttribute(TextAttribute.FONT, smallC, 0, String.valueOf(xp).length() + String.valueOf(xpForNextLevel).length() + 6);
            xpString.addAttribute(TextAttribute.FOREGROUND, Color.decode("#8e8e8e"));
            xpString.addAttribute(TextAttribute.FOREGROUND, Color.WHITE, 0, String.valueOf(xp).length());

            AttributedCharacterIterator cI2 = xpString.getIterator();
            LineBreakMeasurer measurer2 = new LineBreakMeasurer(cI2, frc);
            TextLayout textLayout2 = measurer2.nextLayout(overlay.getWidth());

            AttributedString pointsString = new AttributedString("Punkte:  " + getter.getPoints(member.getId()));
            pointsString.addAttribute(TextAttribute.FONT, normal, 0, 7);
            pointsString.addAttribute(TextAttribute.FONT, normalC, 8, 9 + String.valueOf(getter.getPoints(member.getId())).length());
            pointsString.addAttribute(TextAttribute.FOREGROUND, Color.WHITE, 0, 7 + String.valueOf(getter.getPoints(member.getId())).length());

            AttributedCharacterIterator cI3 = pointsString.getIterator();
            LineBreakMeasurer measurer3 = new LineBreakMeasurer(cI3, frc);
            TextLayout textLayout3 = measurer3.nextLayout(overlay.getWidth());

            AttributedString messagesString = new AttributedString("Nachrichten:  " + getter.getMessages(member.getId()));
            messagesString.addAttribute(TextAttribute.FONT, normal, 0, 12);
            messagesString.addAttribute(TextAttribute.FONT, normalC, 13, 14 + String.valueOf(getter.getMessages(member.getId())).length());
            messagesString.addAttribute(TextAttribute.FOREGROUND, Color.WHITE, 0, 12 + String.valueOf(getter.getMessages(member.getId())).length());

            contents.drawString(rankString.getIterator(), 310, 65);

            contents.drawString(levelString.getIterator(), (int) (overlay.getWidth() - textLayout.getAdvance() - 40), 65);

            contents.drawString(nameString.getIterator(), 310, 180);

            contents.drawString(xpString.getIterator(), (int) (overlay.getWidth() - textLayout2.getAdvance() - 40), 160);

            contents.drawString(pointsString.getIterator(), (int) (overlay.getWidth() - textLayout3.getAdvance() - 40), 264);

            contents.drawString(messagesString.getIterator(), 310, 264);

            contents.dispose();

            ImageIO.write(userCard, "png", new File(userCardPath));

            Timer timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (new File(userCardPath).delete())
                        logger.info("Deleted temporary files of " + member.getEffectiveName());
                    else logger.warn("Couldn't delete temporary files");
                }
            }, 10000);


        } catch (IOException e) {
            logger.error("Error while creating stats image: " + e.getMessage());
        }

        return new File(userCardPath);
    }

    private static BufferedImage colorImage(BufferedImage image, Color color) {
        if (color == null) color = Color.decode("#94c6f3");

        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = r;
                pixels[1] = g;
                pixels[2] = b;
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }

    private static Color convertColor(String color) {
        return Color.decode((color == null ? "#94c6f3" : colors.get(color).hex));
    }
}
