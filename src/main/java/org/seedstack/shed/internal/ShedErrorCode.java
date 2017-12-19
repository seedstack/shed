/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.shed.internal;

import org.seedstack.shed.exception.ErrorCode;

public enum ShedErrorCode implements ErrorCode {
    UNABLE_TO_SET_FIELD,
    UNABLE_TO_GET_FIELD,
    UNABLE_TO_INSTANTIATE_CLASS,
    UNABLE_TO_INVOKE_METHOD
}
