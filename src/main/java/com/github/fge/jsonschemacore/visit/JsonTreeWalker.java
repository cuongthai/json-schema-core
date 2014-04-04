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

package com.github.fge.jsonschemacore.visit;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.fge.jsonschemacore.visit.JsonTreeVisitResult.*;

public final class JsonTreeWalker<T>
{
    private JsonTree tree;
    private final JsonTreeVisitor<T> visitor;

    private final ValidationMessage message = new ValidationMessage();

    public JsonTreeWalker(final JsonTree tree, final JsonTreeVisitor<T> visitor)
    {
        this.tree = tree;
        this.visitor = visitor;
    }

    public void walk()
        throws JsonSchemaException
    {
        final JsonPointer ptr = tree.getPointer();
        final JsonNode node = tree.getNode();
        doWalk(ptr, node, message);
    }

    private JsonTreeVisitResult doWalk(final JsonPointer ptr,
        final JsonNode node, final ValidationMessage message)
        throws JsonSchemaException
    {
        if (node.isMissingNode())
            throw new IllegalArgumentException("tried to visit a missing node");
        final JsonTreeVisitResult result
            = visitor.visitNode(ptr, node, message);

        if (result != CONTINUE)
            return result;
        if (!node.isContainerNode())
            return result;

        return node.isObject() ? walkObject(ptr, node) : walkArray(ptr, node);
    }

    private JsonTreeVisitResult walkObject(final JsonPointer ptr,
        final JsonNode node)
        throws JsonSchemaException
    {
        final Set<JsonPointer> pointers = Sets.newLinkedHashSet();

        JsonTreeVisitResult result;

        result = visitor.preVisitObject(ptr, node, pointers, message);

        if (result != CONTINUE)
            return result;
        if (pointers.isEmpty())
            return result;

        JsonPointer childPointer;
        JsonNode childNode;

        for (final JsonPointer pointer: pointers) {
            childPointer = ptr.append(pointer);
            childNode = pointer.get(node);
            result = doWalk(childPointer, childNode, message);
            if (result != CONTINUE)
                return result;
        }

        return visitor.postVisitObject(ptr, message);
    }

    private JsonTreeVisitResult walkArray(final JsonPointer ptr,
        final JsonNode node)
        throws JsonSchemaException
    {
        final Set<JsonPointer> pointers = Sets.newLinkedHashSet();

        JsonTreeVisitResult result;

        result = visitor.preVisitArray(ptr, node, pointers, message);

        if (result != CONTINUE)
            return result;
        if (pointers.isEmpty())
            return result;

        JsonPointer childPointer;
        JsonNode childNode;

        for (final JsonPointer pointer: pointers) {
            childPointer = ptr.append(pointer);
            childNode = pointer.get(node);
            result = doWalk(childPointer, childNode, message);
            if (result != CONTINUE)
                return result;
        }

        return visitor.postVisitArray(ptr, message);
    }
}
