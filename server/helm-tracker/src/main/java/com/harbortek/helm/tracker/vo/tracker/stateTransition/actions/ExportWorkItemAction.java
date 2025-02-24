/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.tracker.vo.tracker.stateTransition.actions;

import com.harbortek.helm.tracker.anotation.ActionType;
import com.harbortek.helm.tracker.constants.StateTransitionActions;
import com.harbortek.helm.tracker.entity.tracker.TrackerItemEntity;
import com.harbortek.helm.tracker.vo.tracker.stateTransition.TrackerStateTransitionAction;

import java.util.Collection;
@ActionType(type = StateTransitionActions.ACTION_TYPE_EXPORT_WORK_ITEM)
public class ExportWorkItemAction  extends TrackerStateTransitionAction {
	String documentType; //word or excel
	ExportConfig exportConfig;
	String comment;
	static class ExportConfig{

	}
	static class SimpleWordExportConfig extends ExportConfig {
		Boolean generateAbsoluteLinks;
		Boolean exportToPDF;
		String paragraphNumberingOption;
		String templateFilePath;
	}
	static class RoundTripWordExportConfig extends ExportConfig {
		Collection<TrackerItemEntity> configurationItems;
		String templateFilePath;
	}
	static class SimpleExcelExportConfig extends ExportConfig {
		Boolean exportDescription;
		Boolean multiValuesGotoMultipleRows;
		Boolean exportDatesInYourTimezone;
		String templateFilePath;
	}
	static class RoundTripExcelExportConfig extends ExportConfig {
		Boolean exportDescription;
		Boolean multiValuesGotoMultipleRows;
		Boolean exportDatesInYourTimezone;
		String templateFilePath;
	}
}

