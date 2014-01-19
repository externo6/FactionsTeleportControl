package externo6.factionsteleportcontrol.Cmds;

import externo6.factionsteleportcontrol.config.Config;

import org.bukkit.ChatColor;

import com.massivecraft.factions.struct.Permission;



public class CmdReloadFTC extends FTCCommand {
	
	public CmdReloadFTC() {
		super();
		this.aliases.add( "reloadftc" );
		
		//this.optionalArgs.put( "all|conf|templates", "all");
		this.permission = Permission.RELOAD.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		this.errorOnToManyArgs = true;
		
		this.setHelpShort( "Reloads Factions Teleport Control config" );
	}
	
	
	@Override
	public void performfp() {
		long startTime = System.nanoTime();
		
		boolean success = false;
		
		try {
			Config.reload();
			success = true;
		} catch ( Throwable t ) {
			t.printStackTrace();
			success = false;
		} finally {
			long endTime = (System.nanoTime() - startTime) / 1000000;
			
			if ( success ) {
				msg( "<i>Reloaded FactionPlus <h>config.yml <i>from disk, took <h>%,2dms<i>.", String.valueOf( endTime) );
			} else {
				msg( ChatColor.RED+"Errors occurred while loading config.yml. See console for details.");
			}
		}
		
	}
	
}
