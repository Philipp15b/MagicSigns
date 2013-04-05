package de.philworld.bukkit.magicsigns;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MagicSignInfo {

	String name();

	String friendlyName();

	String description();

	String buildPerm();

	String usePerm();

}
