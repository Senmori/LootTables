package net.senmori.loottables.loottable.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.senmori.loottables.loottable.utils.DataPack;
import net.senmori.loottables.loottable.utils.IDataPack;
import org.apache.commons.lang3.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DataPackManager {
    private static final DataPackManager INSTANCE = new DataPackManager();

    private Multimap<String, IDataPack> pluginDataPacks = ArrayListMultimap.create();
    private Set<IDataPack> enabledPacks = Sets.newHashSet();
    private List<IDataPack> orderedPacks = Lists.newArrayList();

    private DataPackManager() {

    }

    public static DataPackManager getInstance() {
        return INSTANCE;
    }

    public IDataPack registerDataPack(Plugin plugin, IDataPack pack) {
        if(getMatchingPacks(pack).size() > 0) {
            throw new IllegalArgumentException("A DataPack already exists with that name and world UUID");
        }
        pluginDataPacks.put(plugin.getName(), pack);
        return pack;
    }

    public boolean enablePack(IDataPack pack) {
        enabledPacks.add(pack);
        pack.setEnabled(true);
        return pack.isEnabled();
    }

    public boolean disablePack(IDataPack pack) {
        enabledPacks.remove(pack);
        pack.setEnabled(false);
        return pack.isEnabled();
    }

    public List<IDataPack> getPacks(NamespacedKey name) {
        Validate.notNull(name, "Name cannot be null");
        Validate.isTrue(name.getKey().length() > 0, "Name cannot be empty");
        List<IDataPack> packs = Lists.newArrayList();
        pluginDataPacks.entries().forEach(e -> {
            if(e.getValue().getKey().equals(name)) {
                packs.add(e.getValue());
            }
        });
        return packs;
    }

    public List<IDataPack> getPacks(Plugin plugin) {
        return Lists.newArrayList(pluginDataPacks.get(plugin.getName()));
    }

    public List<IDataPack> getPacks(UUID worldUUID) {
        List<IDataPack> packs = Lists.newArrayList();
        pluginDataPacks.entries().forEach(e -> {
            if(e.getValue().getWorldUID().equals(worldUUID)) {
                packs.add(e.getValue());
            }
        });
        return packs;
    }

    private List<IDataPack> getMatchingPacks(IDataPack pack) {
        List<IDataPack> packs = Lists.newArrayList();
        pluginDataPacks.values().forEach(v -> {
            if(v.equals(pack)) {
                packs.add(v);
            }
        });
        return packs;
    }
}
