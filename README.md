# File Picker

[![](https://jitpack.io/v/tabasumu/file-picker.svg)](https://jitpack.io/#tabasumu/file-picker)

This is a library that help in picking a single or multiple files of your choosing.

# Installation

### Kotlin DSL

```
implementation("com.github.tabasumu:file-picker:$version")
```

### Groovy

```
implementation 'com.github.tabasumu:file-picker:$version'
```

# Usage

```

    // MULTIPLE FILES
    FilePicker
        .Builder(activity : FragmentActivity)
        .pick { list : List<Pair<Uri, File>> ->
            // do anyhting with list
        }
        
    // SINGLE FILE
    
     FilePicker
        .Builder(activity : FragmentActivity)
        .pickSingle { uri : Uri, file : File ->
            // do anyhting with uri or file
        }

```
