package org.metamechanists.quaptics.storage;

import com.google.gson.JsonObject;
import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataHolder;
import org.metamechanists.quaptics.utils.Keys;
import org.metamechanists.quaptics.utils.id.CustomId;

public class DataTraverser {
    private final PersistentDataHolder persistentDataHolder;
    @Getter
    private final JsonObject data;

    public DataTraverser(final CustomId id) {
        this.persistentDataHolder = Bukkit.getEntity(id.getUUID());

        final JsonObject data = PersistentDataAPI.getJsonObject(persistentDataHolder, Keys.DATA);
        this.data = data == null
                ? new JsonObject()
                : data;
    }

    public void save() {
        PersistentDataAPI.setJsonObject(persistentDataHolder, Keys.DATA, data);
    }
}
