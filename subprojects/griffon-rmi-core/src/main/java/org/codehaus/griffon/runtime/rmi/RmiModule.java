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

import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.plugins.rmi.RmiClientFactory;
import griffon.plugins.rmi.RmiClientStorage;
import griffon.plugins.rmi.RmiHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 */
@Named("rmi")
@ServiceProviderFor(Module.class)
public class RmiModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(RmiClientStorage.class)
            .to(DefaultRmiClientStorage.class)
            .asSingleton();

        bind(RmiClientFactory.class)
            .to(DefaultRmiClientFactory.class)
            .asSingleton();

        bind(RmiHandler.class)
            .to(DefaultRmiHandler.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(RmiAddon.class)
            .asSingleton();
        // end::bindings[]
    }
}
