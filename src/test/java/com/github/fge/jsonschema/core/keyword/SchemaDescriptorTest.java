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

package com.github.fge.jsonschema.core.keyword;

import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.schema.SchemaDescriptorBuilder;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaDescriptorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);
    private static final String K1 = "k1";
    private static final String K2 = "k2";

    private SchemaDescriptorBuilder builder;
    private SyntaxChecker checker1;
    private SyntaxChecker checker2;
    private PointerCollector collector1;
    private PointerCollector collector2;

    @BeforeMethod
    public void init()
    {
        builder = SchemaDescriptor.newBuilder();
        checker1 = mock(SyntaxChecker.class);
        checker2 = mock(SyntaxChecker.class);
        collector1 = mock(PointerCollector.class);
        collector2 = mock(PointerCollector.class);
    }

    @Test
    public void cannotSetNullLocator()
    {
        try {
            builder.setLocator(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("schemaDescriptor.nullLocator"));
        }
    }

    @Test
    public void cannotAddNullKeyword()
    {
        try {
            builder.addKeyword(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("schemaDescriptor.nullDescriptor"));
        }
    }

    @Test
    public void enteredInformationIsRetained()
    {
        final URI locator = URI.create("foo://bar#");
        final Keyword foo = Keyword.withName("foo").build();
        final Keyword k1 = Keyword.withName(K1)
            .setPointerCollector(collector1).setSyntaxChecker(checker1).build();
        final Keyword k2 = Keyword.withName(K2)
            .setPointerCollector(collector2).setSyntaxChecker(checker2).build();

        final SchemaDescriptor descriptor = builder.setLocator(locator)
            .addKeyword(foo).addKeyword(k1).addKeyword(k2).freeze();

        assertEquals(descriptor.getSupportedKeywords(),
            ImmutableSet.of("foo", K1, K2));

        assertEquals(descriptor.getPointerCollectors(),
            ImmutableMap.of(K1, collector1, K2, collector2));


        assertEquals(descriptor.getSyntaxCheckers(),
            ImmutableMap.of(K1, checker1, K2, checker2));

        final SchemaDescriptor descriptor2 = descriptor.thaw()
            .removeKeyword("foo").freeze();

        assertEquals(descriptor2.getSupportedKeywords(),
            ImmutableSet.of(K1, K2));
    }
}
