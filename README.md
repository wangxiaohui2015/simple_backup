# Simple Backup
This is a simple project used to backup files based on AES-256, all informations are hided, includes file name, path, directory, etc.


## Requirement
- JDK: 17, min version >= 1.8.0
- Apache Maven: 3.8.1
- OS: Linux/Windows


## Build
Before this step, make sure you have configured JDK and Maven successfully.

```
git clone https://github.com/wangxiaohui2015/simple_backup.git
cd simple_backup
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
    "enableChecksum": false,
    "backups": [
        {
            "src": "",
            "dest": ""
        }
    ]
}

```

- thread: How many threads will be used for backup. Default is 3.
- key: The password used for encryption. Default is changeme, need to change it for security reason.
- enableChecksum: If calculate source file checksum during backup. Default is false, may impact backup performance if set this value to true.
- backups: The source directory to be backup and destination directory to store backup files.

#### Start Backup
Go to `<Backup>/target/release`, run script `backup.sh` or `backup.bat` to start backup tasks.


## Restore
Go to `<Restore>/target/release`, run script `restore.sh` or `restore.bat` to start restore tasks.

Usage of restore command,

```
usage: ./restore.sh -s <source_dir> -d <destination_dir> [-t <threads> | -m <metadata,fake,restore>]
 -s,--source <arg>        Source folder path.
 -d,--destination <arg>   Destination folder path.
 -t,--threads <arg>       Threads number used for restore, default is 3.
 -m,--mode <arg>          Restore mode, value can be: [metadata, fake, restore], default is restore.
 -h,--help                Show help.
 -v,--version             Show version.
```

