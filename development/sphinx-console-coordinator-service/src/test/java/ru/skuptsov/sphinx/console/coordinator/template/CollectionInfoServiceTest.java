package ru.skuptsov.sphinx.console.coordinator.template;

import ru.skuptsov.sphinx.console.coordinator.model.CollectionInfoWrapper;
import ru.skuptsov.sphinx.console.coordinator.task.schedule.info.CollectionsInfoService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrey on 11.12.2014.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:sphinx.console-coordinator-service-context-test.xml"})
public class CollectionInfoServiceTest {

    //@Autowired
    private CollectionsInfoService collectionsInfoService;

    //@Resource
    protected ConcurrentHashMap<String, CollectionInfoWrapper> collectionsInfoMap;

    // sql example needs: collection_id = 1, process_id = 555
    //INSERT INTO `ACTIVITY_LOG` (`activity_log_id`,`task_name`,`date_time`,`collection_id`,`index_name`,`server_id`,
    //`server_name`,`process_id`,`process_type`,`start_time`,`end_time`,`operation_type`,`stage_status`,`exception_text`,`data`,`task_uid`,`task_start_time`,`task_end_time`,`task_status`)
    //VALUES
    //        (77777,'qqqq','2014-12-09 10:25:03',1,'collection1',1,'qqq',555,'FULL_INDEXING','2014-12-11 19:17:44','2014-12-09 10:25:03','COPY_FULL_INDEX_STUB','SUCCESS',NULL,NULL,'',NULL,NULL,NULL),
    //        (77778,'qqqq','2014-12-09 10:25:03',1,'collection1',1,'qqq',555,'FULL_INDEXING','2014-12-11 19:00:55','2014-12-09 10:25:03','COPY_FULL_INDEX_STUB','SUCCESS',NULL,NULL,'',NULL,NULL,NULL),
    //        (77779,'qqqq','2014-12-09 10:25:03',1,'collection1',1,'qqq',555,'FULL_INDEXING','2014-12-11 19:00:55','2014-12-09 10:25:03','START_INDEXING_INDEX','SUCCESS',NULL,NULL,'',NULL,NULL,NULL);

    //@Ignore
    //@Test
    public void collectionsInfoServiceTest() {
        collectionsInfoService.process();
        System.out.println(collectionsInfoMap);
    }

}
