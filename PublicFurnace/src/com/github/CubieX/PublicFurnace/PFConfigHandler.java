package com.github.CubieX.PublicFurnace;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.CubieX.PublicFurnace.PublicFurnace;

public class PFConfigHandler 
{
   private FileConfiguration config;
   private final PublicFurnace plugin;

   public PFConfigHandler(PublicFurnace plugin) 
   {
      this.plugin = plugin;      

      initConfig();
   }

   private void initConfig()
   {
      plugin.saveDefaultConfig(); //creates a copy of the provided config.yml in the plugins data folder, if it does not exist
      config = plugin.getConfig(); //re-reads config out of memory. (Reads the config from file only, when invoked the first time!)
   }
   
   public FileConfiguration getConfig()
   {
      return (config);
   }

   public void saveConfig() //saves the config to disc (needed when entries have been altered via the plugin in-game)
   {
      // get and set values here!
      plugin.saveConfig();
   }

   //reloads the config from disc (used if user made manual changes to the config.yml file)
   public void reloadConfig(CommandSender sender)
   {
      plugin.reloadConfig();
      config = plugin.getConfig(); // new assignment necessary when returned value is assigned to a variable or static field(!)
      plugin.readConfigValues();

      if(PublicFurnace.language.equals("de")){sender.sendMessage(PublicFurnace.logPrefix + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion() + " neu geladen!");}
      if(PublicFurnace.language.equals("en")){sender.sendMessage(PublicFurnace.logPrefix + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion() + " reloaded!");}
   } 
}
