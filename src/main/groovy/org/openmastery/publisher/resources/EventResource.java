/**
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
package org.openmastery.publisher.resources;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.NewEvent;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.EventService;
import org.openmastery.publisher.security.InvocationContext;
import org.openmastery.time.TimeService;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@Autowired
	private TimeService timeService;
	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private InvocationContext invocationContext;

	@Autowired
	private EventService eventService;

	private EventEntity toEventEntity(NewEvent event, EventType type) {
		return EventEntity.builder()
				.id(null)
				.ownerId(invocationContext.getUserId())
				.position(timeService.javaNow())
				.taskId(event.getTaskId())
				.comment(event.getComment())
				.type(type)
				.build();
	}

	@POST
	@Path(ResourcePaths.EVENT_NOTE_PATH)
	public void createNote(NewEvent event) {
		EventEntity eventEntity = toEventEntity(event, EventType.NOTE);
		persistenceService.saveEvent(eventEntity);
	}

	@POST
	@Path(ResourcePaths.EVENT_SUBTASK_PATH)
	public void createSubtask(NewEvent event) {
		EventEntity eventEntity = toEventEntity(event, EventType.SUBTASK);
		persistenceService.saveEvent(eventEntity);
	}

	@POST
	@Path(ResourcePaths.EVENT_WTF_PATH)
	public void createWTF(NewEvent event) {
		EventEntity eventEntity = toEventEntity(event, EventType.WTF);
		persistenceService.saveEvent(eventEntity);
	}

	@POST
	@Path(ResourcePaths.EVENT_AWESOME_PATH)
	public void createAwesome(NewEvent event) {
		EventEntity eventEntity = toEventEntity(event, EventType.AWESOME);
		persistenceService.saveEvent(eventEntity);
	}

	@GET
	@Path(ResourcePaths.EVENT_BATCH_PATH)
	public List<NewBatchEvent> getLatestEvents(@QueryParam("afterDate") String afterDate, @QueryParam("limit") Integer limit) {
		Long userId = invocationContext.getUserId();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		LocalDateTime jodaAfterDate = formatter.parseLocalDateTime(afterDate);
		return eventService.getLatestEvents(userId, jodaAfterDate, limit);
	}


	//Developers have been creating "note types" manually using [Subtask] and [Prediction] as prefixes in their comments.
	//Subtask events in particular I'm using to derive a "Subtask band" and collapse all the details of events/bands
	// that happen within a subtask, so you can "drill in" on one subtask at a time ford a complex IFM.

}
