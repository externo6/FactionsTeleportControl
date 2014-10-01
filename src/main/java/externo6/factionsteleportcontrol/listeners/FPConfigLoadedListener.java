package externo6.factionsteleportcontrol.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import externo6.factionsteleportcontrol.FactionsTeleportControl;
import externo6.factionsteleportcontrol.events.FPConfigLoadedEvent;



public class FPConfigLoadedListener implements Listener {
	
	/**
	 * called after the config is (re)loaded, which is typically when plugin is enabled and when /f reloadftc  is issued<br>
	 */
	@EventHandler
	public void onConfigLoaded( FPConfigLoadedEvent event ) {
        //TODO: add more here and make sure they can change states between on/off just like they would by a server 'reload' command
        //because this hook is called every time the config is reloaded, which means some things could have been previously enabled
        //and now the config may dictate that they are disabled (state changed) so we must properly handle that behaviour.
        TeleportsListener.initOrDeInit(FactionsTeleportControl.instance);
       
        @SuppressWarnings("unused")
		PluginManager pm = Bukkit.getServer().getPluginManager();
              
	} //onConfigLoaded method ends
}
