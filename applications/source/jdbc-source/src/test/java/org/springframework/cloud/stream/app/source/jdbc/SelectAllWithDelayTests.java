/*
 * Copyright 2016-2019 the original author or authors.
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

package org.springframework.cloud.stream.app.source.jdbc;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.springframework.messaging.Message;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"jdbc.supplier.query=select id, name from test order by id", "spring.cloud.stream.poller.fixedDelay=60000"})
public class SelectAllWithDelayTests extends JdbcSourceIntegrationTests {

	@Test
	public void testExtraction() throws Exception {
		Message<?> received = messageCollector.forChannel(output).poll(10, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
		assertThat(received.getPayload().getClass()).isEqualTo(String.class);

		Map<?, ?> payload = this.objectMapper.readValue((String) received.getPayload(), Map.class);

		assertThat(payload.get("ID")).isEqualTo(1);
		received = messageCollector.forChannel(output).poll(10, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
		assertThat(received.getPayload().getClass()).isEqualTo(String.class);

		payload = this.objectMapper.readValue((String) received.getPayload(), Map.class);
		assertThat(payload.get("ID")).isEqualTo(2);
		received = messageCollector.forChannel(output).poll(10, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
		assertThat(received.getPayload().getClass()).isEqualTo(String.class);

		payload = this.objectMapper.readValue((String) received.getPayload(), Map.class);
		assertThat(payload.get("ID")).isEqualTo(3);
		// should not wrap around to the beginning since delay is 60
		received = messageCollector.forChannel(output).poll(1, TimeUnit.SECONDS);
		assertThat(received).isNull();
	}
}
