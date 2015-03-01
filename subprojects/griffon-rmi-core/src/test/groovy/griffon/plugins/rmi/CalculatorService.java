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
package griffon.plugins.rmi;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonService;
import griffon.exceptions.GriffonException;
import griffon.metadata.ArtifactProviderFor;
import griffon.plugins.rmi.exceptions.RmiException;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.rmi.RemoteException;
import java.util.Map;

@ArtifactProviderFor(GriffonService.class)
public class CalculatorService extends AbstractGriffonService {
    @Inject
    private RmiHandler rmiHandler;

    @Inject
    public CalculatorService(@Nonnull GriffonApplication application) {
        super(application);
    }

    public Double calculate(final double num1, final double num2) {
        Map<String, Object> params = CollectionUtils.<String, Object>map()
            .e("id", "client");
        return rmiHandler.withRmi(params,
            new RmiClientCallback<Double>() {
                @Nullable
                public Double handle(@Nonnull Map<String, Object> params, @Nonnull RmiClient client) throws RmiException {
                    return client.service(Calculator.NAME, new UnaryConsumer<Calculator, Double>() {
                        @Override
                        public Double consume(Calculator calculator) {
                            try {
                                return calculator.add(num1, num2);
                            } catch (RemoteException e) {
                                throw new GriffonException(e);
                            }
                        }
                    });
                }
            });
    }
}