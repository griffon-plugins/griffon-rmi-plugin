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
package org.codehaus.griffon.compile.rmi.ast.transform

import griffon.plugins.rmi.RmiHandler
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * @author Andres Almiray
 */
class RmiAwareASTTransformationSpec extends Specification {
    def 'RmiAwareASTTransformation is applied to a bean via @RmiAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        @griffon.transform.RmiAware
        class Bean { }
        new Bean()
        ''')

        then:
        bean instanceof RmiHandler
        RmiHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                candidate.returnType == target.returnType &&
                candidate.parameterTypes == target.parameterTypes &&
                candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }

    def 'RmiAwareASTTransformation is not applied to a RmiHandler subclass via @RmiAware'() {
        given:
        GroovyShell shell = new GroovyShell()

        when:
        def bean = shell.evaluate('''
        import griffon.plugins.rmi.*
        import griffon.plugins.rmi.exceptions.*

        import javax.annotation.Nonnull
        @griffon.transform.RmiAware
        class RmiHandlerBean implements RmiHandler {
            @Override
            public <R> R withRmi(@Nonnull Map<String,Object> params,@Nonnull RmiClientCallback<R> callback) throws RmiException{
                return null
            }
            @Override
            void destroyRmiClient(@Nonnull String clientId) {}
        }
        new RmiHandlerBean()
        ''')

        then:
        bean instanceof RmiHandler
        RmiHandler.methods.every { Method target ->
            bean.class.declaredMethods.find { Method candidate ->
                candidate.name == target.name &&
                    candidate.returnType == target.returnType &&
                    candidate.parameterTypes == target.parameterTypes &&
                    candidate.exceptionTypes == target.exceptionTypes
            }
        }
    }
}
