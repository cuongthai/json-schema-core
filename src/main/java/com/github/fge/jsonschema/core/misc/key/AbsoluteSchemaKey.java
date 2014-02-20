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

public final class AbsoluteSchemaKey
    extends SchemaKey
{
    private final JsonRef locator;

    AbsoluteSchemaKey(final JsonRef locator)
    {
        this.locator = locator;
    }

    @Override
    public JsonRef getLocator()
    {
        return locator;
    }

    @Override
    public int hashCode()
    {
        return locator.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final AbsoluteSchemaKey other = (AbsoluteSchemaKey) obj;
        return locator.equals(other.locator);
    }

    @Override
    public String toString()
    {
        return null;
    }
}
