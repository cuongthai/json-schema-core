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

package com.github.fge.jsonschema.core.report;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.util.AsJson;
import com.github.fge.jsonschema.core.util.JsonUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

/**
 * One processing message
 *
 * <p>Internally, a processing message has a {@link Map} whose keys are strings
 * and values are {@link JsonNode}s. Note that all methods altering the message
 * contents accept {@code null}: in this case, the value for that key will be a
 * {@link NullNode}. If you submit null <i>keys</i>, the whole value will be
 * <b>ignored</b>.</p>
 *
 * <p>Some methods to append contents to a message accept arbitrary inputs: in
 * this case, it is your responsibility to ensure that these inputs have a
 * correct implementation of {@link Object#toString()}.</p>
 *
 * <p>You can use formatted strings as messages using the capabilities of {@link
 * Formatter}; in order to pass arguments to the different placeholders, you
 * will then use the {@code .putArgument()} methods instead of {@code .put()}.
 * Arguments will appear in the order you submit them.</p>
 *
 * <p>Please note that if you do:</p>
 *
 * <pre>
 *     message.setMessage("foo %s").putArgument("something", "here")
 *         .setMessage("another %s message")
 * </pre>
 *
 * <p>then the argument list is <b>cleared</b>.</p>
 *
 * <p>You can alter the behaviour of a processing message in two ways: its log
 * level and its {@link ExceptionProvider} (used in {@link #asException()}.</p>
 *
 * <p>All mutation methods of a message return {@code this}.</p>
 */
