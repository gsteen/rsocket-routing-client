/*
 * Copyright 2013-2019 the original author or authors.
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

package io.rsocket.routing.frames;

import java.util.Objects;
import java.util.StringJoiner;

import io.netty.buffer.ByteBuf;
import io.rsocket.routing.common.Id;
import io.rsocket.routing.common.Tags;

/**
 * Representation of decoded BrokerInfo information.
 */
public final class BrokerInfo {
	/**
	 * BrokerInfo metadata key.
	 */
	public static final String METADATA_KEY = BrokerInfo.class.getSimpleName().toLowerCase();

	/**
	 * BrokerInfo subtype.
	 */
	public static final String BROKER_INFO = "x.rsocket.routing." + METADATA_KEY + ".v0";

	private final Id brokerId;

	private final long timestamp;

	private final Tags tags;

	private BrokerInfo(Id brokerId, long timestamp, Tags tags) {
		this.brokerId = brokerId;
		this.timestamp = timestamp;
		this.tags = tags;
	}

	public Id getBrokerId() {
		return this.brokerId;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public Tags getTags() {
		return this.tags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BrokerInfo that = (BrokerInfo) o;
		return this.timestamp == that.timestamp
				&& Objects.equals(this.brokerId, that.brokerId)
				&& Objects.equals(this.tags, that.tags);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.brokerId, this.timestamp, this.tags);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", BrokerInfo.class.getSimpleName() + "[", "]")
				.add("brokerId=" + brokerId)
				.add("timestamp=" + timestamp)
				.add("tags=" + tags)
				.toString();
	}

	public static Builder from(Id brokerId) {
		return new Builder(brokerId);
	}

	public static BrokerInfo from(ByteBuf byteBuf) {
		return from(BrokerInfoFlyweight.brokerId(byteBuf))
				.timestamp(BrokerInfoFlyweight.timestamp(byteBuf))
				.with(BrokerInfoFlyweight.tags(byteBuf))
				.build();
	}

	public static final class Builder extends Tags.Builder<Builder> {

		private final Id brokerId;

		private long timestamp = System.currentTimeMillis();

		private Builder(Id brokerId) {
			Objects.requireNonNull(brokerId, "brokerId may not be null");
			this.brokerId = brokerId;
		}

		public Builder timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public BrokerInfo build() {
			if (timestamp <= 0) {
				throw new IllegalArgumentException("timestamp must be > 0");
			}
			return new BrokerInfo(brokerId, timestamp, buildTags());
		}

	}

}
