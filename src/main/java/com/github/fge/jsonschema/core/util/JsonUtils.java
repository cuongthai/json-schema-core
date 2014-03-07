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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import javax.annotation.Nullable;
import java.lang.NullPointerException;

/**
 * Utility methods to represent objects as {@link JsonNode}s
 *
 * @since 1.1.10
 */
public final class JsonUtils
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);
    private static final ObjectMapper MAPPER = JacksonUtils.newMapper();
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private JsonUtils()
    {
    }

    /**
     * Serialize an object to a {@link JsonNode}, if possible
     *
     * <p>If the argument implements {@link JsonSerializable}, then this
     * method returns the JSON representation of the object. Otherwise, it
     * returns a JSON object as follows:</p>
     *
     * <pre>
     *     {
     *         "javaClass": "foo.bar.ClassName"
     *     }
     * </pre>
     *
     * <p>where {@code foo.bar.ClassName} is the object's fully qualified class
     * name (as returned by {@code o.getClass().getCanonicalName()}).</p>
     *
     * <p>If the argument is null, returns a JSON null.</p>
     *
     * @param o the object
     * @return see description
     */
    public static JsonNode toJson(@Nullable final Object o)
    {
        if (o == null)
            return NullNode.getInstance();
        return o instanceof JsonSerializable ? MAPPER.valueToTree(o)
            : FACTORY.objectNode().put("javaClass", o.getClass()
                .getCanonicalName());
    }

    /**
     * Get a JSON String representation of an object
     *
     * <p>This simply returns a {@link TextNode} out of the argument's {@code
     * toString()} implementation.</p>
     *
     * @param o the object
     * @return a JSON String
     * @throws NullPointerException argument is null
     */
    public static JsonNode toString(final Object o)
    {
        BUNDLE.checkNotNull(o, "jsonUtils.nullArgument");
        return FACTORY.textNode(o.toString());
    }
}
