/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2021 The author and/or original authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.rmi;

import griffon.annotations.core.Nonnull;
import griffon.plugins.rmi.RmiClient;
import griffon.plugins.rmi.UnaryConsumer;
import griffon.plugins.rmi.exceptions.RmiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.rmi.registry.LocateRegistry.getRegistry;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * @author Andres Almiray
 */
public class DefaultRmiClient implements RmiClient {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRmiClient.class);

    private static final String ERROR_SERVICE_NAME_BLANK = "Argument 'serviceName' must not be blank";

    private final Map<String, Remote> services = new ConcurrentHashMap<>();
    private final String host;
    private final int port;
    private Registry registry;

    public DefaultRmiClient(@Nonnull String host, int port, boolean lazy) {
        this.host = requireNonBlank(host, "Argument 'host' must not be blank");
        this.port = port;
        if (!lazy) {
            locateRegistry();
        }
    }

    @Override
    public <T, R> R service(@Nonnull String serviceName, @Nonnull UnaryConsumer<T, R> consumer) {
        requireNonBlank(serviceName, ERROR_SERVICE_NAME_BLANK);
        LOG.debug("Invoking RMI callback on service {}", serviceName);
        return consumer.consume((T) locateService(serviceName));
    }

    @Override
    public void removeService(@Nonnull String serviceName) {
        requireNonBlank(serviceName, ERROR_SERVICE_NAME_BLANK);
        if (services.containsKey(serviceName)) {
            LOG.debug("Removing service {}", serviceName);
            services.remove(serviceName);
        }
    }

    @Nonnull
    @Override
    public Set<String> getServiceNames() {
        Set<String> names = new LinkedHashSet<>();
        synchronized (services) {
            names.addAll(services.keySet());
        }
        return unmodifiableSet(names);
    }

    @Nonnull
    @Override
    public Map<String, Remote> getServices() {
        Map<String, Remote> map = new LinkedHashMap<>();
        synchronized (services) {
            map.putAll(services);
        }
        return unmodifiableMap(map);
    }

    @Nonnull
    private Remote locateService(@Nonnull String serviceName) {
        requireNonBlank(serviceName, ERROR_SERVICE_NAME_BLANK);
        locateRegistry();
        Remote service = services.get(serviceName);
        if (service == null) {
            try {
                LOG.debug("Locating service {} at {}:{}", serviceName, host, port);
                service = registry.lookup(serviceName);
            } catch (RemoteException | NotBoundException e) {
                throw new RmiException("An error occurred while looking up " + serviceName + " at " + host + ":" + port, e);
            }
            services.put(serviceName, service);
        }
        return service;
    }

    private void locateRegistry() {
        if (registry == null) {
            try {
                LOG.debug("Locating RMI registry at {}:{}", host, port);
                registry = getRegistry(host, port);
            } catch (RemoteException e) {
                throw new RmiException("An error occurred while attempting to locate an RMI registry at " + host + ":" + port, e);
            }
        }
    }
}
