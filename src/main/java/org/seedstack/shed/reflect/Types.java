/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public final class Types {
    private Types() {
        // no instantiation allowed
    }

    public static String simpleNameOf(Type type) {
        return buildTypeName(type, new StringBuilder(), true, false).toString();
    }

    public static String canonicalNameOf(Type type) {
        return buildTypeName(type, new StringBuilder(), false, true).toString();
    }

    public static String nameOf(Type type) {
        return buildTypeName(type, new StringBuilder(), false, false).toString();
    }

    public static Class<?> rawClassOf(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(rawClassOf(componentType), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else {
            throw new IllegalArgumentException("Unsupported type " + type.getTypeName());
        }
    }

    private static StringBuilder buildTypeName(Type type, StringBuilder sb, boolean simpleName, boolean canonicalName) {
        if (type instanceof ParameterizedType) {
            buildTypeName(((ParameterizedType) type).getRawType(), sb, simpleName, canonicalName);
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            sb.append("<");
            buildGenericTypeNames(actualTypeArguments, sb, simpleName, canonicalName);
            sb.append(">");
        } else if (type instanceof Class) {
            sb.append(simpleName ? ((Class) type).getSimpleName() : (canonicalName ? ((Class) type).getCanonicalName() : ((Class) type).getName()));
        } else if (type instanceof WildcardType) {
            sb.append("?");
            Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (lowerBounds.length > 0) {
                sb.append(" super ");
                buildGenericTypeNames(lowerBounds, sb, simpleName, canonicalName);
            } else if (upperBounds.length > 0) {
                if (upperBounds.length > 1 || !upperBounds[0].equals(Object.class)) {
                    sb.append(" extends ");
                    buildGenericTypeNames(upperBounds, sb, simpleName, canonicalName);
                }
            }
        }
        return sb;
    }

    private static void buildGenericTypeNames(Type[] actualTypeArguments, StringBuilder sb, boolean simpleName, boolean canonicalName) {
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type typeArgument = actualTypeArguments[i];
            buildTypeName(typeArgument, sb, simpleName, canonicalName);
            if (i < actualTypeArguments.length - 1) {
                sb.append(", ");
            }
        }
    }
}
