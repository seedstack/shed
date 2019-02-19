/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.cache;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.seedstack.shed.reflect.Classes;

@FunctionalInterface
public interface Cache<K, V> {
    static <K, V> Cache<K, V> create(Function<? super K, ? extends V> loadingFunction) {
        CacheParameters<K, V> cacheParameters = new CacheParameters<>();
        cacheParameters.setLoadingFunction(loadingFunction);
        return Factories.DEFAULT.createCache(cacheParameters);
    }

    static <K, V> Cache<K, V> create(CacheParameters<K, V> cacheParameters) {
        return Factories.DEFAULT.createCache(cacheParameters);
    }

    static <K, V> Cache<K, V> create(CacheParameters<K, V> cacheParameters,
            Class<? extends CacheFactory> factoryClass) {
        return Factories.FACTORIES.computeIfAbsent(factoryClass, Classes::instantiateDefault)
                .createCache(cacheParameters);
    }

    V get(K key);

    class Factories {
        private static final CacheFactory DEFAULT = resolveDefaultCacheFactory();
        private static final ConcurrentMap<Class<? extends CacheFactory>, CacheFactory> FACTORIES = new
                ConcurrentHashMap<>();

        private static CacheFactory resolveDefaultCacheFactory() {
            Iterator<CacheFactory> cacheFactoryIterator = ServiceLoader.load(CacheFactory.class).iterator();
            if (cacheFactoryIterator.hasNext()) {
                return cacheFactoryIterator.next();
            } else {
                return new SimpleLRUCache.Factory();
            }
        }
    }
}
