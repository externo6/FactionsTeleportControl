package externo6.factionsteleportcontrol.config.sections;

import externo6.factionsteleportcontrol.config._Base;



public class _int extends _Base {
	
	public int	_;
	public int	_defaultValue;
	
	
	public _int( int defaultValue ) {
		_defaultValue=defaultValue;
		_ = defaultValue;
	}
	
	
	@Override
	public void setValue( String value ) {
		_ = Integer.parseInt( value );
	}


	@Override
	public String getValue() {
		return Integer.toString( _ );
	}


	@Override
	public String getDefaultValue() {
		return Integer.toString( _defaultValue );
	}
}
