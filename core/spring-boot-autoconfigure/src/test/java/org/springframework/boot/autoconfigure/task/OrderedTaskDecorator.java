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

package org.springframework.boot.autoconfigure.task;

import org.springframework.core.Ordered;
import org.springframework.core.task.TaskDecorator;

/**
 * {@link TaskDecorator} that is {@link Ordered ordered}.
 *
 * @author Andy Wilkinson
 */
class OrderedTaskDecorator implements TaskDecorator, Ordered {

	private final int order;

	OrderedTaskDecorator() {
		this(0);
	}

	OrderedTaskDecorator(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Runnable decorate(Runnable runnable) {
		return runnable;
	}

}
