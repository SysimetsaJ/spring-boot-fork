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

package org.springframework.boot.kafka.autoconfigure;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Test;

import org.springframework.boot.context.properties.source.MutuallyExclusiveConfigurationPropertiesException;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties.Admin;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties.Cleanup;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties.IsolationLevel;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties.Listener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.CleanupConfig;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ContainerProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link KafkaProperties}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 * @author Scott Frederick
 */
class KafkaPropertiesTests {

	@Test
	void isolationLevelEnumConsistentWithKafkaVersion() {
		org.apache.kafka.common.IsolationLevel[] original = org.apache.kafka.common.IsolationLevel.values();
		assertThat(original).extracting(Enum::name)
			.containsExactly(IsolationLevel.READ_UNCOMMITTED.name(), IsolationLevel.READ_COMMITTED.name());
		assertThat(original).extracting("id")
			.containsExactly(IsolationLevel.READ_UNCOMMITTED.id(), IsolationLevel.READ_COMMITTED.id());
		assertThat(original).hasSameSizeAs(IsolationLevel.values());
	}

	@Test
	void adminDefaultValuesAreConsistent() {
		KafkaAdmin admin = new KafkaAdmin(Collections.emptyMap());
		Admin adminProperties = new KafkaProperties().getAdmin();
		assertThat(admin).hasFieldOrPropertyWithValue("fatalIfBrokerNotAvailable", adminProperties.isFailFast());
		assertThat(admin).hasFieldOrPropertyWithValue("modifyTopicConfigs", adminProperties.isModifyTopicConfigs());
	}

	@Test
	void listenerDefaultValuesAreConsistent() {
		ContainerProperties container = new ContainerProperties("test");
		Listener listenerProperties = new KafkaProperties().getListener();
		assertThat(listenerProperties.isMissingTopicsFatal()).isEqualTo(container.isMissingTopicsFatal());
	}

	@Test
	void sslPemConfiguration() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setKeyStoreKey("-----BEGINkey");
		properties.getSsl().setTrustStoreCertificates("-----BEGINtrust");
		properties.getSsl().setKeyStoreCertificateChain("-----BEGINchain");
		Map<String, Object> consumerProperties = properties.buildConsumerProperties();
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, "-----BEGINkey");
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, "-----BEGINtrust");
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG,
				"-----BEGINchain");
	}

	@Test
	void sslPemConfigurationWithEmptyBundle() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setKeyStoreKey("-----BEGINkey");
		properties.getSsl().setTrustStoreCertificates("-----BEGINtrust");
		properties.getSsl().setKeyStoreCertificateChain("-----BEGINchain");
		properties.getSsl().setBundle("");
		Map<String, Object> consumerProperties = properties.buildConsumerProperties();
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, "-----BEGINkey");
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, "-----BEGINtrust");
		assertThat(consumerProperties).containsEntry(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG,
				"-----BEGINchain");
	}

	@Test
	void sslPropertiesWhenKeyStoreLocationAndKeySetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setKeyStoreKey("-----BEGIN");
		properties.getSsl().setKeyStoreLocation(new ClassPathResource("ksLoc"));
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void sslPropertiesWhenTrustStoreLocationAndCertificatesSetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setTrustStoreLocation(new ClassPathResource("tsLoc"));
		properties.getSsl().setTrustStoreCertificates("-----BEGIN");
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void sslPropertiesWhenKeyStoreLocationAndBundleSetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setBundle("myBundle");
		properties.getSsl().setKeyStoreLocation(new ClassPathResource("ksLoc"));
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void sslPropertiesWhenKeyStoreKeyAndBundleSetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setBundle("myBundle");
		properties.getSsl().setKeyStoreKey("-----BEGIN");
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void sslPropertiesWhenTrustStoreLocationAndBundleSetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setBundle("myBundle");
		properties.getSsl().setTrustStoreLocation(new ClassPathResource("tsLoc"));
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void sslPropertiesWhenTrustStoreCertificatesAndBundleSetShouldThrowException() {
		KafkaProperties properties = new KafkaProperties();
		properties.getSsl().setBundle("myBundle");
		properties.getSsl().setTrustStoreCertificates("-----BEGIN");
		assertThatExceptionOfType(MutuallyExclusiveConfigurationPropertiesException.class)
			.isThrownBy(properties::buildConsumerProperties);
	}

	@Test
	void cleanupConfigDefaultValuesAreConsistent() {
		CleanupConfig cleanupConfig = new CleanupConfig();
		Cleanup cleanup = new KafkaProperties().getStreams().getCleanup();
		assertThat(cleanup.isOnStartup()).isEqualTo(cleanupConfig.cleanupOnStart());
		assertThat(cleanup.isOnShutdown()).isEqualTo(cleanupConfig.cleanupOnStop());
	}

}