@NotThreadSafe
public final class ProcessingMessage
    implements AsJson
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    /**
     * This is where all key/value pairs go
     */
    private final ObjectNode map = FACTORY.objectNode();

    /**
     * Argument list for Formatter
     */
    private final List<Object> args = Lists.newArrayList();

    /**
     * Exception provider
     */
    private ExceptionProvider exceptionProvider
        = SimpleExceptionProvider.getInstance();

    private LogLevel level;

    /**
     * Constructor
     *
     * <p>By default, a message is generated with a log level of {@link
     * LogLevel#INFO}.</p>
     */
    public ProcessingMessage()
    {
        setLogLevel(LogLevel.INFO);
    }

    /**
     * Get the main message
     *
     * @return the main message as a string
     */
    public String getMessage()
    {
        return map.has("message") ? map.get("message").textValue()
            : "(no message)";
    }

    /**
     * Get the log level for this message
     *
     * @return the log level
     */
    public LogLevel getLogLevel()
    {
        return level;
    }

    /**
     * Set the main message
     *
     * @param message the message as a string
     * @return this
     */
    public ProcessingMessage setMessage(final String message)
    {
        args.clear();
        return put("message", message);
    }

    /**
     * Set the log level for this message
     *
     * @param level the log level
     * @return this
     * @throws NullPointerException log level is null
     */
    public ProcessingMessage setLogLevel(final LogLevel level)
    {
        BUNDLE.checkNotNull(level, "processing.nullLevel");
        this.level = level;
        return put("level", level);
    }

    /**
     * Set the exception provider for that particular message
     *
     * @param exceptionProvider the exception provider
     * @return this
     * @throws NullPointerException exception provider is null
     */
    public ProcessingMessage setExceptionProvider(
        final ExceptionProvider exceptionProvider)
    {
        BUNDLE.checkNotNull(exceptionProvider,
            "processing.nullExceptionProvider");
        this.exceptionProvider = exceptionProvider;
        return this;
    }

    /**
     * Add a key/value pair to this message
     *
     * <p>This is the main method. All other put methods call this one.</p>
     *
     * <p>Notes:</p>
     *
     * <ul>
     *     <li>if {@code key} is {@code null}, the key/value pair will be
     *     discarded silently;</li>
     *     <li>if {@code value} is {@code null}, a null JSON value will be
     *     added.</li>
     * </ul>
     *
     * @param key the key
     * @param value the value as a {@link JsonNode}
     * @return this
     * @since 1.1.10
     */
    public ProcessingMessage putJson(final String key, final JsonNode value)
    {
        BUNDLE.checkNotNull(key, "processingMessage.nullKey");
        if (value == null)
            return putNull(key);
        map.put(key, value.deepCopy());
        return this;
    }

    /**
     * Add a key/value pair to this message, which is also a formatter argument
     *
     * @param key the key
     * @param value the value
     * @return this
     */
    public ProcessingMessage putArgument(final String key, final JsonNode value)
    {
        addArgument(value);
        return putJson(key, value);
    }


    /**
     * Add a key/value pair to this message
     *
     * <p>{@link Object#toString()} will be called on the value.</p>
     *
     * @param key the key
     * @param value the value
     * @return this
     */
    public ProcessingMessage put(final String key, final Object value)
    {
        BUNDLE.checkNotNull(key, "processingMessage.nullKey");
        if (value == null)
            map.put(key, NullNode.getInstance());
        else
            map.put(key, value.toString());
        return this;
    }

    /**
     * Add a key/value pair to this message, which is also a formatter argument
     *
     * @param key the key
     * @param value the value
     * @return this
     */
    public ProcessingMessage putArgument(final String key, final Object value)
    {
        addArgument(value);
        return put(key, value);
    }

    /**
     * Add a key/value pair to this message
     *
     * <p>This will use {@link JsonUtils} to attempt to serialize the value to a
     * {@link JsonNode} using Jackson serialization.</p>
     *
     * <p>Notes:</p>
     *
     * <ul>
     *     <li>if {@code key} is {@code null}, the key/value pair will be
     *     discarded silently;</li>
     *     <li>if {@code value} is {@code null}, a null JSON value will be
     *     added.</li>
     * </ul>
     *
     * @param key the key
     * @param o the object
     * @return this
     * @since 1.1.10
     */
    public ProcessingMessage putSerialized(final String key, final Object o)
    {
        return putJson(key, JsonUtils.toJson(o));
    }

    /**
     * Add a key/value pair to this message
     *
     * <p>This is the main method. All other put methods call this one.</p>
     *
     * <p>Note that if the key is {@code null}, the content is <b>ignored</b>.
     * </p>
     *
     * @param key the key
     * @param value the value as a {@link JsonNode}
     * @return this
     * @deprecated use {@link #putJson(String, JsonNode)} instead; will be
     * removed in 1.1.11.
     */
    @Deprecated
    public ProcessingMessage put(final String key, final JsonNode value)
    {
        if (key == null)
            return this;
        if (value == null)
            return putNull(key);
        map.put(key, value.deepCopy());
        return this;
    }

    /**
     * Add a key/value pair to this message
     *
     * @param key the key
     * @param asJson the value, which implements {@link AsJson}
     * @return this
     * @deprecated use {@link #putSerialized(String, Object)} instead; will be
     * removed in 1.1.11.
     */
    @Deprecated
    public ProcessingMessage put(final String key, final AsJson asJson)
    {
        return putSerialized(key, asJson);
    }

    /**
     * Add a key/value pair to this message, which is also a formatter argument
     *
     * @param key the key
     * @param asJson the value
     * @return this
     * @deprecated will be removed in 1.1.11.
     */
    @Deprecated
    public ProcessingMessage putArgument(final String key, final AsJson asJson)
    {
        addArgument(JsonUtils.toJson(asJson));
        return putSerialized(key, asJson);
    }

    /**
     * Add a key/value pair to this message
     *
     * @param key the key
     * @param value the value as an integer
     * @return this
     */
    public ProcessingMessage put(final String key, final int value)
    {
        return putJson(key, FACTORY.numberNode(value));
    }

    /**
     * Add a key/value pair to this message, which is also a formatter argument
     *
     * @param key the key
     * @param value the value
     * @return this
     */
    public ProcessingMessage putArgument(final String key, final int value)
    {
        addArgument(value);
        return put(key, value);
    }

    /**
     * Add a key/value pair to this message, where the value is a collection of
     * items
     *
     * <p>This will put all values (again, using {@link #toString()} of the
     * collection into an array.</p>
     *
     * @param key the key
     * @param values the collection of values
     * @param <T> the element type of the collection
     * @return this
     */
    public <T> ProcessingMessage put(final String key, final Iterable<T> values)
    {
        if (values == null)
            return putNull(key);
        final ArrayNode node = FACTORY.arrayNode();
        for (final T value: values)
            node.add(value == null
                ? FACTORY.nullNode()
                : FACTORY.textNode(value.toString()));
        return putJson(key, node);
    }

    /**
     * Add a key/value pair to this message, which is also a formatter argument
     *
     * <p>Note that the collection will not be "exploded" into its individual
     * arguments.</p>
     *
     * @param key the key
     * @param values the collection of values
     * @return this
     */
    public <T> ProcessingMessage putArgument(final String key,
        final Iterable<T> values)
    {
        addArgument(values);
        return put(key, values);
    }

    private void addArgument(final Object value)
    {
        if (!map.has("message"))
            return;
        args.add(value);
        final String fmt = map.get("message").textValue();
        try {
            final String formatted = new Formatter()
                .format(fmt, args.toArray()).toString();
            map.put("message", FACTORY.textNode(formatted));
        } catch (IllegalFormatException ignored) {
        }
    }

    /**
     * Put a {@link NullNode} as a value for a key
     *
     * <p>Note: if {@code key} is null, the put will be <b>ignored</b>.</p>
     *
     * @param key the key
     * @return this
     */
    private ProcessingMessage putNull(final String key)
    {
        map.put(key, FACTORY.nullNode());
        return this;
    }

    @Override
    public JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.putAll(map);
        return ret;
    }

    @Override
    public void serialize(final JsonGenerator jgen,
        final SerializerProvider provider)
        throws IOException, JsonProcessingException
    {
        jgen.writeTree(map);
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
        final SerializerProvider provider, final TypeSerializer typeSer)
        throws IOException, JsonProcessingException
    {
        serialize(jgen, provider);
    }

    /**
     * Build an exception out of this message
     *
     * <p>This uses the {@link ExceptionProvider} built into the message and
     * invokes {@link ExceptionProvider#doException(ProcessingMessage)} with
     * {@code this} as an argument.</p>
     *
     * @return an exception
     * @see #setExceptionProvider(ExceptionProvider)
     */
    public ProcessingException asException()
    {
        return exceptionProvider.doException(this);
    }

    @Override
    public String toString()
    {
        final Map<String, JsonNode> tmp = JacksonUtils.asMap(map);
        final JsonNode node = tmp.remove("message");
        final String message = node == null ? "(no message)": node.textValue();
        final StringBuilder sb = new StringBuilder().append(level).append(": ");
        sb.append(message);
        for (final Map.Entry<String, JsonNode> entry: tmp.entrySet())
            sb.append("\n    ").append(entry.getKey()).append(": ")
                .append(entry.getValue());
        return sb.append('\n').toString();
    }
}
