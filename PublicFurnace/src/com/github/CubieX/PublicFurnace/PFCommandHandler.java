package com.github.CubieX.PublicFurnace;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PFCommandHandler implements CommandExecutor
{
   private PublicFurnace plugin = null;
   private PFConfigHandler cHandler = null;

   public PFCommandHandler(PublicFurnace plugin, PFConfigHandler cHandler) 
   {
      this.plugin = plugin;
      this.cHandler = cHandler;
   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
   {
      Player player = null;

      if (sender instanceof Player) 
      {
         player = (Player) sender;
      }

      if (cmd.getName().equalsIgnoreCase("pf"))
      {
         if (args.length == 0)
         { //no arguments, so help will be displayed
            return false;
         }
         
         if (args.length==1)
         {
            if (args[0].equalsIgnoreCase("version")) // argument 0 is given and correct
            {
               if(PublicFurnace.language.equals("de")){sender.sendMessage(ChatColor.GREEN + "Auf diesem Server laeuft " + plugin.getDescription().getName() + " Version " + plugin.getDescription().getVersion());}
               if(PublicFurnace.language.equals("en")){sender.sendMessage(ChatColor.GREEN + "This server is running " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion());}               
               
               return true;
            }
            
            if (args[0].equalsIgnoreCase("reload")) // argument 0 is given and correct
            {            
               if(sender.hasPermission("publicfurnace.admin"))
               {                        
                  cHandler.reloadConfig(sender);
                  return true;
               }
               else
               {
                  if(PublicFurnace.language.equals("de")){sender.sendMessage(ChatColor.RED + "Du hast keine Erlaubnis zum Neuladen von " + plugin.getDescription().getName() + "!");}
                  if(PublicFurnace.language.equals("en")){sender.sendMessage(ChatColor.RED + "You do not have sufficient permission to reload " + plugin.getDescription().getName() + "!");}
               }
            }
           
            if (args[0].equalsIgnoreCase("help")) // argument 0 is given and correct
            {
               if(null != player)
               {
                  if(PublicFurnace.language.equals("de")){player.sendMessage(ChatColor.GREEN + PublicFurnace.logPrefix + "Erstellen eines oeffentlichen Ofens:\n" + ChatColor.WHITE +
                        "Erstelle ein Schild:\n" +                        
                        "1. Linie: public\n" +
                        "2. Linie: furnace\n" +
                        "Setze einen Ofen direkt unter das Schild.");}
                  
                  if(PublicFurnace.language.equals("en")){player.sendMessage(ChatColor.GREEN + PublicFurnace.logPrefix + "How to create a public furnace:\n" + ChatColor.WHITE +
                        "Create a sign:\n" +                        
                        "1st Line: public\n" +
                        "2nd Line: furnace\n" +
                        "Place a furnace directly below the sign.");}
               }
               else
               {
                  if(PublicFurnace.language.equals("de")){sender.sendMessage(PublicFurnace.logPrefix + "Erstellen eines oeffentlichen Ofens: Erstelle ein Schild: 1. Linie: public 2. Linie: furnace. Setze einen Ofen direkt unter das Schild.");}
                  if(PublicFurnace.language.equals("en")){sender.sendMessage(PublicFurnace.logPrefix + "How to create a public furnace: Create a sign: 1st Line: public, 2nd Line: furnace. Place a furnace directly below the sign.");}
               }

               return true;
            }
         }
         else
         {
            if(PublicFurnace.language.equals("de")){sender.sendMessage(ChatColor.YELLOW + "Falsche Anzahl an Parametern!");}
            if(PublicFurnace.language.equals("en")){sender.sendMessage(ChatColor.YELLOW + "Wrong parameter count!");}            
         }                

      }         
      return false; // if false is returned, the help for the command stated in the plugin.yml will be displayed to the player
   }
}
