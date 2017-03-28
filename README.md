# RippleView
A view similar ripple effect.

![](https://github.com/huang125/RippleView/blob/master/screenshots/demo.gif)

#### Gradle
```
dependencies {
  ...
  compile 'com.huang.rippleview:rippleview:1.0.1'
}
```
## Usage
```
 <rippleview.huang.com.RippleView
            android:id="@+id/ripple_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:ripple_amount="3"
            app:ripple_color="@android:color/holo_blue_light"
            app:ripple_duration="3000"
            app:ripple_scale="4.0"
            app:ripple_strokeWidth="2.0"
            app:ripple_style="fillRipple" />
```
### Style
You can change the RippleView style to fill or stroke. 
* .xml
```
app:ripple_style="fillRipple"
//or
app:ripple_style="strokeRipple"
```
* code
```
RippleView.setRippleStyle(RippleStyle.FILL);
//or
RippleView.setRippleStyle(RippleStyle.STROKE);
```
### Color
You can change the RippleView color as you like.
* .xml
```
app:ripple_color="@android:color/holo_blue_light"
```
* code
```
RippleView.setRippleColor(R.color.blue);
```
### Amount
You can set any `int` parameter to change ripple amount. But i think you don't want to set it too big, because it's so bad experience.
* .xml
```
app:ripple_amount="3"
```
* code
```
RippleView.setRippleAmount(5);
```
### Scale
* .xml
```
app:ripple_scale="4.0"
```
* code
```
RippleView.setRippleScale(3.0);
```

### Duration
Duration is every ripple's scale time. Biger slower and smaller faster.
* .xml
```
app:ripple_duration="3000"
```
* code
```
RippleView.setRippleDuration(1000 * 5);
```
### StrokeWidth
When style is `RippleStyle.STROKE` can see this effect.
* .xml
```
app:ripple_strokeWidth="2.0"
```
* code
```
RippleView.setRippleStrokeWidth(4.0);
```

### License
```
Copyright 2017 Huang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
