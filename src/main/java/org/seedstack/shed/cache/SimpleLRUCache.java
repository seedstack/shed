/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleLRUCache<K, V> implements Cache<K, V> {
    private final LinkedHashMap<K, V> map;
    private final Function<? super K, ? extends V> loadingFunction;

    private SimpleLRUCache(CacheParameters<K, V> cacheParameters) {
        loadingFunction = cacheParameters.getLoadingFunction();
        final int maxSize = cacheParameters.getMaxSize();
        this.map = new LinkedHashMap<K, V>(cacheParameters.getInitialSize(), 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public V get(K key) {
        synchronized (map) {
            return map.computeIfAbsent(key, loadingFunction);
        }
    }

    public static class Factory implements CacheFactory {
        @Override
        public <K, V> Cache<K, V> createCache(CacheParameters<K, V> cacheParameters) {
            return new SimpleLRUCache<>(cacheParameters);
        }
    }
}
