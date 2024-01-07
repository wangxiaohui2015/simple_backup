# simple_backup
This project is used to backup files based on AES-256.

### Environment
- JDK: 17, min version >= 1.8.0_162
- Maven: 3.8.1
- STS: 4.18.1.RELEASE
- OS: Ubuntu 21.04

### Build
```
git@github.com:wangxiaohui2015/simple_backup.git
mvn clean package
```
After building, go to 'target/release' folder to check configuration files and startup scripts.

Update conf/backup_config.properties and conf/service_config.properties according to your settings.

Then execute backup.sh to start backup or restore.sh to start restore.
