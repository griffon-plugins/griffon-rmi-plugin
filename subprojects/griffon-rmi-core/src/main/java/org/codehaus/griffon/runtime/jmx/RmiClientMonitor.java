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
package org.codehaus.griffon.runtime.jmx;

import griffon.core.env.Metadata;
import griffon.plugins.rmi.RmiClient;
import org.codehaus.griffon.runtime.monitor.AbstractMBeanRegistration;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.rmi.Remote;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class RmiClientMonitor extends AbstractMBeanRegistration implements RmiClientMonitorMXBean {
    private RmiClient delegate;
    private final String name;

    public RmiClientMonitor(@Nonnull Metadata metadata, @Nonnull RmiClient delegate, @Nonnull String name) {
        super(metadata);
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.name = name;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        return new ObjectName("griffon.plugins.rmi:type=RmiClient,application=" + metadata.getApplicationName() + ",name=" + this.name);
    }

    @Override
    public void postDeregister() {
        delegate = null;
        super.postDeregister();
    }

    @Override
    public ServiceInfo[] getServiceDetails() {
        Map<String, Remote> services = delegate.getServices();
        ServiceInfo[] data = new ServiceInfo[services.size()];
        int i = 0;
        for (Map.Entry<String, Remote> entry : services.entrySet()) {
            data[i++] = new ServiceInfo(entry.getKey(), entry.getValue().getClass().getName());
        }
        return data;
    }

    @Override
    public int getServiceCount() {
        return delegate.getServiceNames().size();
    }
}