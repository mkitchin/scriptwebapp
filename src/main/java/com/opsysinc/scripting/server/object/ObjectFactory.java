package com.opsysinc.scripting.server.object;

import org.omg.CORBA.Any;

/**
 * Simple object factory interface.
 *
 * @author mkit
 */
public interface ObjectFactory<T> {

    /**
     * Basic create method.
     *
     * @param args Ctor arguments.
     * @return Created object.
     * @throws Any exception (caller handles).
     */
    T create(Object... args) throws Throwable;
}
