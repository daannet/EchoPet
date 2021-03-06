package com.github.dsh105.echopet.listeners;


import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.dsh105.echopet.EchoPet;
import com.github.dsh105.echopet.entity.pet.CraftPet;
import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.Pet;
import com.github.dsh105.echopet.util.Lang;

public class PetOwnerListener implements Listener {
	
	private EchoPet ec;
	
	public PetOwnerListener(EchoPet ec) {
		this.ec = ec;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Pet pet = ec.PH.getPet(p);
		if (pet != null && (event.getRightClicked() instanceof CraftPet)) {
			event.setCancelled(true);
			((EntityPet) pet.getEntityPet()).a(((CraftPlayer) p).getHandle());
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Entity e = event.getDamager();
			if (e instanceof CraftPet) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		Pet pi = ec.PH.getPet(p);
		if (pi != null) {
			if (event.getFrom().getWorld() == event.getTo().getWorld()) {
				pi.teleportToOwner();
			}
			else {
				ec.PH.removePets(p); // Safeguard for Multiworld travel
				p.sendMessage(Lang.DIMENSION_CHANGE.toString());
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		Pet pi = ec.PH.getPet(p);
		if (pi != null) {
			//ec.PH.saveFileData("autosave", pi);
			ec.PH.removePet(pi);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		if (ec.update && p.hasPermission("echopet.update")) {
			p.sendMessage(ec.prefix + ChatColor.GOLD + "An update is available: " + ec.name + " (" + ec.size + " bytes).");
			p.sendMessage(ec.prefix + ChatColor.GOLD + "Type /ecupdate to update.");
		}
		new BukkitRunnable() {
			
			public void run() {
				loadPets(p);
			}
			
		}.runTaskLater(ec, 20);
	}
	
	private void loadPets(Player p) {
		
		if (ec.getPetConfig().get("default." + p.getName() + ".pet.type") != null) {
			Pet pi = ec.PH.createPetFromFile("default", p);
			if (pi == null) {
				ec.PH.removePet(ec.PH.getPet(p));
			}
			else {
				p.sendMessage(Lang.DEFAULT_PET_LOAD.toString().replace("%petname%", pi.getPetName().toString()));
			}
			return;
		}
		
		if (ec.DO.autoLoadPets(p)) {
			String w = p.getWorld().getName();
			if (ec.getPetConfig().get("autosave." + "." + w + "." + p.getName() + ".pet.type") != null) {
				Pet pi = ec.PH.createPetFromFile("autosave", p);
				if (pi == null) {
					ec.PH.removePet(ec.PH.getPet(p));
				}
				else {
					p.sendMessage(Lang.AUTOSAVE_PET_LOAD.toString().replace("%world%", w).replace("%petname%", pi.getPetName().toString()));
				}
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Pet pet = ec.PH.getPet(p); 
		if (pet != null) {
			ec.PH.removePet(pet);
			p.sendMessage(Lang.REMOVE_PET_DEATH.toString());
		}
	}
}