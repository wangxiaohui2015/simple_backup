# Simple Backup
This is a simple project used to backup files based on AES-256, all informations are hided, includes file name, path, directory, etc.


## Development Environment
- JDK: 17, min version >= 1.8.0_162
- Maven: 3.8.1
- STS: 4.18.1.RELEASE
- OS: Ubuntu 21.04


## Build
```
git@github.com:wangxiaohui2015/simple_backup.git
mvn clean package
```

After building, below is the location of target libraries and configuration files,

- Backup: `<Backup>/target/release`
- Restore: `<Restore>/target/release`


## Backup

#### Edit Configuration File
Go to `<Backup>/target/release`, edit configuration file conf/backup.json,

```
{
    "thread": 3,
    "key": "changeme",
    "backups": [
        {
            "src": "",
            "dest": ""
        }
    ]
}

```

- thread: How many threads will be used for backup.
- key: The password used for encryption.
- backups: The source directory to be backup and destination directory to store backup files.

#### Start Backup
Go to `<Backup>/target/release`, run script `./backup.sh` to start backup.


## Restore
Go to `<Restore>/target/release`, run below command to start restore,

`java -jar SimpleBackup-restore-3.0.0-RELEASE-jar-with-dependencies.jar`

