/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.opentelemetry.docker.compose;

import org.springframework.boot.docker.compose.service.connection.test.DockerComposeTest;
import org.springframework.boot.opentelemetry.autoconfigure.logging.OpenTelemetryLoggingConnectionDetails;
import org.springframework.boot.opentelemetry.autoconfigure.logging.Transport;
import org.springframework.boot.testsupport.container.TestImage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link OpenTelemetryLoggingDockerComposeConnectionDetailsFactory}
 * using {@link TestImage#GRAFANA_OTEL_LGTM}.
 *
 * @author Eddú Meléndez
 */
class GrafanaOpenTelemetryLoggingDockerComposeConnectionDetailsFactoryIntegrationTests {

	@DockerComposeTest(composeFile = "otlp-compose.yaml", image = TestImage.GRAFANA_OTEL_LGTM)
	void runCreatesConnectionDetails(OpenTelemetryLoggingConnectionDetails connectionDetails) {
		assertThat(connectionDetails.getUrl(Transport.HTTP)).startsWith("http://").endsWith("/v1/logs");
		assertThat(connectionDetails.getUrl(Transport.GRPC)).startsWith("http://").endsWith("/v1/logs");
	}

}
