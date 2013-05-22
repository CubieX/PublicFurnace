package com.github.CubieX.PublicFurnace;

public class PFSchedulerHandler
{
   private PublicFurnace plugin = null;

   public PFSchedulerHandler(PublicFurnace plugin)
   {
      this.plugin = plugin;
   }

   public void startPlayerInWaterCheckScheduler_SynchRepeating()
   {
      plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
      {
         public void run()
         {
                    
         }
      }, 10 * 20L, 1 * 20L); // 10 seconds delay, 1 second cycle
   }
}
