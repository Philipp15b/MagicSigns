package de.philworld.bukkit.magicsigns.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a setting in the config on a field. The value contains the path to
 * the setting. If a {@link SettingBase} is exists on the class, all paths are
 * relative to the value defined in {@link SettingBase}.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
	String value();
}
