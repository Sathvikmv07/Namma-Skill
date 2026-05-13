package com.nammaskill.app.ui.apply

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class ApplyBottomSheetArgs(
  public val courseId: String,
  public val courseName: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("courseId", this.courseId)
    result.putString("courseName", this.courseName)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("courseId", this.courseId)
    result.set("courseName", this.courseName)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): ApplyBottomSheetArgs {
      bundle.setClassLoader(ApplyBottomSheetArgs::class.java.classLoader)
      val __courseId : String?
      if (bundle.containsKey("courseId")) {
        __courseId = bundle.getString("courseId")
        if (__courseId == null) {
          throw IllegalArgumentException("Argument \"courseId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseId\" is missing and does not have an android:defaultValue")
      }
      val __courseName : String?
      if (bundle.containsKey("courseName")) {
        __courseName = bundle.getString("courseName")
        if (__courseName == null) {
          throw IllegalArgumentException("Argument \"courseName\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseName\" is missing and does not have an android:defaultValue")
      }
      return ApplyBottomSheetArgs(__courseId, __courseName)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): ApplyBottomSheetArgs {
      val __courseId : String?
      if (savedStateHandle.contains("courseId")) {
        __courseId = savedStateHandle["courseId"]
        if (__courseId == null) {
          throw IllegalArgumentException("Argument \"courseId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseId\" is missing and does not have an android:defaultValue")
      }
      val __courseName : String?
      if (savedStateHandle.contains("courseName")) {
        __courseName = savedStateHandle["courseName"]
        if (__courseName == null) {
          throw IllegalArgumentException("Argument \"courseName\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"courseName\" is missing and does not have an android:defaultValue")
      }
      return ApplyBottomSheetArgs(__courseId, __courseName)
    }
  }
}
