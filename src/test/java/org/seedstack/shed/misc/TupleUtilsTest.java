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
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class TupleUtilsTest {
    @Test
    public void createUnit() throws Exception {
        assertTuple(buildList(1), Unit.class);
    }

    @Test
    public void createPair() throws Exception {
        assertTuple(buildList(2), Pair.class);
    }

    @Test
    public void createTriplet() throws Exception {
        assertTuple(buildList(3), Triplet.class);
    }

    @Test
    public void createQuartet() throws Exception {
        assertTuple(buildList(4), Quartet.class);
    }

    @Test
    public void createQuintet() throws Exception {
        assertTuple(buildList(5), Quintet.class);
    }

    @Test
    public void createSextet() throws Exception {
        assertTuple(buildList(6), Sextet.class);
    }

    @Test
    public void createSeptet() throws Exception {
        assertTuple(buildList(7), Septet.class);
    }

    @Test
    public void createOctet() throws Exception {
        assertTuple(buildList(8), Octet.class);
    }

    @Test
    public void createEnnead() throws Exception {
        assertTuple(buildList(9), Ennead.class);
    }

    @Test
    public void createDecade() throws Exception {
        assertTuple(buildList(10), Decade.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTooBigFromCollection() throws Exception {
        TupleUtils.createTupleFromCollection(buildList(11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTooBigFromArray() throws Exception {
        TupleUtils.createTupleFromArray(buildList(11).toArray());
    }

    private void assertTuple(List<Object> objects, Class<? extends Tuple> tupleClass) {
        Tuple tuple = TupleUtils.createTupleFromCollection(objects);
        assertThat((Object) tuple).isInstanceOf(tupleClass);
        assertThat(tuple.iterator()).containsExactlyElementsOf(objects);
        assertThat((Object) tuple).isEqualTo(TupleUtils.createTupleFromArray(objects.toArray()));
    }

    private List<Object> buildList(int size) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            objects.add(new Object());
        }
        return objects;
    }
}
