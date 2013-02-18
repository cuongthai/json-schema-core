/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessorBuildError;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;

public abstract class ProcessorMap<K, IN extends MessageProvider, OUT extends MessageProvider>
{
    private final Map<K, Processor<IN, OUT>> processors = Maps.newHashMap();
    private Processor<IN, OUT> defaultProcessor = null;

    public final ProcessorMap<K, IN, OUT> addEntry(final K key,
        final Processor<IN, OUT> processor)
    {
        if (key == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_KEY));
        if (processor == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_PROCESSOR));
        processors.put(key, processor);
        return this;
    }

    public final ProcessorMap<K, IN, OUT> setDefaultProcessor(
        final Processor<IN, OUT> defaultProcessor)
    {
        if (defaultProcessor == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_PROCESSOR));
        this.defaultProcessor = defaultProcessor;
        return this;
    }

    public final Processor<IN, OUT> getProcessor()
    {
        return new Mapper<K, IN, OUT>(processors, f(), defaultProcessor);
    }

    protected abstract Function<IN, K> f();

    private static final class Mapper<K, IN extends MessageProvider, OUT extends MessageProvider>
        implements Processor<IN, OUT>
    {
        private final Map<K, Processor<IN, OUT>> processors;
        private final Function<IN, K> f;
        private final Processor<IN, OUT> defaultProcessor;

        Mapper(final Map<K, Processor<IN, OUT>> processors,
            final Function<IN, K> f, final Processor<IN, OUT> defaultProcessor)
        {
            if (f == null)
                throw new ProcessorBuildError(new ProcessingMessage()
                    .message(NULL_FUNCTION));
            this.processors = ImmutableMap.copyOf(processors);
            this.f = f;
            this.defaultProcessor = defaultProcessor;
        }

        @Override
        public OUT process(final ProcessingReport report, final IN input)
            throws ProcessingException
        {
            final K key = f.apply(input);
            Processor<IN, OUT> processor = processors.get(key);

            if (processor == null)
                processor = defaultProcessor;

            if (processor == null) // Not even a default processor. Ouch.
                throw new ProcessingException(new ProcessingMessage()
                    .message(NO_SUITABLE_PROCESSOR).put("key", key));

            return processor.process(report, input);
        }
    }
}
