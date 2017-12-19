/*
 * Copyright © 2013-2018, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.shed.exception;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseExceptionTest {
    @Test(expected = TotoException.class)
    public void createNewTotoException1() {
        throw BaseException.createNew(TotoException.class, TotoErrorCode.CARAMBAR_MODE)
                .put("key1", "value1")
                .put("key2", "value2");
    }

    @Test(expected = TotoException.class)
    public void wrap_should_work_fine_with_descendant() {
        try {
            throw new NullPointerException();
        } catch (Exception exception) {
            throw BaseException.wrap(TotoException.class, exception, TotoErrorCode.JOKE_MODE)
                    .put("Error Code", "this is how we do it !");
        }
    }

    @Test(expected = BaseException.class)
    public void wrap_should_work_fine_with_change_of_error_code() {
        try {
            throw new TotoException(TotoErrorCode.JOKE_MODE);
        } catch (TotoException e) {
            throw BaseException.wrap(TotoException.class, e, TotoErrorCode.CARAMBAR_MODE);
        }
    }

    @Test(expected = BaseException.class)
    public void wrap_should_work_fine() {
        try {
            throw new TotoException(TotoErrorCode.JOKE_MODE);
        } catch (TotoException exception) {
            throw BaseException.wrap(TotoException.class, exception, TotoErrorCode.JOKE_MODE)
                    .put("Error Code", "this is how we do it !");
        }
    }

    @Test
    public void multiple_causes_should_be_visible() throws Exception {
        StringWriter stringWriter = new StringWriter();
        BaseException.wrap(TotoException.class, BaseException.wrap(TotoException.class, new RuntimeException("yop"),
                TotoErrorCode.CARAMBAR_MODE), TotoErrorCode.JOKE_MODE).printStackTrace(new PrintWriter(stringWriter));
        String text = stringWriter.toString();

        assertThat(text).contains("Caused by: java.lang.RuntimeException: yop");
        assertThat(text).contains("Caused by: org.seedstack.shed.exception.BaseExceptionTest$TotoException: [TOTO] " +
                "Carambar mode");
        assertThat(text).contains("org.seedstack.shed.exception.BaseExceptionTest$TotoException: [TOTO] Joke mode");
    }

    @Test
    public void infoShouldBeLoaded() throws Exception {
        BaseException seedException = BaseException.createNew(TotoException.class, TotoErrorCode.CARAMBAR_MODE)
                .put("who", "World")
                .put("tld", "com");
        String text = seedException.toString();
        assertThat(text).contains("Hello World!");
        assertThat(text).contains("Some fix");
        assertThat(text).contains("http://some.url.com");
    }

    private enum TotoErrorCode implements ErrorCode {
        JOKE_MODE, CARAMBAR_MODE;
    }

    private static class TotoException extends BaseException {
        private static final long serialVersionUID = 1L;

        private TotoException(ErrorCode errorCode) {
            super(errorCode);
        }

        private TotoException(ErrorCode errorCode, Throwable throwable) {
            super(errorCode, throwable);
        }
    }
}
