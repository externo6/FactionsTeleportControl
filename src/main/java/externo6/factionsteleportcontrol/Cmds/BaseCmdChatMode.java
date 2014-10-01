package externo6.factionsteleportcontrol.Cmds;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

import externo6.factionsteleportcontrol.FactionsTeleportControlPlugin;
import externo6.factionsteleportcontrol.FactionsBridge.Bridge;
import externo6.factionsteleportcontrol.FactionsBridge.FactionsAny;


public abstract class BaseCmdChatMode extends FTCCommand {
	protected final FactionsAny.ChatMode cMode;
	@SuppressWarnings("unused")
	private final String strPermission;

	protected BaseCmdChatMode( FactionsAny.ChatMode cm, String perm, String... arrayOfAliases) {
		cMode=cm;
		if (0 >= arrayOfAliases.length) {
			FactionsTeleportControlPlugin.bailOut( "coder forgot to add at least one alias for a command "+this.getClass() );
		}
		
		for ( int i = 0; i < arrayOfAliases.length; i++ ) {
			this.aliases.add(arrayOfAliases[i]);
		}
		
		this.permission = Permission.HELP.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		
		this.setHelpShort("set your chat to "+cMode.getDescription());
		
		strPermission=perm;
	}
	
	public final void changeChatMode(FPlayer forWhatPlayer, FactionsAny.ChatMode chatMode) {
//		if ( ! Conf.factionOnlyChat )
//		{
//			msg("<b>The built in chat chat channels are disabled on this server.");
//			return;
//		}
		
		FactionsAny.ChatMode from=Bridge.factions.setChatMode(forWhatPlayer, chatMode );
		//a null here means, we're using 1.7 and setChatMode is not supported
		if ( null!=from ) {
			// fme.setChatMode(com.massivecraft.factions.struct.ChatMode.FACTION);
			externo6.factionsteleportcontrol.FactionsBridge.FactionsAny.ChatMode modeNow = Bridge.factions.getChatMode(forWhatPlayer);
			
			fme.msg( "Your chat mode "+
					(modeNow.equals( from )?"is still on:"
						:"has been changed from "+from.getDescription()+" to")
				+" <i>"+modeNow.getDescription()+"." );
		} else {
			fme.msg( "This version of Factions does not support changing chat mode." );
		}
	}


}
