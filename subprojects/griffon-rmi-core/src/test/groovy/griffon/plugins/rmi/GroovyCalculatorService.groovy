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
package griffon.plugins.rmi

import griffon.core.artifact.GriffonService
import org.kordamp.jipsy.annotations.ServiceProviderFor

import javax.inject.Inject

@ServiceProviderFor(GriffonService)
class GroovyCalculatorService {
    @Inject
    private RmiHandler rmiHandler

    Double calculate(double num1, double num2) {
        Map params = [id: 'client', port: 1199]
        rmiHandler.withRmi(params, { Map<String, Object> ps, RmiClient client ->
            client.service(Calculator.NAME) { Calculator calculator ->
                calculator.add(num1, num2)
            }
        })
    }
}