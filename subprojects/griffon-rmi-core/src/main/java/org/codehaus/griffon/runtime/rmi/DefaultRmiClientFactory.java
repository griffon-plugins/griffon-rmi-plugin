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
import griffon.annotations.core.Nullable;
import griffon.core.env.Metadata;
import griffon.plugins.monitor.MBeanManager;
import griffon.plugins.rmi.RmiClient;
import griffon.plugins.rmi.RmiClientFactory;
import org.codehaus.griffon.runtime.rmi.monitor.RmiClientMonitor;

import javax.inject.Inject;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.ConfigUtils.getConfigValueAsInt;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultRmiClientFactory implements RmiClientFactory {
    @Inject
    private Metadata metadata;

    @Inject
    private MBeanManager mBeanManager;

    @Nonnull
    public RmiClient create(@Nonnull Map<String, Object> params, @Nullable String id) {
        requireNonNull(params, "Argument 'params' must not be null");
        String host = getConfigValue(params, "host", "localhost");
        int port = getConfigValueAsInt(params, "port", 1099);
        boolean lazy = getConfigValueAsBoolean(params, "lazy", true);

        DefaultRmiClient delegate = new DefaultRmiClient(host, port, lazy);
        if (!isBlank(id)) {
            RmiClientMonitor monitor = new RmiClientMonitor(metadata, delegate, id);
            JMXAwareRmiClient rmiClient = new JMXAwareRmiClient(delegate);
            rmiClient.addObjectName(mBeanManager.registerMBean(monitor, false).getCanonicalName());
            return rmiClient;
        }
        return delegate;
    }

    @Override
    public void destroy(@Nonnull RmiClient client) {
        requireNonNull(client, "Argument 'client' must not be null");
        if (client instanceof JMXAwareRmiClient) {
            unregisterMBeans((JMXAwareRmiClient) client);
        }
    }

    private void unregisterMBeans(@Nonnull JMXAwareRmiClient client) {
        for (String objectName : client.getObjectNames()) {
            mBeanManager.unregisterMBean(objectName);
        }
        client.clearObjectNames();
    }
}
