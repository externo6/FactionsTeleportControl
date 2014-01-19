package externo6.factionsteleportcontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.utils.LocationUtil;

import net.ess3.api.InvalidWorldException;

/**
 * for this to compile, you'll have to either use the Essentials 2.9.2 jar in project or the 2.9 branch of Essentials 
 * project from github, thanks KHobbits
 */
public abstract class EssentialsIntegration {
	private static final String	pluginName	= "Essentials";
	
	private static IEssentials		ess							= null;
//	public static ESS_HAVE			haveEssentials				= ESS_HAVE.NOT_INITED;			// since last Plugin.onEnable()
	private static boolean	isLoadedButNotEnabled=false;
							
	
	public synchronized static void onDisable(){
		ess=null;
		isLoadedButNotEnabled=false;
	}
	
//	public enum ESS_HAVE {
//		INITED_AND_HAVE, NOT_INITED, INITED_AND_NOT_HAVE
//	}
	
//	
//	public final static boolean enableOrDisableEssentialsIntegration() {
//		boolean currentState = haveEssentials.equals( ESS_HAVE.INITED_AND_HAVE);
//		
//		if (haveEssentials.equals( ESS_HAVE.NOT_INITED)) {
//			if (Config._teleports.disallowTeleportingToEnemyLandViaHomeCommand._) {
//				//this needs it enabled
//				boolean ret = turnOnEsI();
//				
//				if (!EssentialsIntegration.isHooked()){
//					FactionsTeleportControl.warn("Due to failing to hook into Essentials plugin" +
//							" the following enabled config option will have no effect: "+
//							Config._teleports.disallowTeleportingToEnemyLandViaHomeCommand._dottedName_asString+
//							"\nHowever you may try /f reloadftc to cause this recheck");
//				}	
//				return ret;
//			}
//		}else {
//			if (!Config._teleports.disallowTeleportingToEnemyLandViaHomeCommand._) {
//				//nolonger needing esi
//				return turnOffEsI();
//			}
//		}
//		
//		//state is the same if we're here
//		return currentState;
//	}
//	
//	private synchronized static boolean turnOffEsI() {
//		assert isHooked();
//		ess=null;
//		haveEssentials=ESS_HAVE.NOT_INITED;
//		FactionsTeleportControl.info( pluginName+" integration is OFF" );
//		return false;
//	}
//
//	private synchronized static boolean turnOnEsI() {
//		assert !isHooked();
//		
//		refreshEssInst();
//		if (haveEssentials.equals( ESS_HAVE.INITED_AND_HAVE )) {
//			
//		}
//		FactionsTeleportControl.info( ess + " " + haveEssentials );
//		FactionsTeleportControl.info( pluginName+" integration is ON" );
//		return false;
//	}
	
	/**
	 * with lazy init, due to the fact that Essentials being a soft-depend, my guess is that there is a possibility it can be 
	 * not enabled, ie. it may enable after our plugin enables<br>
	 * 
	 * @return the instance or null
	 */
	private synchronized static final IEssentials getEssentialsInstance() {
		//fixed: caching the instance should be a bad idea if something like plugman reloads or unloads only the Essentials plugin
		//the following cases are handled:
		//plugman reload - doesn't change the reference
		//plugman unload then plugman reload - changes the reference (this breaks Factions but not FP currently, although /f warp x will trigger it)
		//plugman unload when previously was loaded
		//plugman load when previously was unloaded/nonexistent
		
		Plugin essPlugin;
		if (	   ( null == ess ) 
				|| (!ess.isEnabled())
				//short circuit may not eval the next one
				|| (ess != (essPlugin = Bukkit.getPluginManager().getPlugin( pluginName ))) ) {
			
			// lazyly init or : maybe add depend (not soft) in plugin.yml
			essPlugin = Bukkit.getPluginManager().getPlugin( pluginName );
			
			if ( ( null != essPlugin ) && ( essPlugin.isEnabled() ) ) {
				ess = (IEssentials)essPlugin;
				isLoadedButNotEnabled=!essPlugin.isEnabled();
//				haveEssentials = ESS_HAVE.INITED_AND_HAVE;
//			} else {
//				haveEssentials = ESS_HAVE.INITED_AND_NOT_HAVE;
//			}else{
//				isLoadedButNotEnabled=false;
				FactionsTeleportControl.info( "updated the cached reference to Essentials' instance: `"+ess+"`" );
			}else {
				if (null != ess) {
					FactionsTeleportControl.info( "Essentials plugin is nolonger on the system");
				}
				ess=null;
				
			}
			
			
		}
		
		return ess;// can be null
	}
	
	
	public final static boolean isLoadedButNotEnabled() {
		return !isHooked() && isLoadedButNotEnabled;
	}
	
//	private synchronized final static void refreshEssInst(){
//		
//	}

	public synchronized final static boolean isHooked() {
		return null != getEssentialsInstance();
	}
	
	public final static Location getHomeForPlayer( Player player, String homeName ) throws Exception {
		checkInvariants();
		try {
			       return( getEssentialsInstance().getUser( player ).getHome( homeName ) );
			     } catch (InvalidWorldException e) {
			      
			      player.sendMessage(ChatColor.RED + "The home " + homeName + " was in the world "+e.getWorld()+", but that world is no longer existant..");
			      return( null );
			     }
	}
	
	
	public final static int getHomesCount( Player player ) {
		checkInvariants();
		
		return getEssentialsInstance().getUser( player ).getHomes().size();
	}
	

	/**
	 * @param player
	 * @return can be null
	 */
	public final static Location getLastLocation( Player player) {
		checkInvariants();
		return getEssentialsInstance().getUser( player ).getLastLocation();
	}
	
	
	private final static void checkInvariants() {
		if (!isHooked()) {
			throw new RuntimeException("coding error: using "+pluginName+" functions while it was not hooked");
		}
	}

	public static Location getSafeDestination( Location targetLocation ) throws Exception {
		if (isHooked()){
			
			return LocationUtil.getSafeDestination( targetLocation );
		}else{
			//not running Essentials on server? return same location
			return targetLocation;
		}
	}
}
