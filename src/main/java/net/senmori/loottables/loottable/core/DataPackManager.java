package net.senmori.loottables.loottable.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.senmori.loottables.loottable.utils.DataPack;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class DataPackManager {
    private static final DataPackManager INSTANCE = new DataPackManager();

    private Multimap<Plugin, DataPack> pluginDataPacks = ArrayListMultimap.create();

    private DataPackManager() {

    }

    public static DataPackManager getInstance() {
        return INSTANCE;
    }

    public DataPack registerDataPack(Plugin plugin, DataPack pack) {
        if(getMatchingPacks(pack).size() > 0) {
            throw new IllegalArgumentException("A DataPack already exists with that name and world UUID");
        }
        pluginDataPacks.put(plugin, pack);
        return pack;
    }

    public List<DataPack> getPacks(String name) {
        Validate.notNull(name, "Name cannot be null");
        Validate.isTrue(name.length() > 0, "Name cannot be empty");
        List<DataPack> packs = Lists.newArrayList();
        pluginDataPacks.entries().forEach(e -> {
            if(e.getValue().getName().equals(name)) {
                packs.add(e.getValue());
            }
        });
        return packs;
    }

    public List<DataPack> getPacks(Plugin plugin) {
        return Lists.newArrayList(pluginDataPacks.get(plugin));
    }

    public List<DataPack> getPacks(UUID worldUUID) {
        List<DataPack> packs = Lists.newArrayList();
        pluginDataPacks.entries().forEach(e -> {
            if(e.getValue().getWorldUID().equals(worldUUID)) {
                packs.add(e.getValue());
            }
        });
        return packs;
    }

    private List<DataPack> getMatchingPacks(DataPack pack) {
        List<DataPack> packs = Lists.newArrayList();
        pluginDataPacks.values().forEach(v -> {
            if(v.equals(pack)) {
                packs.add(v);
            }
        });
        return packs;
    }
}
