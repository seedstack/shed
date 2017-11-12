/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.cache;

import java.util.function.Function;

public class CacheParameters<K, V> {
    private int initialSize = 256;
    private int maxSize = 1024;
    private int concurrencyLevel = Math.max(1, Runtime.getRuntime().availableProcessors());
    private Function<? super K, ? extends V> loadingFunction;

    public int getInitialSize() {
        return initialSize;
    }

    public CacheParameters<K, V> setInitialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public CacheParameters<K, V> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public void setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }

    public Function<? super K, ? extends V> getLoadingFunction() {
        return loadingFunction;
    }

    public CacheParameters<K, V> setLoadingFunction(Function<? super K, ? extends V> loadingFunction) {
        this.loadingFunction = loadingFunction;
        return this;
    }
}
