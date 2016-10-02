/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.misc;

import org.javatuples.Decade;
import org.javatuples.Ennead;
import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Septet;
import org.javatuples.Sextet;
import org.javatuples.Triplet;
import org.javatuples.Tuple;
import org.javatuples.Unit;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Tuple utilities.
 */
public final class TupleUtils {
    private TupleUtils() {
    }

    /**
     * Create a Tuple from an object array.
     *
     * @param objects the array of objects.
     * @param <T>     the tuple type.
     * @return the tuple.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Tuple> T createTupleFromArray(Object[] objects) {
        try {
            return (T) getTupleClass(objects.length).getMethod("fromArray", Object[].class).invoke(null, (Object) objects);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create tuple", e);
        }
    }

    /**
     * Create a Tuple from an object collection.
     *
     * @param objects the list of objects.
     * @param <T>     the tuple type.
     * @return the tuple.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Tuple> T createTupleFromCollection(Collection<?> objects) {
        try {
            return (T) getTupleClass(objects.size()).getMethod("fromCollection", Collection.class).invoke(null, objects);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create tuple", e);
        }
    }

    private static Class<? extends Tuple> getTupleClass(int size) {
        switch (size) {
            case 1:
                return Unit.class;
            case 2:
                return Pair.class;
            case 3:
                return Triplet.class;
            case 4:
                return Quartet.class;
            case 5:
                return Quintet.class;
            case 6:
                return Sextet.class;
            case 7:
                return Septet.class;
            case 8:
                return Octet.class;
            case 9:
                return Ennead.class;
            case 10:
                return Decade.class;
            default:
                throw new IllegalArgumentException("Cannot create tuple: unsupported size " + size);
        }
    }
}
