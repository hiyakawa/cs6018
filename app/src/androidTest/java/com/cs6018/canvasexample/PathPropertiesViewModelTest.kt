package com.cs6018.canvasexample

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cs6018.canvasexample.model.PathProperties
import com.cs6018.canvasexample.model.PathPropertiesViewModel
import com.cs6018.canvasexample.gesture.MotionEvent

import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PathPropertiesViewModelTest {
    @Test
    fun testInitialization() {
        val vm = PathPropertiesViewModel()
        Assert.assertSame("#ffffff", vm.hexColorCode.value)
        Assert.assertEquals(MotionEvent.Idle, vm.motionEvent.value);
        Assert.assertEquals(Offset.Unspecified, vm.currentPosition.value)
        Assert.assertEquals(Offset.Unspecified, vm.previousPosition.value)
    }

    @Test
    fun testReset() {
        val vm = PathPropertiesViewModel()
        vm.updateHexColorCode(Color.White)
        vm.updateMotionEvent(MotionEvent.Up)
        vm.reset()
        Assert.assertSame("#ffffff", vm.hexColorCode.value)
    }

    @Test
    fun testUpdateHexColorCode() {
        val vm = PathPropertiesViewModel()
        val before = vm.hexColorCode.value
        vm.updateHexColorCode(Color.White)
        Assert.assertNotSame(before, vm.hexColorCode.value)
    }

    @Test
    fun testUpdateMotionEvent() {
        val vm = PathPropertiesViewModel()
        val before = vm.motionEvent.value
        vm.updateMotionEvent(MotionEvent.Down)
        Assert.assertNotSame(before, vm.motionEvent.value)
    }

    @Test
    fun testUpdateCurrentPathProperty() {
        val vm = PathPropertiesViewModel()
        val oldProperty = vm.currentPathProperty.value
        vm.updateCurrentPathProperty(Color.Blue, 10f, StrokeCap.Butt, StrokeJoin.Miter)
        val newProperty = vm.currentPathProperty.value
        Assert.assertNotSame(oldProperty.color, newProperty.color)
        Assert.assertNotSame(oldProperty.strokeWidth, newProperty.strokeWidth)
        Assert.assertNotSame(oldProperty.strokeCap, newProperty.strokeCap)
        Assert.assertNotSame(oldProperty.strokeJoin, newProperty.strokeJoin)
    }
}