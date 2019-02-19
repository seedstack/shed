/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.cache;

import java.util.function.Function;

public class FakeCache<K, V> implements Cache<K, V> {
    private final Function<? super K, ? extends V> loadingFunction;

    private FakeCache(CacheParameters<K, V> cacheParameters) {
        loadingFunction = cacheParameters.getLoadingFunction();
    }

    @Override
    public V get(K key) {
        return loadingFunction.apply(key);
    }

    public static class Factory implements CacheFactory {
        @Override
        public <K, V> Cache<K, V> createCache(CacheParameters<K, V> cacheParameters) {
            return new FakeCache<>(cacheParameters);
        }
    }
}
