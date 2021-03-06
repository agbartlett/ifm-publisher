package org.openmastery.publisher.api;

import org.openmastery.publisher.api.activity.RandomNewEditorActivityBuilder;
import org.openmastery.publisher.api.batch.RandomNewBatchEventBuilder;
import org.openmastery.publisher.api.task.RandomNewTaskBuilder;

public class RandomApiBuilderSupport {

	public RandomNewTaskBuilder newTask() {
		return new RandomNewTaskBuilder();
	}

	public RandomNewEditorActivityBuilder newEditorActivity() {
		return new RandomNewEditorActivityBuilder();
	}

	public RandomNewBatchEventBuilder newBatchEvent() {
		return new RandomNewBatchEventBuilder();
	}

}
