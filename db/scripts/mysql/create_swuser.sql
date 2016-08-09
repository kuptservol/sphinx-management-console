GRANT ALL ON sphinx-console.* TO swuser@'localhost' IDENTIFIED BY 'swpass';
GRANT ALL ON sphinx-console.* TO swuser@'%' IDENTIFIED BY 'swpass';
FLUSH PRIVILEGES;
