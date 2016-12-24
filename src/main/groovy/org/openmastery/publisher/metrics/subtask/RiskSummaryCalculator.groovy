/*
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.metrics.subtask

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.api.metrics.SubtaskMetrics
import org.openmastery.publisher.metrics.subtask.calculator.AvgFeedbackLoopsCalculator
import org.openmastery.publisher.metrics.subtask.calculator.CapacityDistributionCalculator
import org.openmastery.publisher.metrics.subtask.calculator.MaxHaystackSizeCalculator
import org.openmastery.publisher.metrics.subtask.calculator.MaxWtfDurationCalculator
import org.openmastery.publisher.metrics.subtask.calculator.WtfsPerDayCalculator

public class RiskSummaryCalculator {

	SubtaskMetrics calculateSubtaskMetrics(Event subtask, IdeaFlowTimeline timelineSegment) {

		SubtaskMetrics metrics = new SubtaskMetrics()
		metrics.id = subtask.id
		metrics.description = subtask.comment
		metrics.durationInSeconds = timelineSegment.durationInSeconds


		metrics.addMetric(MetricType.WTFS_PER_DAY, new WtfsPerDayCalculator())
		metrics.addMetric(MetricType.MAX_HAYSTACK_SIZE, new MaxHaystackSizeCalculator())
		metrics.addMetric(MetricType.MAX_WTF_DURATION, new MaxWtfDurationCalculator())
		metrics.addMetric(MetricType.AVG_FEEDBACK_LOOPS, new AvgFeedbackLoopsCalculator())
		metrics.addMetric(MetricType.AVG_FEEDBACK_LOOP_DURATION, new AvgFeedbackLoopsCalculator())
		metrics.addMetric(MetricType.CAPACITY_DISTRIBUTION, new CapacityDistributionCalculator())

		metrics.calculate(timelineSegment)

		return metrics
	}


}