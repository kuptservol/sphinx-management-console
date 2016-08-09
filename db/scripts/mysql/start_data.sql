#Стартовый sql для заполнения шаблонов по умолчанию
SET NAMES utf8;

USE sphinx-console;

#Создание начальных шаблонов;
INSERT into CONFIGURATION_TEMPLATE values(1, 'Base index template', '', 1, 0, 'INDEX', 'SIMPLE');
INSERT into CONFIGURATION_TEMPLATE values(2, 'Base search template', '', 1, 0, 'SEARCH', 'SIMPLE');
INSERT into CONFIGURATION_TEMPLATE values(3, 'Base configuration template', '', 1, 0, 'CONFIGURATION', 'SIMPLE');
INSERT into CONFIGURATION_TEMPLATE values(4, 'Base Distributed configuration template', '', 1, 0, 'CONFIGURATION', 'DISTRIBUTED');
INSERT into CONFIGURATION_TEMPLATE values(5, 'Base Distributed search template', '', 1, 0, 'SEARCH', 'DISTRIBUTED');

#Создание полей шаблона
#INDEX;
INSERT into CONFIGURATION_FIELDS values(1, 'lemmatizer_cache', '256M', 1, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(2, 'mem_limit', '512M', 1, null, null, '', null);

#SEARCH;
INSERT into CONFIGURATION_FIELDS values(5, 'max_filter_values', '8192', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(6, 'read_timeout', '5', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(7, 'max_packet_size', '16M', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(8, 'client_timeout', '200', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(9, 'unlink_old', '1', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(10, 'mva_updates_pool', '1M', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(11, 'seamless_rotate', '1', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(12, 'max_children', '0', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(13, 'max_batch_queries', '22', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(14, 'preopen_indexes', '0', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(15, 'thread_stack', '512K', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(16, 'max_filters', '512', 2, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(17, 'query_log_format', 'sphinxql', 2, null, null, '', null);

#CONFIGURATION;
INSERT into CONFIGURATION_FIELDS values(20, 'expand_keywords', '1', 3, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(21, 'min_infix_len', '3', 3, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(22, 'morphology', 'lemmatize_ru', 3, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(23, 'index_exact_words', '1', 3, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(24, 'wordforms', '/opt/sphinx-console/data/wordforms/ru.txt', 3, null, null, '', null);

/*DISTRIBUTED CONFIGURATION; expand_keywords, ha_strategy, global_idf*/
INSERT into CONFIGURATION_FIELDS values(25, 'ha_strategy', 'noerrors', 4, null, null, '', null);

#DISTRIBUTED SEARCH;
INSERT into CONFIGURATION_FIELDS values(26, 'max_filter_values', '8192', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(27, 'read_timeout', '5', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(28, 'max_packet_size', '16M', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(29, 'client_timeout', '200', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(30, 'unlink_old', '1', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(31, 'mva_updates_pool', '1M', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(32, 'seamless_rotate', '1', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(33, 'max_children', '0', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(34, 'max_batch_queries', '22', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(35, 'preopen_indexes', '0', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(36, 'thread_stack', '512K', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(37, 'max_filters', '512', 5, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(38, 'query_log_format', 'sphinxql', 5, null, null, '', null);

/*Additional simple configuration template*/
INSERT into CONFIGURATION_TEMPLATE values(6, 'One char search', 'Дает возможность искать по одной букве, например, "а*"', 0, 0, 'CONFIGURATION', 'SIMPLE');
INSERT into CONFIGURATION_FIELDS values(39, 'expand_keywords', '1', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(40, 'min_infix_len', '1', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(41, 'morphology', 'lemmatize_ru', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(42, 'index_exact_words', '1', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(43, 'wordforms', '/opt/sphinx-console/data/wordforms/ru.txt', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(44, 'charset_table', '0..9, english, russian', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(45, 'blend_chars', '-, _, %, U+002C, U+002E, @', 6, null, null, '', null);
INSERT into CONFIGURATION_FIELDS values(46, 'blend_mode', 'trim_none, skip_pure', 6, null, null, '', null);


#Создание локального сервера
#INSERT into SERVER values(1, '195.168.0.6', null, 'coordinator');
#INSERT into SERVER values(2, '192.168.189.3', null, 'agent');
#INSERT into SERVER values(3, '127.0.0.1', null, 'localhost');

#Создание админских процессов
#INSERT into ADMIN_PROCESS values(1, 'COORDINATOR', '8888', 1);
#INSERT into ADMIN_PROCESS values(2, 'SEARCH_AGENT', '8889', 2);
#INSERT into ADMIN_PROCESS values(3, 'INDEX_AGENT', '8889', 2);