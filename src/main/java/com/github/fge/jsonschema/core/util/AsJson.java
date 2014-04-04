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

package com.github.fge.jsonschema.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.github.fge.jsonschema.core.tree.SchemaTree;

/**
 * Interface implemented by classes having a JSON representation
 *
 * <p>This representation needs not be complete. For instance, {@link
 * SchemaTree} implements it to provide an object with the summary of its main
 * characteristics (loading URI, current pointer).</p>
 */
public interface AsJson
    extends JsonSerializable
{
    /**
     * Return a JSON representation of this object
     *
     * @return a {@link JsonNode}
     * @deprecated implement {@link JsonSerializable} instead
     */
    @Deprecated
    JsonNode asJson();
}
