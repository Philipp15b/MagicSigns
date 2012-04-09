package de.philworld.bukkit.magicsigns;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MagicSignInfo {

	String friendlyName();

	String description();

	String buildPerm();

	String usePerm();

}
