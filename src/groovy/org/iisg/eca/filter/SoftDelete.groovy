package org.iisg.eca.filter

import java.lang.annotation.Target
import java.lang.annotation.Retention
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * Used on any domain class that can be soft deleted
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface SoftDelete {

    /**
     * Name of the column that indicates whether the instance is soft deleted
     *
     * @return The name of the soft-delete column
     */
    abstract String value() default 'deleted'
}