package externo6.factionsteleportcontrol.config.sections;

import externo6.factionsteleportcontrol.config.Section;



public class SubSection_TPIntoAlly {
	
	
	@Section(realAlias_neverDotted="deny")
	public final SubSection_TeleportsIntoAllyDeny _deny=new SubSection_TeleportsIntoAllyDeny();
	
	
	
	@Section(realAlias_neverDotted="reportOnConsole")
	public final SubSection_TeleportsIntoAllyReport _report=new SubSection_TeleportsIntoAllyReport();



	public final boolean isAnySet() {
		return _deny.isAnySet() || _report.isAnySet();
	}



	public final boolean shouldReportCommands() {
		return _report.shouldReportCommands();
	}



	public final boolean shouldPreventHomeTelepors() {
		return _deny.shouldPreventHomeTelepors();
	}
	
	public final boolean shouldPreventBackTelepors() {
		return _deny.shouldPreventBackTelepors();
	}


	public final  boolean shouldPreventEnderPearlsTeleports() {
		return _deny.shouldPreventEnderPearlsTeleports();
	}
}
