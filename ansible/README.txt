В комплекте несколько playbook'ов. Основный из них - sphinx-console.yaml - для установки sphinx-console'а с нуля.

Для работы всего это хозяйства нужен сервер с установленным и настроенным ansible.
В конфигах ansible должны быть прописаны хосты, на которые будет устанавливаться дистрибутив.
На ansible-сервере должны быть ssh ключи для доступа к хостам.

Пример конфига (/etc/ansible/hosts):
sphinx-console.ru
sphinx-consoletest.ru
sphinx-console01 ansible_ssh_port=5022 ansible_ssh_host=54.76.42.99 ansible_ssh_user=demo
sphinx-console02 ansible_ssh_port=6022 ansible_ssh_host=54.76.42.99 ansible_ssh_user=demo

Параметры:
    ansible_ssh_port - нужен в случае использования нестандартного ssh-порта для доступа к серверу
    ansible_ssh_host - если по указанному названию хоста IP сервера не резолвится
    ansible_ssh_user - если доступ к хосту осуществляется от пользователя, имя которого не совпадает с тем, от имени
                       которого запускается ansible

===================
Перед установкой надо подготовить стенд:
-------------------

> sudo visudo
  --- закомментировать requiretty
			
> sudo yum install http://dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm -y 
> sudo yum install mysql-community-server -y 
> sudo chkconfig --level 345 mysqld on
> sudo /etc/init.d/mysqld start
> mysql -u root -p
mysql> set PASSWORD=password('mysql');
mysql> flush privileges;
mysql> exit
			
sudo yum install mysqlclient16 -y
sudo yum install unixODBC -y
sudo yum install postgresql-libs -y

===================
Выполнить установку:
-------------------
> ansible-playbook sphinx-console.yaml --extra-vars "target=dev user=sphinx-console type=all use_own_sphinx=true sphinx-console_path=/u01/sphinx-console sphinx_base_path=/u01/sphinx-console/sphinx db_root_pass=mysql db_user=sphinx-console db_pass=sphinx-console webapp_host=54.76.42.99 webapp_port=8085 webapp_forwarded_port=10085" --ask-sudo-pass --ask-pass

Параметры:
    --ask-sudo-pass - если пользователь на хосте отличается от root, придется руками ввести пароль для sudo
    --extra-vars    - переменные инсталляции, подробности - внутри yaml-файлов. Минимально необходимые переменные - target и type

Важные переменные:
    sphinx-console_path - куда устанавливать приложение
    sphinx_base_path - где будет размещен Сфинкс со всеми файлами (конфиги, логи, индексы и тп)
    db_root_pass - пароль рута от MySQL
    db_user, db_pass - реквизиты подключения к координатора к своей собственной БД (пользователь будет создан в процессе установки).
    webapp_host, webapp_port - URL/IP и порт, на котором будет работать web-интерсейс
    webapp_forwarded_port - порт, проброшенные наружу, если вдруг webapp_port будет закрыт

    action - что нужно сделать. По умолчанию - "install". Второе возможное значение - "delete". Для выполнения delete нужны такие же параметры, с какими была выполнена установка.

===================
После установки:
(вместо <HOST_IP> прописать ip машины, на которую ставится sphinx-console)
-------------------
# Подключиться под пользователем sphinx-console, настроить ssh-ключи
> ssh-keygen -t rsa -f ~/.ssh/id_rsa -N ''
> ssh-keyscan -H <HOST_IP> >> ~/.ssh/known_hosts
> ssh-copy-id <HOST_IP>
				




===================
Работа в консоли после установки:
-------------------
/etc/init.d/sphinx-console-agent - сервис агента, команды - start / stop / restart / status
/etc/init.d/sphinx-console-coordinator - сервис агента, команды - start / stop / restart / status


===================
Web-интерфейс:
-------------------
http://<HOST_IP>:8080/sphinx-console-web/sphinx-console/