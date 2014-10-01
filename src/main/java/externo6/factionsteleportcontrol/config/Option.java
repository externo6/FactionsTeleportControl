package externo6.factionsteleportcontrol.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( {
	ElementType.FIELD
} )
public @interface Option {

	String[] autoComment() default {};
	
	String realAlias_inNonDottedFormat();

	String[] oldAliases_alwaysDotted() default {};
	
}
