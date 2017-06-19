/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import org.seedstack.shed.internal.ShedErrorCode;
import org.seedstack.shed.internal.ShedException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ReflectUtils {
    private ReflectUtils() {
        // no instantiation allowed
    }

    public static <T extends AccessibleObject> T makeAccessible(T accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            accessibleObject.setAccessible(true);
            return null;
        });
        return accessibleObject;
    }

    public static Object invoke(Method method, Object self, Object... args) {
        try {
            return method.invoke(self, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_INVOKE_METHOD)
                    .put("method", method.toGenericString());
        }
    }

    public static void setValue(Field field, Object self, Object value) {
        try {
            field.set(self, value);
        } catch (IllegalAccessException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_SET_FIELD)
                    .put("field", field.toGenericString());
        }
    }

    public static Object getValue(Field field, Object self) {
        try {
            return field.get(self);
        } catch (IllegalAccessException e) {
            throw ShedException.wrap(e, ShedErrorCode.UNABLE_TO_GET_FIELD)
                    .put("field", field.toGenericString());
        }
    }
}
