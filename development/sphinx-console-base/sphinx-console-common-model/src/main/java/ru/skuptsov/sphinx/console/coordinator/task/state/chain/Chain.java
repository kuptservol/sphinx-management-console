package ru.skuptsov.sphinx.console.coordinator.task.state.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.skuptsov.sphinx.console.coordinator.annotation.ParallelSubflow;
import ru.skuptsov.sphinx.console.coordinator.exception.ApplicationException;
import ru.skuptsov.sphinx.console.coordinator.exception.ChainException;
import ru.skuptsov.sphinx.console.coordinator.task.state.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public enum Chain {

    ADD_COLLECTION_CHAIN(AddCollectionStateDB.class, AddCollectionState.class),
    MAKE_COLLECTION_FULL_REBUILD_CHAIN(MakeCollectionFullRebuildIndexStateDB.class, MakeCollectionFullRebuildIndexState.class),
    MAKE_COLLECTION_FULL_REBUILD_APPLY_CHAIN(MakeCollectionFullRebuildApplyIndexState.class, MakeCollectionFullRebuildApplySearchState.class),
    DELETE_COLLECTION_CHAIN(DeleteCollectionState.class, DeleteSnippetConfigurationStateDB.class, DeleteCollectionStateDB.class),
    DELETE_COLLECTION_DB_PART_CHAIN(DeleteCollectionStateDB.class),
    DELETE_PROCESS_FROM_SERVER_CHAIN(DeleteProcessFromServerState.class),
    MODIFY_COLLECTION_ATTRIBUTES_NO_CHANGE_CHAIN(ModifyCollectionAttributesNoChangeStateDB.class, ModifyCollectionAttributesNoChangeState.class),
    MODIFY_COLLECTION_ATTRIBUTES_RESTORE_FAILURE_CHAIN(ModifyCollectionAttributesRestoreFailureState.class, ModifyCollectionAttributesStateDB.class, AddCollectionState.class),
    MOVE_PROCESS_TO_SERVER_CHAIN(MoveProcessToServerState.class),
    REBUILD_COLLECTION_CHAIN(RebuildCollectionState.class),
    REBUILD_COLLECTION_PARALLEL_CHAIN("REBUILD_COLLECTION_PARALLEL_CHAIN", RebuildCollectionState.class),
    START_PROCESS_CHAIN(StartProcessState.class),
    START_ALL_PROCESSES_CHAIN(StartAllProcessesState.class),
    START_ALL_PROCESSES_PARALLEL_CHAIN("START_ALL_PROCESSES_PARALLEL_CHAIN", StartAllProcessesState.class),
    STOP_INDEXING_CHAIN(StopIndexingState.class),
    STOP_PROCESS_CHAIN(StopProcessState.class),
    STOP_ALL_PROCESSES_PARALLEL_CHAIN("STOP_ALL_PROCESSES_PARALLEL_CHAIN", StopAllProcessesState.class),
    STOP_ALL_PROCESSES_CHAIN(StopAllProcessesState.class),
    CREATE_REPLICA_CHAIN(CreateReplicaStateDB.class, CreateReplicaState.class),
    REMOVE_REPLICA_CHAIN(RemoveReplicaState.class, RemoveReplicaStateDB.class),
    MODIFY_REPLICA_PORT_CHAIN(ModifyReplicaPortState.class, ModifyReplicaPortStateDB.class),
    MERGE_COLLECTION_CHAIN(MergeCollectionState.class),
    DELETE_FULL_INDEX_DATA_CHAIN(DeleteFullIndexDataState.class),
    ADD_DISTRIBUTED_COLLECTION_CHAIN(AddDistributedCollectionStateDB.class, AddDistributedCollectionState.class),
    RELOAD_DISTRIBUTED_COLLECTION_CHAIN(ReloadDistributedCollectionStateDB.class, ReloadDistributedCollectionState.class),
    CREATE_DISTRIBUTED_REPLICA_CHAIN(CreateDistributedReplicaStateDB.class, CreateDistributedReplicaState.class),
    MODIFY_DISTRIBUTED_REPLICA_PORT_CHAIN(ModifyDistributedReplicaPortState.class, ModifyDistributedReplicaPortStateDB.class),
    MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES_CHAIN(ModifyDistributedCollectionAttributesStateDB.class, ModifyDistributedCollectionAttributesState.class),
    MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES_RESTORE_FAILURE_CHAIN(ModifyDistributedCollectionAttributesRestoreFailureState.class, ModifyDistributedCollectionAttributesStateDB.class, AddDistributedCollectionState.class),
    CREATE_SNIPPET_CONFIGURATION_CHAIN(CreateSnippetConfigurationStateDB.class),
    DELETE_SNIPPET_CONFIGURATION_CHAIN(DeleteSnippetConfigurationStateDB.class, DeleteSnippetConfigurationState.class),
    REBUILD_SNIPPETS_CHAIN(RebuildSnippetsState.class),
    EDIT_SNIPPET_CONFIGURATION_CHAIN(EditSnippetConfigurationStateDB.class),
    FULL_REBUILD_SNIPPET_CHAIN(MakeSnippetsFullRebuildState.class),
    STOP_REBUILD_SNIPPETS_CHAIN(StopRebuildSnippetsState.class);

//    private Set<TaskState> chainStates = new LinkedHashSet<TaskState>();

//    public Set<TaskState> getChainStates() {
//        return chainStates;
//    }

    public static void main(String[] args){
        Chain.ADD_COLLECTION_CHAIN.isFirstInTransaction(AddCollectionState.SET_COORDINATOR_SEARCH);
    }

    private Map<Class<? extends TaskState>, Integer> orderNumberByState = new HashMap<Class<? extends TaskState>, Integer>();
    private Map<Integer, Class<? extends TaskState>> stateByOrderNumber = new HashMap<Integer, Class<? extends TaskState>>();

    private String subChainName;
    private static final Logger logger = LoggerFactory.getLogger(Chain.class);

    private Chain(String subChainName, Class<? extends TaskState>... chainPartClasses) {
        this(chainPartClasses);
        this.subChainName = subChainName;
    }

    private Chain(Class<? extends TaskState>... chainPartClasses) {

        int i = 0;
        for(Class<? extends TaskState> chainPartClass : chainPartClasses) {
            orderNumberByState.put(chainPartClass, i);
            stateByOrderNumber.put(i, chainPartClass);

            i++;
        }
    }

    public List<TaskState> getChainStates(TaskState curState) {
        return getChainStates(((Enum) curState).getDeclaringClass());
    }

    public List<TaskState> getChainStates(int stateIndex) {
        return getChainStates(stateByOrderNumber.get(stateIndex));
    }

    public List<TaskState> getChainStates(Class<? extends TaskState> currentStateClass) {

        List<TaskState> states = new LinkedList<TaskState>();

        for (TaskState state : Arrays.asList(currentStateClass.getEnumConstants())) {
            try {
                Field field = Class.forName(currentStateClass.getName()).getField(state.getStateName());

                Annotation parallelSubflow = field.getAnnotation(ParallelSubflow.class);
                if (subChainName == null && parallelSubflow == null ||
                        subChainName != null && parallelSubflow != null) {
                    if(states.contains(state)){
                        throw new ChainException("Task chain has duplicate states.");
                    }
                    states.add(state);
                }
            } catch (Throwable e) {
                logger.error("Error during getChainStates",e);
                throw new ApplicationException(e);
            }
        }

        return states;
    }

    public TaskState getNextState(TaskState curState) {
        TaskState nextState = null;
        // list there can contain only unique values, so it's safe to use indexOf() (it can return only one index)
        List<TaskState> chainStates = new ArrayList<TaskState>(getChainStates(curState));
        int curStateIndex = chainStates.indexOf(curState);
        if(curStateIndex + 1 < chainStates.size()) {
            nextState = chainStates.get(curStateIndex + 1);
        } else {
            if (stateByOrderNumber.containsKey(orderNumberByState.get(((Enum) curState).getDeclaringClass()) + 1)) {
                nextState = Arrays.asList(stateByOrderNumber.get(orderNumberByState.get(((Enum) curState).getDeclaringClass()) + 1).getEnumConstants()).get(0);
            } else {
                nextState = TaskState.COMPLETED;
            }
        }

        return nextState;
    }

    public TaskState getFirstState(){
        List<TaskState> chainStates = new ArrayList<TaskState>(getChainStates(0));
        return chainStates.get(0);
    }

    public boolean isFirstInTransaction(TaskState state) {
        if (!(state instanceof TransactionalTaskState)) {
            return false;
        } else {
            List<TaskState> chainStates = new ArrayList<TaskState>(getChainStates(0));
            if (chainStates.indexOf(state) == 0) {
                return true;
            }
            if (chainStates.indexOf(state) - 1 >= 0 && chainStates.get(chainStates.indexOf(state) - 1) instanceof TransactionalTaskState) {
                return false;
            } else {
                return true;
            }
        }
    }

}
