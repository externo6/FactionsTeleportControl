package externo6.factionsteleportcontrol.config;

import java.lang.reflect.Field;

import externo6.factionsteleportcontrol.config.yaml.WYIdentifier;



public class FailedToSetConfigValueException extends RuntimeException {
	
	public FailedToSetConfigValueException( WYIdentifier<COMetadata> wID, Field field, Throwable t ) {
		
		super( "----------\nUnable to set the value `"+wID.getValue()+"` for config option `" + wID.getID_InAbsoluteDottedForm( Config.virtualRoot )
			+ "` at line `"+wID.getLineNumber()+"` for unknown reasons", t );
	}
	
}
