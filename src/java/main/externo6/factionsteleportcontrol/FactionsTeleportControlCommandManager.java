package externo6.factionsteleportcontrol;

import externo6.factionsteleportcontrol.Cmds.CmdFactionHome;
import externo6.factionsteleportcontrol.Cmds.CmdDebug;
import externo6.factionsteleportcontrol.Cmds.CmdReloadFTC;
import externo6.factionsteleportcontrol.FactionsBridge.Bridge;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;

public class FactionsTeleportControlCommandManager {
	FactionsTeleportControlCommandManager FactionsTeleportControlCommandManager;
	
	public static void setup() {
		
		addSC(new CmdFactionHome());
		addSC(new CmdDebug());
		
		addSC(new CmdReloadFTC());
		
		Bridge.factions.finalizeHelp(); 
	}

	private static final void addSC(FCommand subCommand) {
		Bridge.factions.addSubCommand(P.p.cmdBase, subCommand);
	}
	
}
