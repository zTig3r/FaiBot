package de.ztiger.faibot.interactions.components;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;
import java.util.Optional;

public interface IComponentHandler {
    String getComponentId();

    default String getRequiredValue(ModalInteractionEvent event, String fieldId) {
        return Optional.ofNullable(event.getValue(fieldId))
                .map(ModalMapping::getAsString)
                .orElseThrow(() -> new IllegalArgumentException("Missing required modal field: " + fieldId));
    }

    default String getValueOrDefault(ModalInteractionEvent event, String fieldId, String defaultValue) {
        return Optional.ofNullable(event.getValue(fieldId))
                .map(ModalMapping::getAsString)
                .orElse(defaultValue);
    }

    default String getRequiredStringOption(ModalInteractionEvent event, String fieldId) {
        return Optional.ofNullable(event.getValue(fieldId))
                .map(m -> m.getAsStringList().getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Missing required modal field: " + fieldId));
    }

    default List<Message.Attachment> getAttachments(ModalInteractionEvent event, String fieldId) {
        return Optional.ofNullable(event.getValue(fieldId))
                .map(ModalMapping::getAsAttachmentList)
                .orElse(List.of());
    }
}
