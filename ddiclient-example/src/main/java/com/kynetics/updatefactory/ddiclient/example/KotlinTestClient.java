/*
 *   Copyright © 2017-2018 Kynetics LLC
 *
 *   All rights reserved. This program and the accomp*ing materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.htm
 */

package com.kynetics.updatefactory.ddiclient.example;

import com.kynetics.updatefactory.ddiclient.api.ClientBuilder;
import com.kynetics.updatefactory.ddiclient.api.ServerType;
import com.kynetics.updatefactory.ddiclient.api.api.DdiRestApi;
import com.kynetics.updatefactory.ddiclient.corek.Client;
import com.kynetics.updatefactory.ddiclient.corek.UFService;
import com.kynetics.updatefactory.ddiclient.corek.model.UFState;
import com.kynetics.updatefactory.ddiclient.example.callback.OnTargetTokenFoundMock;
import com.kynetics.updatefactory.ddiclient.example.callback.SystemOperationMock;
import com.kynetics.updatefactory.ddiclient.example.callback.TargetDataMock;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Daniele Sergio
 */
public class KotlinTestClient {
    private static final String CONFIGURATION_FILE_NAME = "update_factory.properties";
    private static final String CONFIGURATION_URL_KEY = "url";
    private static final String CONFIGURATION_TENANT_KEY = "tenant";
    private static final String CONFIGURATION_CONTROLLER_ID_KEY = "controllerId";
    private static final String CONFIGURATION_GATEWAY_TOKEN_KEY = "gatewayToken";

    public static void main(String... args) throws IOException {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGURATION_FILE_NAME);
        final Properties properties = new Properties();
        properties.load(inputStream);

        DdiRestApi api = new ClientBuilder()
                .withBaseUrl(properties.getProperty(CONFIGURATION_URL_KEY))
                .withGatewayToken(properties.getProperty(CONFIGURATION_GATEWAY_TOKEN_KEY))
                .withHttpBuilder(new OkHttpClient.Builder())
                .withOnTargetTokenFound(new OnTargetTokenFoundMock())
                .withServerType(ServerType.UPDATE_FACTORY)
                .build();

        UFService ufServiceK = new UFService(
                new Client(api, properties.getProperty(CONFIGURATION_TENANT_KEY), properties.getProperty(CONFIGURATION_CONTROLLER_ID_KEY)),
                new SystemOperationMock(),
                new TargetDataMock(),
                new UFState(UFState.Name.WAITING, new UFState.Data(0))
        );

        ufServiceK.start();
    }
}
