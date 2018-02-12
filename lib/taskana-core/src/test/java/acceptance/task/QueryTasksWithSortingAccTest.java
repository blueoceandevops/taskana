package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.TaskState;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks with sorting" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksWithSortingAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryTasksWithSortingAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testSortByModifiedAndDomain()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("key5")
            .orderByModified(desc)
            .orderByDomain(null)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getModified().isBefore(taskSummary.getModified()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testSortByDomainNameAndCreated()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("key5")
            .orderByDomain(asc)
            .orderByName(asc)
            .orderByCreated(null)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            // System.out.println("domain: " + taskSummary.getDomain() + ", name: " + taskSummary.getName() + ",
            // created: " + taskSummary.getCreated());
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getDomain().compareToIgnoreCase(previousSummary.getDomain()) >= 0);
                if (taskSummary.getDomain().equals(previousSummary.getDomain())) {
                    Assert.assertTrue(taskSummary.getName().compareToIgnoreCase(previousSummary.getName()) >= 0);
                    if (taskSummary.getName().equals(previousSummary.getName())) {
                        Assert.assertTrue(!taskSummary.getCreated().isBefore(previousSummary.getCreated()));
                    }
                }
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testSortByPorSystemNoteDueAndOwner()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("key5")
            .orderByPrimaryObjectReferenceSystem(SortDirection.DESCENDING)
            .orderByNote(null)
            .orderByDue(null)
            .orderByOwner(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getSystem().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getSystem()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testSortByPorSystemInstanceParentProcPlannedAndState()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("key5")
            .orderByPrimaryObjectReferenceSystemInstance(desc)
            .orderByParentBusinessProcessId(asc)
            .orderByPlanned(asc)
            .orderByState(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getSystemInstance().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getSystemInstance()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "user_1_2",
        groupNames = {"group_1"})
    @Test
    public void testSortByPorCompanyAndClaimed()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyIn("key5")
            .orderByPrimaryObjectReferenceCompany(desc)
            .orderByClaimed(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            // System.out.println("porCompany: " + taskSummary.getPrimaryObjRef().getCompany() + ", claimed: "
            // + taskSummary.getClaimed());
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getCompany().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getCompany()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group2"})
    @Test
    public void testSortByWbKeyPrioPorValueAndCompleted()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .stateIn(TaskState.READY)
            .orderByWorkbasketKey(null)
            .workbasketKeyIn("key5")
            .orderByPriority(desc)
            .orderByPrimaryObjectReferenceValue(asc)
            .orderByCompleted(desc)
            .list();

        assertThat(results.size(), equalTo(22));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getWorkbasketSummary().getKey().compareToIgnoreCase(
                    previousSummary.getWorkbasketSummary().getKey()) >= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group2"})
    @Test
    public void testSortBpIdClassificationIdDescriptionAndPorType()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .stateIn(TaskState.READY)
            .workbasketKeyIn("key5")
            .orderByBusinessProcessId(asc)
            .orderByClassificationKey(null)
            .orderByPrimaryObjectReferenceType(SortDirection.DESCENDING)
            .list();

        assertThat(results.size(), equalTo(22));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getBusinessProcessId().compareToIgnoreCase(
                    previousSummary.getBusinessProcessId()) >= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }
}