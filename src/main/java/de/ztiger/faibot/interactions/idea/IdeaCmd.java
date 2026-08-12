package de.ztiger.faibot.interactions.idea;

import de.ztiger.faibot.config.BotChannel;
import de.ztiger.faibot.interactions.ICommand;
import de.ztiger.faibot.interactions.components.IModalHandler;
import de.ztiger.faibot.localization.keys.Idea;
import de.ztiger.faibot.utils.ChannelProvider;
import de.ztiger.faibot.services.LocalizationService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@RequiredArgsConstructor
public class IdeaCmd implements ICommand, IModalHandler {

    private final ChannelProvider channelProvider;
    private final IdeaComponents ideaComponents;
    private final LocalizationService i18n;

    @Override
    public String getComponentId() {
        return IdeaComponents.COMPONENT_ID;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("idea", i18n.get(Idea.Command.DESCRIPTION));
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        event.replyModal(ideaComponents.getIdeaModal()).queue();
    }

    @Override
    public void modalInteraction(ModalInteractionEvent event) {
        String subject = event.getValue(IdeaComponents.SUBJECT_FIELD).getAsString();
        String body = event.getValue(IdeaComponents.BODY_FIELD).getAsString();

        MessageCreateData pollMessage = ideaComponents.getPollMessage(
                event.getUser().getAsMention(),
                subject, body
        );

        channelProvider.sendMessage(BotChannel.RECOMMENDATIONS, pollMessage);

        event.reply(i18n.get(Idea.Modal.SUCCESS)).setEphemeral(true).queue();
    }
}