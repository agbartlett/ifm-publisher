package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType
import org.ideaflow.publisher.api.timeline.TreeNode
import org.ideaflow.publisher.api.timeline.TreeNodeType
import org.ideaflow.publisher.api.timeline.TreeTimeline
import spock.lang.Specification

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK


class TreeTimelineBuilderSpec extends Specification {

	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private List<BandTimelineSegment> createBandTimelineSegments() {
		BandTimelineFactory factory = new BandTimelineFactory()
		factory.persistenceService = testSupport.persistenceService
		factory.createAndSplitBandTimelineSegmentForTask(testSupport.taskId)
	}

	private TreeTimelineValidator createTreeTimelineAndValidator() {
		List<BandTimelineSegment> segments = createBandTimelineSegments()
		TreeTimeline treeTimeline = new TreeTimelineBuilder()
				.addTimelineSegments(segments)
				.build()
		new TreeTimelineValidator(treeTimeline)
	}

	def "should indent segment bands"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.endBand(LEARNING)

		when:
		TreeTimelineValidator validator = createTreeTimelineAndValidator()

		then:
		validator.assertTreeNodeSegment(0)
		validator.assertTreeNodeBand(1, PROGRESS)
		validator.assertTreeNodeBand(1, LEARNING)
		validator.assertValidationComplete()
	}

	def "should indent nested bands"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 2)
		testSupport.editor()

		when:
		TreeTimelineValidator validator = createTreeTimelineAndValidator()

		then:
		validator.assertTreeNodeSegment(0)
		validator.assertTreeNodeBand(1, PROGRESS)
		validator.assertTreeNodeBand(1, LEARNING)
		validator.assertTreeNodeBand(2, CONFLICT)
		validator.assertValidationComplete()
	}

	def "should indent linked bands"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.startBandAndAdvanceHours(REWORK, 1)
		testSupport.editor()

		when:
		TreeTimelineValidator validator = createTreeTimelineAndValidator()

		then:
		validator.assertTreeNodeSegment(0)
		validator.assertTreeNodeBand(1, PROGRESS)
		validator.assertTreeNodeGroup(1)
		validator.assertTreeNodeBand(2, LEARNING)
		validator.assertTreeNodeBand(2, REWORK)
		validator.assertValidationComplete()
	}

	def "should indent events at band level"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.note()
		testSupport.advanceHours(1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.note()
		testSupport.editor()

		when:
		TreeTimelineValidator validator = createTreeTimelineAndValidator()

		then:
		validator.assertTreeNodeSegment(0)
		validator.assertTreeNodeBand(1, PROGRESS)
		validator.assertTreeNodeBand(1, LEARNING)
		validator.assertTreeNodeEvent(1)
		validator.assertTreeNodeBand(2, CONFLICT)
		validator.assertTreeNodeEvent(2)
		validator.assertValidationComplete()
	}


	private static class TreeTimelineValidator {

		private List<TreeNode> treeNodes
		private int currentIndex = 0

		TreeTimelineValidator(TreeTimeline treeTimeline) {
			this.treeNodes = treeTimeline.treeNodes
		}

		private void assertTreeNode(int index, int expectedIndent, TreeNodeType expectedType) {
			assert treeNodes[index] != null
			assert treeNodes[index].type == expectedType
			assert treeNodes[index].indentLevel == expectedIndent
		}

		void assertTreeNodeSegment(int expectedIndent) {
			int index = currentIndex++
			assertTreeNode(index, expectedIndent, TreeNodeType.SEGMENT)
		}

		void assertTreeNodeGroup(int expectedIndent) {
			int index = currentIndex++
			assertTreeNode(index, expectedIndent, TreeNodeType.TIME_BAND_GROUP)
		}

		void assertTreeNodeBand(int expectedIndent, IdeaFlowStateType bandType) {
			int index = currentIndex++
			assertTreeNode(index, expectedIndent, TreeNodeType.IDEA_FLOW_BAND)
			assert treeNodes[index].bandType == bandType
		}

		void assertTreeNodeEvent(int expectedIndent) {
			int index = currentIndex++
			assertTreeNode(index, expectedIndent, TreeNodeType.EVENT)
		}

		void assertValidationComplete() {
			assert currentIndex == treeNodes.size()
		}

	}

}
