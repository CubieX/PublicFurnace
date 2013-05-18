package com.github.CubieX.PublicFurnace;

import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PFEntityListener implements Listener
{
   private PublicFurnace plugin = null;
   private HashMap<String, String> lockedFurnaces = new HashMap<String, String>(); // Coords of Furnace (x_y_z_world), playerName

   public PFEntityListener(PublicFurnace plugin)
   {
      this.plugin = plugin;

      plugin.getServer().getPluginManager().registerEvents(this, plugin);
   }

   //================================================================================================
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onFurnaceInventoryClose(InventoryCloseEvent e)
   {
      // this can only be triggered by a player with override permissions or the player who locked this furnace because
      // of the handling of the PlayerInteractEvent
      // so no further permission checks needed here

      boolean empty = true;

      if(e.getInventory().getHolder() instanceof Furnace)
      {
         Furnace fur = (Furnace) e.getInventory().getHolder();
         String key = fur.getX() + "_" + fur.getY() + "_" + fur.getZ() + "_" + fur.getWorld().getName();

         if(lockedFurnaces.containsKey(key))
         {
            // its locked, so check if player has taken out all smelting items and all resulting items (Slot 0 and 2 in Content-Array) then unlock it
            if((null != fur.getInventory().getContents()[0]) || (null != fur.getInventory().getContents()[2]))
            {
               empty = false; // player took not all of the items out of the furnaces smelt material and resulting slot
               // fuel material will be ignored and can be used by the next one
            }

            if(empty)
            {
               // player has taken all items out of the furnace, so unlock it for other players to use
               lockedFurnaces.remove(key);
            }
         }
      }      
   }

   //================================================================================================
   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void onPlayerInteractEvent(PlayerInteractEvent e)
   {
      if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
      {
         if((e.getClickedBlock().getType() == Material.FURNACE) ||
               (e.getClickedBlock().getType() == Material.BURNING_FURNACE))
         {
            Furnace fur = (Furnace) e.getClickedBlock().getState();
            String key = fur.getX() + "_" + fur.getY() + "_" + fur.getZ() + "_" + fur.getWorld().getName();

            if(lockedFurnaces.containsKey(key))
            {
               if(!lockedFurnaces.get(key).equals(e.getPlayer().getName()))
               {
                  // furnace locked by another player
                  if((!e.getPlayer().isOp()) &&
                        (!e.getPlayer().hasPermission("publicfurnace.admin") &&
                              (!e.getPlayer().hasPermission("publicfurnace.manage"))))
                  {
                     // block access
                     e.setCancelled(true);
                     if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.GOLD + "Dieser oeffentliche Ofen ist momentan gesperrt durch " + ChatColor.GREEN + lockedFurnaces.get(key) + "\n" +
                           ChatColor.GOLD + "Bitte versuche es nochmal, wenn er fertig ist mit Schmelzen.");}
                     
                     if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.GOLD + "This public furnace is currently locked by " + ChatColor.GREEN + lockedFurnaces.get(key) + "\n" +
                           ChatColor.GOLD + "Please try again, when he has finished his smelting.");}
                  }
               }
            }
            else
            {
               // not yet locked, so lock it for current player
               lockedFurnaces.put(key, e.getPlayer().getName());
            }
         }
      }
   }

   //================================================================================================
   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void onSignChange(SignChangeEvent e)
   {
      boolean condtitionsOK = true;

      if((e.getLine(0).equalsIgnoreCase("Public")) &&
            (e.getLine(1).equalsIgnoreCase("Furnace")))
      {
         if((e.getPlayer().isOp()) ||
               (e.getPlayer().hasPermission("publicfurnace.admin") ||
                     (e.getPlayer().hasPermission("publicfurnace.manage"))))
         {
            e.setLine(0, "");
            e.setLine(1, "[Public]"); // write it in the middle lines of the sign
            e.setLine(2, "[Furnace]");

            if(e.getPlayer().getWorld().getBlockAt(e.getBlock().getX(), e.getBlock().getY() - 1, e.getBlock().getZ()).getType() == Material.FURNACE)
            {              
               if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.GREEN + "Oeffentlicher Ofen wurde erstellt!");}
               if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.GREEN + "Public furnace has been created!");}               
            }
            else
            {
               if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.GOLD + "Bitte setze einen Ofen unter dieses Schild!");}
               if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.GOLD + "Pleace place a furnace below this sign!");}               
            }
         }
         else
         {
            condtitionsOK = false;
            if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.RED + "Du hast keine Rechte um PublicFurnace-Schilder zu erstellen!");}
            if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.RED + "You have no permission to create PublicFurnace signs!");}            
         }

         if(!condtitionsOK)
         {
            e.getBlock().breakNaturally();            
         }
      }
   }

   //================================================================================================
   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void onBlockBreak(BlockBreakEvent e)
   {
      // PROTECT PF SIGNS ###############################
      if((e.getBlock().getType() == Material.WALL_SIGN) || (e.getBlock().getType() == Material.SIGN_POST))
      {
         Sign sign = (Sign) e.getBlock().getState();

         if((sign.getLine(1).equals("[Public]")) &&
               (sign.getLine(2).equals("[Furnace]")))
         {
            if((e.getPlayer().isOp()) ||
                  (e.getPlayer().hasPermission("publicfurnace.admin") ||
                        (e.getPlayer().hasPermission("publicfurnace.manage"))))
            {
               return;
            }
            else
            {
               e.setCancelled(true);
               if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.RED + "Du hast keine Rechte um PublicFurnace-Schilder zu zerstoeren!");}
               if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.RED + "You have no permission to destroy PublicFurnaces signs!");}               
            }
         }
      }   

      // PROTECT PF FURNACES BELOW PF SIGNS ###############################
      if((e.getBlock().getType() == Material.FURNACE) || (e.getBlock().getType() == Material.BURNING_FURNACE))
      {
         Furnace fur = (Furnace) e.getBlock().getState();

         // get block above Furnace to check if it is a PF sign
         if((e.getPlayer().getWorld().getBlockAt(fur.getX(), fur.getY() + 1, fur.getZ()).getType() == Material.WALL_SIGN) ||
               (e.getPlayer().getWorld().getBlockAt(fur.getX(), fur.getY() + 1, fur.getZ()).getType() == Material.SIGN_POST))
         {
            Sign sign = (Sign) e.getPlayer().getWorld().getBlockAt(fur.getX(), fur.getY() + 1, fur.getZ()).getState();

            if((sign.getLine(1).equals("[Public]")) &&
                  (sign.getLine(2).equals("[Furnace]")))
            {
               if((e.getPlayer().isOp()) ||
                     (e.getPlayer().hasPermission("publicfurnace.admin") ||
                           (e.getPlayer().hasPermission("publicfurnace.manage"))))
               {
                  String key = fur.getX() + "_" + fur.getY() + "_" + fur.getZ() + "_" + fur.getWorld().getName();

                  // remove lock of this furnace, because it's about to be destroyed
                  if(lockedFurnaces.containsKey(key))
                  {
                     lockedFurnaces.remove(key);
                  }
                  return;
               }
               else
               {
                  e.setCancelled(true);
                  if(PublicFurnace.language == "de"){e.getPlayer().sendMessage(ChatColor.RED + "Du hast keine Rechte um oeffentliche Oefen zu zerstoeren!");}
                  if(PublicFurnace.language == "en"){e.getPlayer().sendMessage(ChatColor.RED + "You have no permission to destroy public furnaces!");}                  
               }
            }
         }
      }
   }
}

