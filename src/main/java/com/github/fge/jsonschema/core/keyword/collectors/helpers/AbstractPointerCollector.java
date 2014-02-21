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

package com.github.fge.jsonschema.core.keyword.collectors.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.jsonschema.core.tree.SchemaTree;

public abstract class AbstractPointerCollector
    implements PointerCollector
{
    protected final String keyword;
    protected final JsonPointer basePointer;

    protected AbstractPointerCollector(final String keyword)
    {
        this.keyword = keyword;
        basePointer = JsonPointer.of(keyword);
    }

    protected final JsonNode getNode(final SchemaTree tree)
    {
        return tree.getNode().get(keyword);
    }
}
