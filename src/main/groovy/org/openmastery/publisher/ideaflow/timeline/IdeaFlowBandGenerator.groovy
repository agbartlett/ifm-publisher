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
package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel

class IdeaFlowBandGenerator {

	private int learningTimeThresholdInMinutes = 5
	private int learningModificationCountThreshold = 150

	public List<IdeaFlowBandModel> generateIdeaFlowBands(List<Positionable> positionableList) {
		if (positionableList.isEmpty()) {
			return []
		}

		List<Positionable> sortedPositionableList = positionableList.sort(false, PositionableComparator.INSTANCE)

		List<IdeaFlowBandModel> strategyBands = generateStrategyBands(sortedPositionableList)
		List<IdeaFlowBandModel> troubleshootingBands = generateTroubleshootingBands(sortedPositionableList)
		List<IdeaFlowBandModel> ideaFlowBandList = strategyBands + troubleshootingBands
		ideaFlowBandList.sort(PositionableComparator.INSTANCE)

		List progressBands = []
		LocalDateTime lastBandEndTime = sortedPositionableList.first().position
		for (IdeaFlowBandModel ideaFlowBandModel : ideaFlowBandList) {
			if (ideaFlowBandModel.start.isAfter(lastBandEndTime)) {
				progressBands << createIdeaFlowBand(lastBandEndTime, ideaFlowBandModel.start, IdeaFlowStateType.PROGRESS)
			}
			lastBandEndTime = ideaFlowBandModel.end
		}

		LocalDateTime endTimeOfLastPositionable = getEndTime(sortedPositionableList.last())
		if (lastBandEndTime.isBefore(endTimeOfLastPositionable)) {
			progressBands << createIdeaFlowBand(lastBandEndTime, endTimeOfLastPositionable, IdeaFlowStateType.PROGRESS)
		}

		ideaFlowBandList.addAll(progressBands)
		ideaFlowBandList.sort(PositionableComparator.INSTANCE)
		ideaFlowBandList
	}

	private List<IdeaFlowBandModel> generateStrategyBands(List<Positionable> sortedPositionableList) {
		List<IdeaFlowBandModel> learningBandList = []

		LocalDateTime learningBandStartTime = sortedPositionableList.first().position
		ModificationActivityTracker tracker = new ModificationActivityTracker(learningTimeThresholdInMinutes, learningModificationCountThreshold)
		for (Positionable positionable : sortedPositionableList) {
			tracker.addModificationActivity(positionable)
			if (tracker.isOverModificationThreshold()) {
				if (learningBandStartTime != null) {
					learningBandList << createIdeaFlowBand(learningBandStartTime, tracker.earliestTrackedStartTime, IdeaFlowStateType.LEARNING)
					learningBandStartTime = null
				}
			} else if (learningBandStartTime == null) {
				learningBandStartTime = tracker.earliestTrackedStartTime
			}
		}

		if (learningBandStartTime != null) {
			learningBandList << createIdeaFlowBand(learningBandStartTime, tracker.latestTrackedEndTime, IdeaFlowStateType.LEARNING)
		}

		learningBandList.removeAll { IdeaFlowBandModel ideaFlowBandModel ->
			ideaFlowBandModel.start.plusMinutes(learningTimeThresholdInMinutes).isAfter(ideaFlowBandModel.end)
		}
		learningBandList
	}

	private List<IdeaFlowBandModel> generateTroubleshootingBands(List<Positionable> sortedPositionableList) {
		List<IdeaFlowBandModel> troubleshootingBandList = []
		LocalDateTime troubleshootingStart = null
		List<Event> troubleshootingEvents = getTroubleshootingEvents(sortedPositionableList)
		for (Event event : troubleshootingEvents) {
			if (event.type == EventType.WTF) {
				if (troubleshootingStart == null) {
					troubleshootingStart = event.position
				}
			} else if (event.type == EventType.AWESOME) {
				if (troubleshootingStart != null) {
					troubleshootingBandList << createIdeaFlowBand(troubleshootingStart, event.position, IdeaFlowStateType.TROUBLESHOOTING)
					troubleshootingStart = null
				}
			}
		}
		troubleshootingBandList
	}

	private List<Event> getTroubleshootingEvents(List<Positionable> sortedPositionableList) {
		sortedPositionableList.findAll {
			EventType eventType = null
			if (it instanceof Event) {
				eventType = ((Event) it).type
			}
			eventType == EventType.WTF || eventType == EventType.AWESOME
		}
	}


	private IdeaFlowBandModel createIdeaFlowBand(LocalDateTime start, LocalDateTime end, IdeaFlowStateType type) {
		IdeaFlowBandModel.builder()
				.start(start)
				.end(end)
				.type(type)
				.nestedBands([])
				.idleBands([])
				.build()
	}

	private static LocalDateTime getEndTime(Positionable pos) {
		(pos instanceof Interval) ? pos.end : pos.position
	}


	private static class ModificationActivityTracker {

		private int timeThresholdInMinutes
		private int modificationCountThreshold
		private List<Positionable> recentActivityList = []

		ModificationActivityTracker(int timeThresholdInMinutes, int modificationCountThreshold) {
			this.timeThresholdInMinutes = timeThresholdInMinutes
			this.modificationCountThreshold = modificationCountThreshold
		}

		void addModificationActivity(Positionable positionable) {
			LocalDateTime minimumPosition = positionable.position.minusMinutes(timeThresholdInMinutes)

			recentActivityList.removeAll { Positionable p ->
				p.position.isBefore(minimumPosition)
			}
			recentActivityList.add(positionable)
		}

		boolean isOverModificationThreshold() {
			int modificationCount = recentActivityList.sum { Positionable positionable ->
				(positionable instanceof ModificationActivity) ? positionable.modificationCount : 0
			}
			modificationCount > modificationCountThreshold
		}

		LocalDateTime getEarliestTrackedStartTime() {
			recentActivityList.collect { Positionable pos ->
				pos.position
			}.sort().first()
		}

		LocalDateTime getLatestTrackedEndTime() {
			recentActivityList.collect { Positionable pos ->
				getEndTime(pos)
			}.sort().last()
		}

	}

}
