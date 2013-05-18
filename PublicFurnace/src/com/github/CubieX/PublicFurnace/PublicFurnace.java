package com.github.CubieX.PublicFurnace;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PublicFurnace extends JavaPlugin
{
   static final Logger log = Bukkit.getServer().getLogger();
   static final String logPrefix = "[PublicFurnace] "; // Prefix to go in front of all log entries
   static ArrayList<String> availableLanguages = new ArrayList<String>();
   
   private PublicFurnace plugin = null;
   private PFCommandHandler comHandler = null;
   private PFConfigHandler cHandler = null;
   private PFEntityListener eListener = null;
   //private BSchedulerHandler schedHandler = null;

   static boolean debug = false;
   static String language = "en";

   //*************************************************
   static String usedConfigVersion = "1"; // Update this every time the config file version changes, so the plugin knows, if there is a suiting config present
   //*************************************************

   // TODO make a time limit for locked furnaces to unlock them.
   
   @Override
   public void onEnable()
   {
      this.plugin = this;
      initLanguageList();
      cHandler = new PFConfigHandler(this);

      if(!checkConfigFileVersion())
      {
         log.severe(logPrefix + "Outdated or corrupted config file(s). Please delete your config files."); 
         log.severe(logPrefix + "will generate a new config for you.");
         log.severe(logPrefix + "will be disabled now. Config file is outdated or corrupted.");
         getServer().getPluginManager().disablePlugin(this);
         return;
      }      

      eListener = new PFEntityListener(this);      
      comHandler = new PFCommandHandler(this, cHandler);      
      getCommand("pf").setExecutor(comHandler);

      //schedHandler = new BSchedulerHandler(this);

      readConfigValues();

      log.info(this.getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");

      //schedHandler.startPlayerInWaterCheckScheduler_SynchRepeating();
   }
   
   private void initLanguageList()
   {
      availableLanguages.add("de");
      availableLanguages.add("en");
   }

   private boolean checkConfigFileVersion()
   {      
      boolean configOK = false;     

      if(cHandler.getConfig().isSet("config_version"))
      {
         String configVersion = cHandler.getConfig().getString("config_version");

         if(configVersion.equals(usedConfigVersion))
         {
            configOK = true;
         }
      }

      return (configOK);
   }
   
   public void readConfigValues()
   {
      boolean exceed = false;
      boolean invalid = false;

      debug = cHandler.getConfig().getBoolean("debug");
      
      language = cHandler.getConfig().getString("language");
      
      if(!availableLanguages.contains(language))
      {
         language = "en"; // English is default
         invalid = true;
      }
     
      if(exceed)
      {
         log.warning("One or more config values are exceeding their allowed range. Please check your config file!");
      }

      if(invalid)
      {
         log.warning("One or more config values are invalid. Please check your config file!");
      }
   }

   @Override
   public void onDisable()
   {
      this.getServer().getScheduler().cancelAllTasks();
      cHandler = null;
      eListener = null;
      comHandler = null;
      //schedHandler = null; // TODO ACTIVATE THIS AGAIN IF USED!
      log.info(this.getDescription().getName() + " version " + getDescription().getVersion() + " is disabled!");
   }

   // #########################################################

   
}


