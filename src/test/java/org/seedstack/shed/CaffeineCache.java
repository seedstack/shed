/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.seedstack.shed.cache.Cache;
import org.seedstack.shed.cache.CacheFactory;
import org.seedstack.shed.cache.CacheParameters;

public class CaffeineCache<K, V> implements Cache<K, V> {
    private final LoadingCache<K, V> cache;

    public static class Factory implements CacheFactory {
        @Override
        public <K, V> Cache<K, V> createCache(CacheParameters<K, V> cacheParameters) {
            return new CaffeineCache<>(cacheParameters);
        }
    }

    private CaffeineCache(CacheParameters<K, V> cacheParameters) {
        cache = Caffeine.newBuilder()
                .initialCapacity(cacheParameters.getInitialSize())
                .maximumSize(cacheParameters.getMaxSize())
                .build(cacheParameters.getLoadingFunction()::apply);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }
}
