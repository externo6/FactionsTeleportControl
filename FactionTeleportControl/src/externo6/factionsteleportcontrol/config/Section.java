package externo6.factionsteleportcontrol.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( {
	ElementType.FIELD
} )
public @interface Section {

	String[] comments() default {};

	String realAlias_neverDotted();
}
