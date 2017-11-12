/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class UnboundedCache<K, V> implements Cache<K, V> {
    private final ConcurrentMap<K, V> map;
    private final Function<? super K, ? extends V> loadingFunction;

    private UnboundedCache(CacheParameters<K, V> cacheParameters) {
        loadingFunction = cacheParameters.getLoadingFunction();
        this.map = new ConcurrentHashMap<>(cacheParameters.getInitialSize(), 0.75f,
                1);
    }

    @Override
    public V get(K key) {
        return map.computeIfAbsent(key, loadingFunction);
    }

    public static class Factory implements CacheFactory {
        @Override
        public <K, V> Cache<K, V> createCache(CacheParameters<K, V> cacheParameters) {
            return new UnboundedCache<>(cacheParameters);
        }
    }
}
