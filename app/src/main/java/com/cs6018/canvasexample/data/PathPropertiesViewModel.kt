package com.cs6018.canvasexample.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.lifecycle.ViewModel
import com.cs6018.canvasexample.utils.MotionEvent
import com.cs6018.canvasexample.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PathPropertiesViewModel : ViewModel() {
//    enum class EraseDrawToggleButtonIconEnum(val iconResource: Int) {
//        ERASE_MODE_ICON(R.drawable.ink_eraser_off),
//        DRAW_MODE_ICON(R.drawable.ink_eraser)
//    }

//    enum class EraseDrawToggleButtonTextEnum(val text: String) {
//        ERASE_MODE_TEXT("Draw"),
//        DRAW_MODE_TEXT("Erase")
//    }

    private val _hexColorCode = MutableStateFlow("#ffffff")
    val hexColorCode: StateFlow<String> = _hexColorCode.asStateFlow()

    private val _currentPathProperty = MutableStateFlow(PathProperties())
    val currentPathProperty: StateFlow<PathProperties> = _currentPathProperty.asStateFlow()

//    private val _eraseDrawToggleButtonIcon =
//        MutableStateFlow(EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON)
//    val eraseDrawToggleButtonIcon: StateFlow<EraseDrawToggleButtonIconEnum> =
//        _eraseDrawToggleButtonIcon.asStateFlow()

//    private val _eraseDrawToggleButtonText =
//        MutableStateFlow(EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT)
//    val eraseDrawToggleButtonText: StateFlow<EraseDrawToggleButtonTextEnum> =
//        _eraseDrawToggleButtonText.asStateFlow()

    val paths = mutableStateListOf<Pair<Path, PathProperties>>()
    val pathsUndone = mutableStateListOf<Pair<Path, PathProperties>>()
    val motionEvent = mutableStateOf(MotionEvent.Idle)
    val currentPosition = mutableStateOf(Offset.Unspecified)
    val previousPosition = mutableStateOf(Offset.Unspecified)
    val currentPath = mutableStateOf(Path())

    fun reset() {
        _hexColorCode.value = "#ffffff"
        _currentPathProperty.value = PathProperties()
//        _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON
//        _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT
        paths.clear()
        pathsUndone.clear()
        motionEvent.value = MotionEvent.Idle
        currentPosition.value = Offset.Unspecified
        previousPosition.value = Offset.Unspecified
        currentPath.value = Path()
    }

    fun updateHexColorCode(newHexColorCode: Color) {
        _hexColorCode.value = newHexColorCode.toString()
    }

    fun updateMotionEvent(newMotionEvent: MotionEvent) {
        motionEvent.value = newMotionEvent
    }

    fun updateCurrentPosition(newPosition: Offset) {
        currentPosition.value = newPosition
    }

    fun updatePreviousPosition(newPosition: Offset) {
        previousPosition.value = newPosition
    }

    fun updateCurrentPath(newPath: Path) {
        currentPath.value = newPath
    }

    fun updateCurrentPathProperty(newProperty: PathProperties) {
        _currentPathProperty.value = newProperty
    }

    fun updateCurrentPathProperty(
        newColor: Color? = null,
        newStrokeWidth: Float? = null,
        newStrokeCap: StrokeCap? = null,
        newStrokeJoin: StrokeJoin? = null
    ) {
        val newProperty = currentPathProperty.value.copy()
        newColor?.let { newProperty.color = it }
        newStrokeWidth?.let { newProperty.strokeWidth = it }
        newStrokeCap?.let { newProperty.strokeCap = it }
        newStrokeJoin?.let { newProperty.strokeJoin = it }
        _currentPathProperty.value = newProperty
    }

//    fun isEraseMode(): Boolean {
//        return currentPathProperty.value.eraseMode
//    }

//    fun toggleDrawMode() {
//        currentPathProperty.value.eraseMode = !currentPathProperty.value.eraseMode
//        if (currentPathProperty.value.eraseMode) {
//            _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.ERASE_MODE_ICON
//            _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.ERASE_MODE_TEXT
//        } else {
//            _eraseDrawToggleButtonIcon.value = EraseDrawToggleButtonIconEnum.DRAW_MODE_ICON
//            _eraseDrawToggleButtonText.value = EraseDrawToggleButtonTextEnum.DRAW_MODE_TEXT
//        }
//    }

    fun undoLastAction() {
        if (paths.isNotEmpty()) {
            val lastItem = paths.removeAt(paths.size - 1)
            pathsUndone.add(lastItem)
        }
    }
}
