package externo6.factionsteleportcontrol.config.sections;

import externo6.factionsteleportcontrol.config.Option;
import externo6.factionsteleportcontrol.config.Section;


public final class Section_Extras {
	//XXX: I may use the terms ID, key, alias, config option   interchangeably to mean the same thing.
	
	@Option(oldAliases_alwaysDotted={
		"disableUpdateCheck"
	}, realAlias_inNonDottedFormat = "disableUpdateCheck" )
	public  final _boolean disableUpdateCheck=new _boolean(false);
	
@Section(realAlias_neverDotted="Protection")
public final SubSection_Protection _protection=new SubSection_Protection();}