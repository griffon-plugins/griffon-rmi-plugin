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
import griffon.core.GriffonApplication;
import griffon.core.env.Metadata;
import griffon.plugins.monitor.MBeanManager;
import griffon.plugins.rmi.RmiClientStorage;
import griffon.plugins.rmi.RmiHandler;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;
import org.codehaus.griffon.runtime.rmi.monitor.RmiClientStorageMonitor;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andres Almiray
 */
@Named("rmi")
public class RmiAddon extends AbstractGriffonAddon {
    @Inject
    private RmiClientStorage rmiClientStorage;

    @Inject
    private MBeanManager mbeanManager;

    @Inject
    private Metadata metadata;

    @Inject
    private RmiHandler rmiHandler;

    @Override
    public void init(@Nonnull GriffonApplication application) {
        mbeanManager.registerMBean(new RmiClientStorageMonitor(metadata, rmiClientStorage));
    }

    @Override
    public void onShutdown(@Nonnull GriffonApplication application) {
        for (String clientId : rmiClientStorage.getKeys()) {
            rmiHandler.destroyRmiClient(clientId);
        }
    }
}
