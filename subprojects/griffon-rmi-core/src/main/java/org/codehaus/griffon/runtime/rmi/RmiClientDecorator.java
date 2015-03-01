/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.rmi;

import griffon.plugins.rmi.RmiClient;
import griffon.plugins.rmi.UnaryConsumer;

import javax.annotation.Nonnull;
import java.rmi.Remote;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class RmiClientDecorator implements RmiClient {
    private final RmiClient delegate;

    public RmiClientDecorator(@Nonnull RmiClient delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected RmiClient getDelegate() {
        return delegate;
    }

    @Override
    public <T, R> R service(@Nonnull String serviceName, @Nonnull UnaryConsumer<T, R> consumer) {
        return delegate.service(serviceName, consumer);
    }

    @Override
    public void removeService(@Nonnull String serviceName) {
        delegate.removeService(serviceName);
    }

    @Override
    @Nonnull
    public Set<String> getServiceNames() {
        return delegate.getServiceNames();
    }

    @Override
    @Nonnull
    public Map<String, Remote> getServices() {
        return delegate.getServices();
    }
}
