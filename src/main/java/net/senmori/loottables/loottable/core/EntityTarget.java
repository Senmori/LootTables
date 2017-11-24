package net.senmori.loottables.loottable.core;


public enum EntityTarget {
    /** The entity that was killed */
    THIS,
    /** The entity who killed this entity */
    KILLER,
    /** The entity, if it is a player, who killed this entity */
    KILLER_PLAYER;


    EntityTarget() {
    }

    public static EntityTarget fromString(String name) {
        for (EntityTarget target : EntityTarget.values()) {
            if (target.toString().toLowerCase().equals(name.toLowerCase())) {
                return target;
            }
        }
        return null;
    }
}
