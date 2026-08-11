package de.ztiger.faibot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "external_reference")
public class ExternalReference {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "resource_type")
    private String resourceType;

    @DatabaseField
    @Getter
    @Setter
    private String identifier;
}
