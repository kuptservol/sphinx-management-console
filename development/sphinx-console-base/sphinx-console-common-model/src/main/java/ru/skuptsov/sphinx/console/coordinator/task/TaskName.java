package ru.skuptsov.sphinx.console.coordinator.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lnovikova on 14.09.2015.
 */
public enum TaskName {
    REBUILD_COLLECTION("rebuildCollection"),
    ADD_COLLECTION("addCollection"),
    ADD_DISTRIBUTED_COLLECTION("addDistributedCollection"),
    CREATE_DISTRIBUTED_REPLICA("createDistributedReplica"),
    CREATE_REPLICA("createReplica"),
    CREATE_SNIPPET_CONFIGURATION("createSnippetConfiguration"),
    DELETE_COLLECTION("deleteCollection"),
    DELETE_FULL_INDEX_DATA("deleteFullIndexData"),
    DELETE_PROCESS_FROM_SERVER("deleteProcessFromServer"),
    DELETE_SNIPPET_CONFIGURATION("deleteSnippetConfiguration"),
    EDIT_SNIPPET_CONFIGURATION("editSnippetConfiguration"),
    FULL_REBUILD_SNIPPET("fullRebuildSnippet"),
    MAKE_COLLECTION_FULL_REBUILD_APPLY("makeCollectionFullRebuildApply"),
    MAKE_COLLECTION_FULL_REBUILD_INDEX("makeCollectionFullRebuildIndex"),
    MERGE_COLLECTION("mergeCollection"),
    MODIFY_COLLECTION_ATTRIBUTES_NO_CHANGE("modifyCollectionAttributesNoChange"),
    MODIFY_COLLECTION_ATTRIBUTES_RESTORE_FAILURE("modifyCollectionAttributesRestoreFailure"),
    MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES_RESTORE_FAILURE("modifyDistributedCollectionAttributesRestoreFailure"),
    MODIFY_DISTRIBUTED_COLLECTION_ATTRIBUTES("modifyDistributedCollectionAttributes"),
    MODIFY_DISTRIBUTED_REPLICA_PORT("modifyDistributedReplicaPort"),
    MODIFY_REPLICA_PORT("modifyReplicaPort"),
    MOVE_PROCESS_TO_SERVER("moveProcessToServer"),
    REBUILD_SNIPPETS("rebuildSnippets"),
    RELOAD_DISTRIBUTED_COLLECTION("reloadDistributedCollection"),
    REMOVE_REPLICA("removeReplica"),
    START_ALL_PROCESSES("startAllProcesses"),
    START_PROCESS("startProcess"),
    STOP_ALL_PROCESSES("stopAllProcesses"),
    STOP_FULL_INDEXING("stopFullIndexing"),
    STOP_INDEXING("stopIndexing"),
    STOP_PROCESS("stopProcess"),
    STOP_REBUILD_SNIPPETS("stopRebuildSnippets")
    ;

    private String title;

    TaskName(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString(){
        return title;
    }

    public static List<String> getTitles(List<TaskName> taskNames){
        List<String> result = new ArrayList<String>();
        for(TaskName taskName : taskNames){
            result.add(taskName.title);
        }
        return result;
    }
}
