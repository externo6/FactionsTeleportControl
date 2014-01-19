package externo6.factionsteleportcontrol;

import java.util.Set;
import java.util.logging.Logger;

import externo6.factionsteleportcontrol.FactionsBridge.Bridge;
import externo6.factionsteleportcontrol.config.Config;
import externo6.factionsteleportcontrol.listeners.CoreListener;
import externo6.factionsteleportcontrol.listeners.FPConfigLoadedListener;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionsTeleportControl extends FactionsTeleportControlPlugin {

	public static FactionsTeleportControl instance;
	
	public static Logger log = Logger.getLogger("Minecraft");
	
	Factions factions;
	FPlayers fplayers;
	Faction faction;
	 
    public static Permission permission = null;
    
	
	public final CoreListener corelistener = new CoreListener();
	
	public static String version;
	public static String FactionsVersion;
	
	public static Set<String> ignoredPvPWorlds = com.massivecraft.factions.Conf.worldsIgnorePvP;
	public static Set<String> noClaimingWorlds = com.massivecraft.factions.Conf.worldsNoClaiming;
	public static Set<String> noPowerLossWorlds = com.massivecraft.factions.Conf.worldsNoPowerLoss;
	
	public FactionsTeleportControl() {
		super();
		if (null != instance) {
			throw bailOut("this was not expected, getting new-ed again without getting unloaded first.\n"
				+"Safest way to reload is to stop and start the server!");
		}
		instance=this;
	}
	
	
	@Override
	public void onDisable() {
		Throwable failed = null;// TODO: find a way to chain all thrown exception rather than overwrite all older
		try {
			
			try {
				EssentialsIntegration.onDisable();
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				Config.deInit();
			} catch ( Throwable t ) {
				failed = t;
			}
			
			
			try {
				getServer().getServicesManager().unregisterAll( this );// not really needed at this point, only for when using
																		// .register(..)
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				HandlerList.unregisterAll( FactionsTeleportControl.instance );
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				//this will deInit metrics, but it will be enabled again onEnable
				getServer().getScheduler().cancelTasks( this);
			} catch ( Throwable t ) {
				failed = t;
			}
			
			if (null == failed) {
				FactionsTeleportControlPlugin.info( "Disabled successfuly." );
			}
			
		} catch ( Throwable t ) {
			failed = t;
		} finally {
			if ( null != failed ) {
				FactionsTeleportControlPlugin.info( "Did not disable successfuly." );
				FactionsTeleportControl.severe( failed, "This is the last seen exception:" );
			}
		}
	}
	
	
	@Override
	public void onEnable() {
		try {
			super.onEnable(); // Be first

			
			Config.init();
			Bridge.init();
			
			PluginManager pm = this.getServer().getPluginManager();
			FactionsVersion = pm.getPlugin("Factions").getDescription().getVersion().toLowerCase();
			
			FactionsTeleportControlPlugin.info("Factions version " + FactionsVersion ); // Before reload
			
			pm.registerEvents(new FPConfigLoadedListener(),this);
			
			Config.reload(); 
			
			pm.registerEvents(this.corelistener, this);
						
			FactionsTeleportControlCommandManager.setup();
			
	        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
	            permission = permissionProvider.getProvider();
	        }
	        	        	        
			FactionsTeleportControlPlugin.info("Ready. ");		
			
		} catch (Throwable t) {
			FactionsTeleportControl.severe( t);
			if (isEnabled()) {
				disableSelf();
			}
		} //try
	} //onEnable
	
	
}
