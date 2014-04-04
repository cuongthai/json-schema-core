/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.misc.key;

import com.github.fge.jsonschema.core.ref.JsonRef;

import java.util.concurrent.atomic.AtomicLong;

public abstract class SchemaKey
{
    private static final AtomicLong ANONYMOUS_ID = new AtomicLong(0L);

    public static SchemaKey anonymousKey()
    {
        return new AnonymousSchemaKey(ANONYMOUS_ID.getAndIncrement());
    }

    public static SchemaKey absoluteKey(final JsonRef ref)
    {
        return new AbsoluteSchemaKey(ref);
    }

    public abstract JsonRef getLocator();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(final Object obj);

    @Override
    public abstract String toString();
}
