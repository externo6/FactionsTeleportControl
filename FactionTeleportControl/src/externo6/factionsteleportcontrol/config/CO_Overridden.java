package externo6.factionsteleportcontrol.config;

import externo6.factionsteleportcontrol.config.yaml.WYIdentifier;



public class CO_Overridden extends COMetadata {
	
	public static final String	commentPrefixForOVERRIDDENones	= "OVERRIDDEN by %s at line %d #";
	
	
	
	public WYIdentifier<COMetadata>		lostOne;
	public String				dottedLostOne;
	public WYIdentifier<?>		overriddenByThis;
	public String				dottedOverriddenByThis;
	
	
	public CO_Overridden( WYIdentifier<COMetadata> wid, String dottedWID, WYIdentifier<?> _overriddenByThis,
		String _dottedOverriddenByThis )
	{
		assert null != wid;
		assert Typeo.isValidAliasFormat( dottedWID );
		assert null != _overriddenByThis;
		assert Typeo.isValidAliasFormat( _dottedOverriddenByThis );
		lostOne = wid;
		dottedLostOne = dottedWID;
		overriddenByThis = _overriddenByThis;
		dottedOverriddenByThis = _dottedOverriddenByThis;
	}
	
	
	
}
