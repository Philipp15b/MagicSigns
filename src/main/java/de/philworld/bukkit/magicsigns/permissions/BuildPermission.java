package de.philworld.bukkit.magicsigns.permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BuildPermission {
	String value();
}
