package externo6.factionsteleportcontrol.Cmds;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitWorker;

import com.massivecraft.factions.struct.Permission;

import externo6.factionsteleportcontrol.FactionsTeleportControl;
import externo6.factionsteleportcontrol.config.Typeo;

public class CmdDebug extends FTCCommand {
	public CmdDebug() {
		this.aliases.add("debug");
		this.errorOnToManyArgs = true;
		
		//this.requiredArgs.add("message");
		this.optionalArgs.put("configdiff", "");

		this.permission = Permission.HELP.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		
		this.setHelpShort("used to debug FactionsTeleportControl");
		
	}
	@Override
	public void performfp() {
		// TEMP, remove this completely after this inconsistency is fixed; if it's commented out it means it's not fixed
//		if ( null != fme ) {
//			Player player = Bukkit.getPlayerExact( fme.getId() );
//			assert Utilities.getOnlinePlayerExact( fme ).equals(player);
//			if ( null != player ) {
//				String perm = Section_Jails.permissionNodeNameForCanJailUnjail;//"factionsteleportcontrol.xyz"
//				sender.sendMessage( player.getWorld().toString() );
//				sender.sendMessage( "has perm: " + FactionsTeleportControl.permission.playerHas( player, perm ) );
//				sender.sendMessage( "has perm: " + FactionsTeleportControl.permission.playerHas( (World)null, player.getName(), perm ) );
//				sender.sendMessage( "has perm: "
//					+ FactionsTeleportControl.permission.playerHas( player.getWorld(), player.getName(), perm ) );
//			}
//		}

		if ( (null != fme) && (fme.isOnline()) && (!sender.isOp())) {
				return;
		}
		
		
		
		String param = this.argAsString(0);
		if ((param != null) && (param.trim().equalsIgnoreCase( "configdiff" ))) {
			Typeo.showDiff( sender );
			return;
		}
		
		sender.sendMessage("--- START ---");
		sender.sendMessage("Bukkit Version: " + Bukkit.getBukkitVersion());
		sender.sendMessage("Bukkit Version: " + Bukkit.getServer().getVersion());
		List<BukkitWorker> workers = Bukkit.getScheduler().getActiveWorkers();
		sender.sendMessage("Active Workers: "+workers.size());
		
		for ( BukkitWorker bukkitWorker : workers ) {
			sender.sendMessage("  workerOwner: "+bukkitWorker.getOwner()+" taskId="+bukkitWorker.getTaskId()
				+", "+bukkitWorker.getThread().getName());			
		}
		sender.sendMessage("Permissions: " + FactionsTeleportControl.permission.getClass().getName());
		if (null != fme) {
			}
		}
		
	
		
		
	}
