/*
 * Copyright © 2013-2017, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.internal;

import org.seedstack.shed.exception.BaseException;
import org.seedstack.shed.exception.ErrorCode;

/**
 * This is the class for all Shed exceptions.
 */
public class ShedException extends BaseException {
    protected ShedException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ShedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * Create a new SeedException from an {@link ErrorCode}.
     *
     * @param errorCode the error code to set.
     * @return the created SeedException.
     */
    public static ShedException createNew(ErrorCode errorCode) {
        return new ShedException(errorCode);
    }

    /**
     * Wrap a SeedException with an {@link ErrorCode} around an existing {@link Throwable}.
     *
     * @param throwable the existing throwable to wrap.
     * @param errorCode the error code to set.
     * @return the created SeedException.
     */
    public static ShedException wrap(Throwable throwable, ErrorCode errorCode) {
        return new ShedException(errorCode, throwable);
    }
}
