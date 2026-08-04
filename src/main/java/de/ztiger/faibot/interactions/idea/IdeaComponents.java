package de.ztiger.faibot.interactions.idea;

import de.ztiger.faibot.localization.keys.Idea;
import de.ztiger.faibot.utils.Localization;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;

import java.time.Duration;

public final class IdeaComponents {

    public static final String MODAL_ID = "idea";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_BODY = "body";

    public static Modal ideaModal() {
        TextInput subject = TextInput.create(FIELD_SUBJECT, TextInputStyle.SHORT)
                .setPlaceholder(Localization.get(Idea.Modal.Subject.PLACEHOLDER))
                .setMaxLength(100)
                .setRequired(true)
                .build();

        TextInput body = TextInput.create(FIELD_BODY, TextInputStyle.PARAGRAPH)
                .setPlaceholder(Localization.get(Idea.Modal.Body.PLACEHOLDER))
                .setMaxLength(1000)
                .setRequired(false)
                .build();

        return Modal.create(MODAL_ID, Localization.get(Idea.Modal.TITLE))
                .addComponents(
                        Label.of(Localization.get(Idea.Modal.Subject.TITLE), subject),
                        Label.of(Localization.get(Idea.Modal.Body.TITLE), body)
                )
                .build();
    }

    public static MessageCreateData createPollMessage(String authorMention, String subject, String body) {
        MessagePollData pollData = MessagePollData.builder(subject)
                .addAnswer(Localization.get(Idea.Poll.ACCEPT), Emoji.fromUnicode("✅"))
                .addAnswer(Localization.get(Idea.Poll.REJECT), Emoji.fromUnicode("❌"))
                .setDuration(Duration.ofDays(7))
                .build();

        String title = Localization.format(Idea.Poll.TITLE, "user", authorMention);
        String content = (body != null && !body.isBlank()) ? title + "\n\n" + body : title;

        return new MessageCreateBuilder()
                .setContent(content)
                .setPoll(pollData)
                .build();
    }
}