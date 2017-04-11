package org.bukkit.craftbukkit.loottable.properties;

import com.google.common.collect.Maps;
import org.bukkit.util.ResourceLocation;

import java.util.Map;


public class EntityPropertyManager {

    private static Map<ResourceLocation, EntityProperty.Serializer<?>> nameToSerializerMap = Maps.newHashMap();
    private static Map<Class<? extends EntityProperty>, EntityProperty.Serializer<?>> classToSerializerMap = Maps.newHashMap();

    public EntityPropertyManager() {
    }

    static {
        registerProperty(new EntityOnFire.Serializer());
    }

    public static <T extends EntityProperty> void registerProperty(EntityProperty.Serializer<? extends T> property) {
        ResourceLocation resourcelocation = property.getName();
        Class oclass = property.getPropertyClass();
        if (nameToSerializerMap.containsKey(resourcelocation)) {
            throw new IllegalArgumentException("Can\'t re-register entity property name " + resourcelocation);
        } else if (classToSerializerMap.containsKey(oclass)) {
            throw new IllegalArgumentException("Can\'t re-register entity property class " + oclass.getName());
        } else {
            nameToSerializerMap.put(resourcelocation, property);
            classToSerializerMap.put(oclass, property);
        }
    }

    public static EntityProperty.Serializer<?> getSerializerForName(ResourceLocation name) {
        EntityProperty.Serializer serializer = nameToSerializerMap.get(name);
        if(serializer == null) {
            throw new IllegalArgumentException("Unknown loot entity property \'" + name + "\'");
        } else {
            return serializer;
        }
    }

    public static <T extends EntityProperty> EntityProperty.Serializer<T> getSerializerFor(T property) {
        EntityProperty.Serializer serializer = classToSerializerMap.get(property.getClass());
        if(serializer == null) {
            throw new IllegalArgumentException("Unknown loot entity property " + property);
        } else {
            return serializer;
        }
    }
}
