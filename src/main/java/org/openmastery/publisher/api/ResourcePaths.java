/**
 * Copyright 2015 New Iron Group, Inc.
 * <p>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.api;

public class ResourcePaths {

	public static final String API_KEY_HEADER = "X-API-KEY";

	public static final String ID_PATH = "/id";

	// task paths
	public static final String TASK_PATH = "/task";
	public static final String RECENT_PATH = "/recent";
	public static final String ACTIVATE_PATH = "/activate";
	public static final String TASK_NAME_PATH = "/name";

	// ideaflow paths
	public static final String IDEAFLOW_PATH = "/ideaflow";

	public static final String IDEAFLOW_TIMELINE = "/timeline";
	public static final String IDEAFLOW_METRICS = "/metrics";
	public static final String IDEAFLOW_TASK = "/task";
	public static final String IDEAFLOW_SUBTASK = "/subtask";



	// activity paths
	public static final String ACTIVITY_PATH = "/activity";
	public static final String EDITOR_PATH = "/editor";
	public static final String IDLE_PATH = "/idle";
	public static final String EXTERNAL_PATH = "/external";

	// event paths
	public static final String EVENT_PATH = "/event";
	public static final String EVENT_NOTE_PATH = "/note";
	public static final String EVENT_SUBTASK_PATH = "/subtask";
	public static final String EVENT_WTF_PATH = "/wtf";
	public static final String EVENT_AWESOME_PATH = "/awesome";
	public static final String EVENT_BATCH_PATH = "/batch";

	// timeline paths
	public static final String TIMELINE_PATH = "/timeline";
	public static final String TIMELINE_TREE_PATH = "/tree";
	public static final String TIMELINE_BAND_PATH = "/band";
	public static final String TIMELINE_SUMMARY_PATH = "/summary";

	public static final String TIMELINE_ACTIVITY_PATH = "/activity";

	public static final String TIMELINE_DAY_PATH = "/day";
	public static final String TIMELINE_USER_PATH = "/user";
	public static final String TIMELINE_PROJECT_PATH = "/project";

	//user paths
	public static final String USER_PATH = "/user";
	public static final String APIKEY_PATH = "/apikey";

	//batch paths
	public static final String BATCH_PATH = "/batch";
}
