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
package org.codehaus.griffon.compile.rmi;

import org.codehaus.griffon.compile.core.BaseConstants;
import org.codehaus.griffon.compile.core.MethodDescriptor;

import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedMethod;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedType;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotations;
import static org.codehaus.griffon.compile.core.MethodDescriptor.args;
import static org.codehaus.griffon.compile.core.MethodDescriptor.method;
import static org.codehaus.griffon.compile.core.MethodDescriptor.throwing;
import static org.codehaus.griffon.compile.core.MethodDescriptor.type;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParams;
import static org.codehaus.griffon.compile.core.MethodDescriptor.types;

/**
 * @author Andres Almiray
 */
public interface RmiAwareConstants extends BaseConstants {
    String RMI_HANDLER_TYPE = "griffon.plugins.rmi.RmiHandler";
    String RMI_HANDLER_PROPERTY = "rmiHandler";
    String RMI__HANDLER_FIELD_NAME = "this$" + RMI_HANDLER_PROPERTY;
    String RMI_CLIENT_CALLBACK_TYPE = "griffon.plugins.rmi.RmiClientCallback";
    String RMI_EXCEPTION_TYPE = "griffon.plugins.rmi.exceptions.RmiException";

    String METHOD_WITH_RMI = "withRmi";
    String METHOD_DESTROY_RMI_CLIENT = "destroyRmiClient";
    String CALLBACK = "callback";
    String PARAMS = "params";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(R),
            typeParams(R),
            METHOD_WITH_RMI,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), RMI_CLIENT_CALLBACK_TYPE, R)),
            throwing(type(RMI_EXCEPTION_TYPE))
        ),
        method(
            type(VOID),
            METHOD_DESTROY_RMI_CLIENT,
            args(annotatedType(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
    };
}
