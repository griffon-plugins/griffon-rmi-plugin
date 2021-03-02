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

import griffon.test.core.GriffonUnitRule
import griffon.test.core.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import java.rmi.Remote
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@TestFor(GroovyCalculatorService)
class GroovyCalculatorServiceTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    private GroovyCalculatorService service

    private Calculator remoteCalculator

    @Before
    void setup() throws Exception {
        remoteCalculator = new CalculatorImpl()
        Remote stub = UnicastRemoteObject.exportObject(remoteCalculator, 0)
        Registry registry = LocateRegistry.createRegistry(1199)
        registry.bind(Calculator.NAME, stub)
    }

    @After
    void cleanup() throws Exception {
        Registry registry = LocateRegistry.getRegistry(1199)
        registry.unbind(Calculator.NAME)
        UnicastRemoteObject.unexportObject(remoteCalculator, true)
    }

    @Test
    void addTwoNumbers() {
        // when:
        double result = service.calculate(21d, 21d)

        // then:
        assertNotNull(result)
        assertEquals(42d, result, 0d)
    }
}
