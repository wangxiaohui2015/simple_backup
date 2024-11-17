# Simple Backup
This is a simple backup and restore project used to backup files based on AES-256, all informations are hided in backup files, includes file name, path, directory, etc.


## Requirement
- JDK: 17
- Apache Maven: 3.8.1
- OS: Linux/Windows


## Build
Before this step, make sure you have configured JDK and Maven successfully, then execute below scripts to build,

```
git clone https://github.com/wangxiaohui2015/simple_backup.git
cd simple_backup
mvn clean package
```

After building, below is the location of target libraries and configuration files,

- Backup: `<CODE_BASE>/backup/target/release`
- Restore: `<CODE_BASE>/restore/target/release`


## Backup

#### Edit Configuration File
Go to `<CODE_BASE>/backup/target/release`, edit configuration file conf/backup.json,

```
{
    "thread": 4,
    "key": "changeme",
    "enableChecksum": false,
    "backups": [
        {
            "src": "",
            "dest": "",
            "excludeFiles": [],
            "excludeDirs": []
        }
    ]
}
```

* thread: How many threads will be used for backup. Default is `4`, value range is `[1,256]`.
* key: The password used for encryption. Default is `changeme`, cannot be empty, need to change it for security reason.
* enableChecksum: If calculate source file checksum during backup. Default is `false`, may impact backup performance if set this value to `true`.
* backups: Backup items.
    * src: Source directory to be backup.
    * dest: Destination directory to store backup files.
    * excludeFiles: Exclude file names, supports regular expression.
    * excludeDirs: Exclude directory names, supports regular expression.

#### Start Backup
Go to `<CODE_BASE>/backup/target/release`, run script `backup.sh` or `backup.bat` to start backup tasks.


## Restore
Go to `<CODE_BASE>/restore/target/release`, run script `restore.sh` or `restore.bat` to start restore tasks.

Usage of restore command,

```
usage: ./restore.sh -s <source_dir> -d <destination_dir> [-t <threads> | -m <metadata,fake,restore>]
 -s,--source <arg>        Source folder path.
 -d,--destination <arg>   Destination folder path.
 -t,--threads <arg>       Threads number used for restore, default is 4.
 -m,--mode <arg>          Restore mode, value can be: [metadata, fake, restore], default is restore.
 -h,--help                Show help.
 -v,--version             Show version.
```


## Limitations
- Symbolic link: For symbolic link files, will backup real file instead of symbolic link, and will restore real file instead of symbolic link.
- Empty folder: Empty folder will be skipped during backup and restore.
