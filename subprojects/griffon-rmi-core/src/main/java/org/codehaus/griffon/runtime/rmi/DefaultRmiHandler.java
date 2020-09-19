/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2014-2020 The author and/or original authors.
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
import griffon.annotations.core.Nullable;
import griffon.plugins.rmi.RmiClient;
import griffon.plugins.rmi.RmiClientCallback;
import griffon.plugins.rmi.RmiClientFactory;
import griffon.plugins.rmi.RmiClientStorage;
import griffon.plugins.rmi.RmiHandler;
import griffon.plugins.rmi.exceptions.RmiException;

import javax.inject.Inject;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultRmiHandler implements RmiHandler {
    private static final String ERROR_PARAMS_NULL = "Argument 'params' must not be null";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' must not be null";
    private static final String KEY_ID = "id";

    private final RmiClientFactory rmiClientFactory;
    private final RmiClientStorage rmiClientStorage;

    @Inject
    public DefaultRmiHandler(@Nonnull RmiClientFactory rmiClientFactory, @Nonnull RmiClientStorage rmiClientStorage) {
        this.rmiClientFactory = rmiClientFactory;
        this.rmiClientStorage = rmiClientStorage;
    }

    @Nullable
    @Override
    public <R> R withRmi(@Nonnull Map<String, Object> params, @Nonnull RmiClientCallback<R> callback) throws RmiException {
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        try {
            RmiClient client = getRmiClient(params);
            return callback.handle(params, client);
        } catch (Exception e) {
            throw new RmiException("An error occurred while executing RMI call", e);
        }
    }

    @Nonnull
    private RmiClient getRmiClient(@Nonnull Map<String, Object> params) {
        requireNonNull(params, ERROR_PARAMS_NULL);
        if (params.containsKey(KEY_ID)) {
            String id = String.valueOf(params.remove(KEY_ID));
            RmiClient client = rmiClientStorage.get(id);
            if (client == null) {
                client = rmiClientFactory.create(params, id);
                rmiClientStorage.set(id, client);
            }
            return client;
        }
        return rmiClientFactory.create(params, null);
    }

    @Override
    public void destroyRmiClient(@Nonnull String clientId) {
        requireNonBlank(clientId, "Argument 'clientId' must not be blank");
        RmiClient rmiClient = rmiClientStorage.get(clientId);
        try {
            if (rmiClient != null) {
                rmiClientFactory.destroy(rmiClient);
            }
        } finally {
            rmiClientStorage.remove(clientId);
        }
    }
}
