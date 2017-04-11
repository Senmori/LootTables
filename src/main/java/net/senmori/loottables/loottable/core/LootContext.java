package org.bukkit.craftbukkit.loottable.core;

import com.google.common.collect.Sets;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;

public class LootContext {
	
	protected float luck;
	protected World world;
	protected LootTableManager lootTableManager;
	protected Entity lootedEntity;
	protected Entity killer;
	protected Player player;
	protected Set<LootTable> lootTables = Sets.newLinkedHashSet();
	
	public LootContext(float luckIn, World world, LootTableManager manager, Entity lootedEntity, Entity killer, Player player) {
		this.luck = luckIn;
		this.world = world;
		this.lootTableManager = manager;
		this.lootedEntity = lootedEntity;
		this.killer = killer;
		this.player = player;
	}
	
	public float getLuck() {
		return this.luck;
	}
	
	public Entity getLootedEntity() {
		return this.lootedEntity;
	}
	
	public Player getKillerPlayer(){
		return this.player;
	}
	
	public Entity getKiller() {
		return this.killer;
	}
	
	public boolean addLootTable(LootTable table) {
		return this.lootTables.add(table);
	}
	
	public void removeLootTable(LootTable table) {
		this.lootTables.remove(table);
	}
	
	public LootTableManager getLootTableManager() {
		return lootTableManager;
	}


	public Entity getEntity(EntityTarget target) {
		switch(target) {
			case THIS:
				return this.getLootedEntity();
			case KILLER:
				return this.getKiller();
			case KILLER_PLAYER:
				return this.getKillerPlayer();
			default:
				return null;
		}
	}
	
	public static class Builder {
		private World world;
		private float luck;
		private Entity lootedEntity;
		private Player player;
		private Entity killer;
		private LootTableManager lootTableManager;
		
		public Builder(World world, LootTableManager manager) {
			this.world = world;
			this.lootTableManager = manager;
		}
		
		public LootContext.Builder withLuck(float luck) {
			this.luck = luck;
			return this;
		}
		
		public LootContext.Builder withLootedEntity(Entity lootedEntity) {
			this.lootedEntity = lootedEntity;
			return this;
		}
		
		public LootContext.Builder withPlayer(Player player) {
			this.player = player;
			return this;
		}
		
		public LootContext.Builder withKillerEntity(Entity killer) {
			this.killer = killer;
			return this;
		}
		
		public LootContext build() {
			return new LootContext(luck, world, lootTableManager, lootedEntity, killer, player);
		}
	}
}
