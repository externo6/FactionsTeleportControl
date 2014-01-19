package externo6.factionsteleportcontrol.Cmds;

import org.bukkit.ChatColor;

import com.massivecraft.factions.cmd.FCommand;

import externo6.factionsteleportcontrol.FactionsTeleportControl;


public abstract class FTCCommand extends FCommand{

	protected abstract void performfp();
	
	@Override
	public final void perform() {//XXX: final to avoid overriding the wrong one
		if (!FactionsTeleportControl.instance.isEnabled()) {
			sender.sendMessage( ChatColor.RED+"This command is unavailable while FTC is not enabled." );
			return;
		}
		performfp();
	}
	
}
