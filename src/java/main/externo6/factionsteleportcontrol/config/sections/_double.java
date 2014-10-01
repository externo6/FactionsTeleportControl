package externo6.factionsteleportcontrol.config.sections;

import externo6.factionsteleportcontrol.config._Base;


public class _double extends _Base {
	public double _;
	public double _defaultValue;
	
	
	public _double(double defaultValue) {
		_defaultValue=defaultValue;
		_=defaultValue;
	}
	
	@Override
	public void setValue( String value ) {
		_=Double.parseDouble( value );
	}

	@Override
	public String getValue() {
		return Double.toString(_);
	}

	@Override
	public String getDefaultValue() {
		return Double.toString(_defaultValue);
	}
}
