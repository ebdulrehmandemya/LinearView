# Linear View

A powerful android library coded in kotlin. It's like recyclerview but eliminated some steps of implementation.

## Features

1. No more adapter implemantation
2. Act like single widget
3. Support muli-layout
4. Add, insert, edit, remove by Item id (recommended) and item position without headache
5. Support Load more
6. Support custom pages like empty, preload, errors, ...
7. Support vertical & horizontal orientation

## How to use

### #1: Add library to your project

- Add it in your root `build.gradle` (app level) at the end of repositories:

```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

- Add the dependency:

```gradle
    dependencies {
        ...
        implementation 'com.github.ebdulrehmandemya:LinearView:v5.0'
    }
```

> For other build files, see [here](https://jitpack.io/#ebdulrehmandemya/LinearView)

### #2: Implementation of `Item`

```kotlin
    class Note(val id: Long, val title: String) : Item {
        companion object {
            const val TYPE_ID = 1
            fun bind(item: ItemText, view: View, position: Int) {
                view.title.text = item.title
            }
        }
        override fun type(): Int = TYPE_ID  // required, must be unique per classe
        override fun id(): Long = id        // optional, should be unique per instance in the same type
    }
```

> Repeat this action for other classes that you want to use them in LinearView

### #3: In layout and code

- Add `org.dahatu.libs.linearview.LinearView` in layout

```xml
    <!-- activity_main.xml -->
    ...
    <org.dahatu.libs.linearview.LinearView
            android:id="@+id/dlv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    ...
```

- Somewhere in your code like `onCreate(...)`

```kotlin
    // MainActivity class
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        dlv.onManageListener(this)  // set a listener, see next section
        dlv.addItem(Note.createMock(id=1))  // add an item to LinearView

        val items = listOf(Note.createMock(id=2), Note.createMock(id=3))
        dlv.addItems(items)  // add a list of items to LinearView
    }
    ...
```

### #4: Implementation of `OnManageListener`

```kotlin
    class MainActivity : AppCompatActivity(), OnManageListener {
        ...
    }
```

- Override `OnManageListener` Methods

| Method         | Required | arguments                             | retrun                 | Description |
| -------------- | -------- | ------------------------------------- | ---------------------- | ----------- |
| layout         | Yes      | type : Int                            | @LayoutRes Int         | d           |
| onBind         | Yes      | item: Item, view: View, position: Int |                        | d           |
| preloadLayout  | No       |                                       | @LayoutRes Int? = null | d           |
| emptyLayout    | No       |                                       | @LayoutRes Int? = null | d           |
| loadMoreLayout | No       |                                       | @LayoutRes Int? = null | d           |
| hasMore        | No       |                                       | Boolean = false        | d           |
| onMore         | No       |                                       |                        | d           |
| onPageLayout   | No       | code: Int                             | @LayoutRes Int? = null | d           |
| onPageBind     | No       | code: Int, view: View                 |                        | d           |
