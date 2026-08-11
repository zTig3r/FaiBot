package de.ztiger.faibot.services;

import com.j256.ormlite.dao.Dao;
import de.ztiger.faibot.data.ExternalReference;
import de.ztiger.faibot.data.ResourceType;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class ExternalReferenceService {

    private final Dao<ExternalReference, Integer> externalReferenceDao;

    public void setHallOfFameMessage(String messageId) throws SQLException {
        createOrUpdateExternalReference(ResourceType.HALL_OF_FAME, messageId);
    }

    public long getHallOfFameMessageId() throws SQLException {
        ExternalReference reference = externalReferenceDao.queryBuilder().where().eq("resource_type", ResourceType.HALL_OF_FAME.name()).queryForFirst();
        if (reference == null) return -1;
        return Long.parseLong(reference.getIdentifier());
    }

    public void createOrUpdateExternalReference(ResourceType type, String identifier) throws SQLException {
        ExternalReference reference = externalReferenceDao.queryBuilder().where().eq("resource_type", type.name()).queryForFirst();

        if (reference == null) {
            reference = new ExternalReference(0, type.name(), identifier);
            externalReferenceDao.create(reference);
            return;
        }

        reference.setIdentifier(identifier);
        externalReferenceDao.update(reference);
    }
}
