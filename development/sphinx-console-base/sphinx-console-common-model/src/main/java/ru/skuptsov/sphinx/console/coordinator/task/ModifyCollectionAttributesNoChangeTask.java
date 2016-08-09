package ru.skuptsov.sphinx.console.coordinator.task;

import ru.skuptsov.sphinx.console.coordinator.model.SphinxProcessType;
import ru.skuptsov.sphinx.console.coordinator.task.state.chain.Chain;

/**
 * Created with IntelliJ IDEA.
 * User: Crow
 * Date: 26.08.14
 * Time: 4:55
 * To change this template use File | Settings | File Templates.
 */
public class ModifyCollectionAttributesNoChangeTask extends ModifyCollectionAttributesTask {
    public static final TaskName TASK_NAME = TaskName.MODIFY_COLLECTION_ATTRIBUTES_NO_CHANGE;
    private static final Chain CHAIN = Chain.MODIFY_COLLECTION_ATTRIBUTES_NO_CHANGE_CHAIN;

    public ModifyCollectionAttributesNoChangeTask() {
        setSphinxProcessType(SphinxProcessType.SEARCHING);
    }

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public Chain getChain() {
        return CHAIN;
    }
}
