/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Function;
import org.seedstack.shed.cache.Cache;
import org.seedstack.shed.cache.CacheFactory;
import org.seedstack.shed.cache.CacheParameters;

public class GuavaCache<K, V> implements Cache<K, V> {
    private final LoadingCache<K, V> cache;

    public static class Factory implements CacheFactory {
        @Override
        public <K, V> Cache<K, V> createCache(CacheParameters<K, V> cacheParameters) {
            return new GuavaCache<>(cacheParameters);
        }
    }

    private GuavaCache(CacheParameters<K, V> cacheParameters) {
        final Function<? super K, ? extends V> loadingFunction = cacheParameters.getLoadingFunction();
        cache = CacheBuilder.newBuilder()
                .initialCapacity(cacheParameters.getInitialSize())
                .maximumSize(cacheParameters.getMaxSize())
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K key) throws Exception {
                        return loadingFunction.apply(key);
                    }
                });
    }

    @Override
    public V get(K key) {
        return cache.getUnchecked(key);
    }
}
