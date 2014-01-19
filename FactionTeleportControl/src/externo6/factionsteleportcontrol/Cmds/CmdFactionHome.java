package externo6.factionsteleportcontrol.Cmds;

import externo6.factionsteleportcontrol.FactionsTeleportControl;
import externo6.factionsteleportcontrol.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdFactionHome extends FTCCommand {
	Factions factions;
	FPlayers fplayers;
	Faction faction;
	
	public CmdFactionHome() {
		this.aliases.add("factionhome");
		
		this.requiredArgs.add("tag");
		
		this.permission = Permission.HELP.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		
		this.setHelpShort("teleport to another Factions home");
	}
	
	@Override
	public void performfp() {
		String factionName = this.argAsString(0).toString();
		Faction currentF = Factions.i.getByTag(factionName);
		
		Player player = Utilities.getOnlinePlayerExact(fme);
		if(FactionsTeleportControl.permission.has(player, "factionsteleportcontrol.otherfactionshome")) {
			if(currentF == null) {
				player.sendMessage(ChatColor.GOLD + "[FTC]" + ChatColor.RED + "Faction was not found!");
			} else {
				if(currentF.hasHome()) {
					Location FactionHome = currentF.getHome();
					player.teleport(FactionHome);
					player.sendMessage(ChatColor.GOLD + "[FTC]" + ChatColor.GREEN + "You have been teleported to the Faction home of " + ChatColor.RED + factionName);
				} else {
					player.sendMessage(ChatColor.GOLD +"[FTC]" + ChatColor.RED + "That faction doesn't have a home!");
				}
			}
		}else {
			sendMessage(ChatColor.GOLD + "[FTC]" + ChatColor.RED+"No permission to use this command!" );
		}
	}
}
