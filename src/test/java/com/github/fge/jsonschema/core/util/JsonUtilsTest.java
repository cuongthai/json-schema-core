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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class JsonUtilsTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String HELLO = "Hello world";
    private static final String NOSER
        = NoSerialization.class.getCanonicalName();
    private static final String NOSER_TOSTRING = "I am no beauty, I know";

    @Test
    public void toJsonWithSerializationReturnsSerializedForm()
    {
        final JsonNode actual = JsonUtils.toJson(new WithSerialization());
        assertTrue(actual.equals(FACTORY.textNode(HELLO)));
    }

    @Test
    public void toJsonWithoutSerializationReturnsClassname()
    {
        final JsonNode expected = FACTORY.objectNode()
            .put("javaClass", NOSER);
        final JsonNode actual = JsonUtils.toJson(new NoSerialization());
        assertTrue(actual.equals(expected));
    }

    @Test
    public void toJsonWithNullArgumentReturnsJsonNull()
    {
        assertTrue(JsonUtils.toJson(null).equals(NullNode.getInstance()));
    }

    @Test
    public void toStringWithNullArgumentThrowsNPEWithCorrectMessage()
    {
        try {
            JsonUtils.toString(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("jsonUtils.nullArgument"));
        }
    }

    @Test
    public void toStringWithNonNullArgumentReturnsObjectsToString()
    {
        final JsonNode actual = JsonUtils.toString(new NoSerialization());
        final JsonNode expected = FACTORY.textNode(NOSER_TOSTRING);
        assertTrue(actual.equals(expected));
    }

    private static final class WithSerialization
        implements JsonSerializable
    {
        @Override
        public void serialize(final JsonGenerator jgen,
            final SerializerProvider provider)
            throws IOException, JsonProcessingException
        {
            jgen.writeString(HELLO);
        }

        @Override
        public void serializeWithType(final JsonGenerator jgen,
            final SerializerProvider provider, final TypeSerializer typeSer)
            throws IOException, JsonProcessingException
        {
            serialize(jgen, provider);
        }
    }

    private static final class NoSerialization
    {
        @Override
        public String toString()
        {
            return NOSER_TOSTRING;
        }
    }
}
