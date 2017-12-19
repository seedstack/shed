/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.seedstack.shed.internal.ShedErrorCode;
import org.seedstack.shed.internal.ShedException;

public final class ReflectUtils {
    private ReflectUtils() {
        // no instantiation allowed
    }

    /**
     * Makes an {@link AccessibleObject} accessible by invoking
     * {@link AccessibleObject#setAccessible(boolean)}
     * in a {@link PrivilegedAction}.
     *
     * @param accessibleObject the object to make accessible.
     * @param <T>              the type of object.
     * @return the object made accessible.
     */
    public static <T extends AccessibleObject> T makeAccessible(T accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            accessibleObject.setAccessible(true);
            return null;
        });
        return accessibleObject;
    }

    /**
     * Invokes the specified method while wrapping checked exception in a {@link ShedException}.
     *
     * @param method the method.
     * @param self   the instance to invoke the method on.
     * @param args   the method arguments.
     * @param <T>    the type of the return value.
     * @return the method returned value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object self, Object... args) {
        try {
            return (T) method.invoke(self, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_INVOKE_METHOD)
                    .put("method", method.toGenericString());
        }
    }

    /**
     * Sets the specified value into the specified field while wrapping checked exception in a {@link
     * ShedException}.
     *
     * @param field the field.
     * @param self  the instance to set the field on.
     * @param value the value to set.
     */
    public static void setValue(Field field, Object self, Object value) {
        try {
            field.set(self, value);
        } catch (IllegalAccessException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_SET_FIELD)
                    .put("field", field.toGenericString());
        }
    }

    /**
     * Sets the specified field value while wrapping checked exception in a {@link ShedException}.
     *
     * @param field the field.
     * @param self  the instance to get the value from.
     * @param <T>   the type of the field value.
     * @return the field value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Field field, Object self) {
        try {
            return (T) field.get(self);
        } catch (IllegalAccessException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_GET_FIELD)
                    .put("field", field.toGenericString());
        }
    }
}
