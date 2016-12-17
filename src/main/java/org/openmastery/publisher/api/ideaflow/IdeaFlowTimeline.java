package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.task.Task;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class IdeaFlowTimeline {

	private Task task;

	private LocalDateTime start;
	private LocalDateTime end;

	private Long durationInSeconds;
	private Long relativePositionInSeconds; //can be offset if showing a subtask fragment

	private List<IdeaFlowBand> ideaFlowBands;
	private List<ModificationActivity> modificationActivities;
	private List<ExecutionEvent> executionEvents;
	private List<CalendarEvent> calendarEvents; //TODO need to generate
	private List<Event> events;

}