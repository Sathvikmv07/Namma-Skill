package com.nammaskill.app.ui.detail

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nammaskill.app.R
import kotlin.Int
import kotlin.String

public class CourseDetailFragmentDirections private constructor() {
  private data class ActionDetailToApply(
    public val courseId: String,
    public val courseName: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_detail_to_apply

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("courseId", this.courseId)
        result.putString("courseName", this.courseName)
        return result
      }
  }

  public companion object {
    public fun actionDetailToApply(courseId: String, courseName: String): NavDirections =
        ActionDetailToApply(courseId, courseName)
  }
}
