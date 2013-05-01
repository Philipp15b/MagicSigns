package de.philworld.bukkit.magicsigns.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Base for settings.
 * 
 * Annotation for classes that extend {@link AnnotationConfiguration}.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SettingBase {
	String value();
}
