# FragmentLayout
fragment Container View

## Usage

Add it in your root build.gradle at the end of repositories:
``` groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency
``` groovy
	implementation 'com.github.cyynf:FragmentLayout:1.0.2'
```
Use FragmentLayout
``` xml
<cpf.fragment.layout.FragmentLayout
        android:id="@+id/delayFragmentLayout"
        android:name="cpf.fragment.demo.SingleFragment1"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginTop="10dp"
        android:animateLayoutChanges="true"
        app:autoLoad="true"
        app:delayDuration="1000" />
```