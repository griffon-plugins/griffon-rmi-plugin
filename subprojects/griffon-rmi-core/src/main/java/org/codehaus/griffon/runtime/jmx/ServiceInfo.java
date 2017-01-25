/*
 * Copyright 2014-2017 the original author or authors.
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

import org.codehaus.griffon.runtime.monitor.AbstractCompositeData;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import java.beans.ConstructorProperties;

/**
 * @author Andres Almiray
 */
public class ServiceInfo extends AbstractCompositeData {
    private final String serviceName;
    private final String serviceClass;

    public static final CompositeType COMPOSITE_TYPE;
    private static final String[] ATTRIBUTE_NAMES = {"serviceName", "serviceClass"};

    static {
        try {
            COMPOSITE_TYPE = new CompositeType(
                ServiceInfo.class.getName(),
                ServiceInfo.class.getName(),
                ATTRIBUTE_NAMES,
                ATTRIBUTE_NAMES,
                new OpenType[]{SimpleType.STRING, SimpleType.STRING}
            );
        } catch (OpenDataException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @ConstructorProperties({"serviceName", "serviceClass"})
    public ServiceInfo(String mvcType, String mvcId) {
        this.serviceName = mvcType;
        this.serviceClass = mvcId;
        try {
            cds = new CompositeDataSupport(
                COMPOSITE_TYPE,
                ATTRIBUTE_NAMES,
                new Object[]{mvcType, mvcId}
            );
        } catch (OpenDataException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceClass() {
        return serviceClass;
    }
}
