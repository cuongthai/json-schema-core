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
import com.google.common.primitives.Longs;

public final class AnonymousSchemaKey
    extends SchemaKey
{
    private final long id;

    AnonymousSchemaKey(final long id)
    {
        this.id = id;
    }

    @Override
    public JsonRef getLocator()
    {
        return JsonRef.emptyRef();
    }

    @Override
    public int hashCode()
    {
        return Longs.hashCode(id);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final AnonymousSchemaKey other = (AnonymousSchemaKey) obj;
        return id == other.id;
    }

    @Override
    public String toString()
    {
        return "anonymous schema #" + id;
    }
}
