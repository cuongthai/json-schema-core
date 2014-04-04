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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;

public final class JsonUtils
{
    private static final ObjectMapper MAPPER = JacksonUtils.newMapper();
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private JsonUtils()
    {
    }

    public static JsonNode toJson(final Object o)
    {
        if (o instanceof JsonSerializable)
            return MAPPER.valueToTree(o);
        return FACTORY.objectNode()
            .put("javaClass", o.getClass().getCanonicalName());
    }
}
